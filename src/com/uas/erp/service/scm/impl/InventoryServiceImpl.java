package com.uas.erp.service.scm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.InventoryService;

@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {
	@Autowired
	private BaseDao baseDao;
	static final String StockTaking = "insert into StockTaking(st_id,st_code,st_type,st_date,st_recorder,st_indate,st_status,st_whcode,st_statuscode) values(?,?,?,sysdate,?,sysdate,?,?,?)";
	static final String StockTakingDetail = "insert into StockTakingDetail(std_id, std_stid, std_code,std_detno,std_prodcode,std_batchqty,std_actqty,std_price,std_purbatchqty,std_puractqty) select STOCKTAKINGDETAIL_SEQ.NEXTVAL,@std_stid,@std_code,rownum,pw_prodcode,pw_onhand,pw_onhand,pw_avprice,pw_purconhand,pw_purconhand from productwh where pw_whcode=? and nvl(pw_onhand,0)>0";
	static final String StockTakingDetailByCondition= "insert into StockTakingDetail(std_id, std_stid, std_code,std_detno,std_prodcode,std_batchqty,std_actqty,std_price) "
			+ "select STOCKTAKINGDETAIL_SEQ.NEXTVAL,@std_stid,@std_code,rownum,pw_prodcode,pw_onhand,pw_onhand,pw_avprice from (select pw_prodcode,pw_onhand,pw_avprice from productwh left join product on pw_prodcode=pr_code where pw_whcode=? and nvl(pw_onhand,0)>0 @con order by pw_prodcode)";
	static final String StockTakingDetailByBatch = "insert into StockTakingDetail(std_id, std_stid, std_code,std_detno,std_prodcode,std_batchqty,std_actqty,std_price,std_purbatchqty,std_puractqty,std_prodmadedate,std_validtime,std_batchcode) "
			+ "										select STOCKTAKINGDETAIL_SEQ.NEXTVAL,@std_stid,@std_code,rownum,ba_prodcode,ba_remain,ba_remain,ba_price,case when nvl(ba_purcrate,0)=0 then ba_remain else round(ba_remain/BA_PURCRATE,8) end,"
			+ "										case when nvl(ba_purcrate,0)=0 then ba_remain else round(ba_remain/BA_PURCRATE,8) end,ba_date,ba_validtime,ba_code from batch where ba_whcode=? and nvl(ba_remain,0)>0";
	
	@Override
	public String inventory(String method, String whcode) {
		String sql1 = "Select wh_code from WareHouse";
		String sql2 = "select wm_concat('盘点单号：'||st_code||'仓库编号:'||st_whcode) from StockTaking where nvl(st_statuscode,' ')='ENTERING' and to_char(st_date,'yyyymm')=to_char(sysdate,'yyyymm')";
		StringBuffer errorLog = new StringBuffer();
		//按批号产生盘点底稿
		boolean inventoryByBatch=baseDao.isDBSetting("Inventory","inventoryByBatch");
		//产生盘点底稿时略过已存在在录入单据的仓库
		boolean inventoryIgnoreEntring = baseDao.isDBSetting("Inventory", "inventoryIgnoreEntring");
		if (whcode != null && !"".equals(whcode) ) {
			if(whcode.contains("#")){
				whcode = whcode.replaceAll("#", "','");
			}
			sql1 = sql1 + " where wh_code in ('" + whcode + "') ";
		}
		sql2 = sql2 + " and st_whcode in (" + sql1 + ")";
		String dets = baseDao.getJdbcTemplate().queryForObject(sql2, String.class);
		if (dets != null) {
			if(inventoryIgnoreEntring){
				errorLog.append("当前月份同一仓库已存在[在录入]的盘点单!</br>"+dets+"<hr>");
			}else{
				BaseUtil.showError("当前月份同一仓库已存在[在录入]的盘点单，不允许进行盘点操作!</br>"+dets+"<hr>");
			}
		}
		String sql3 = "select wm_concat(wh_code) from WareHouse where not exists (select 1 from productwh where pw_whcode = wh_code and nvl(pw_onhand,0) > 0 ) and wh_code in ("+sql1+")";
		if(inventoryByBatch){
			sql3 = "select wm_concat(wh_code) from WareHouse where not exists (select 1 from batch where ba_whcode = wh_code and nvl(ba_remain,0) > 0 ) and wh_code in ("+sql1+")";
		}
		dets = baseDao.getJdbcTemplate().queryForObject(sql3, String.class);
		if(dets != null){
			errorLog.append("编号："+dets+"仓库库存为空，无需生成盘点底稿。<hr>");
		}
		if(sql1.indexOf("where")<0){
			sql1 = sql1+" where ";
		}else{
			sql1 = sql1+" and ";
		}
		if(inventoryByBatch){// 产生盘点底稿，库存为0时不生产单据
			sql1 = sql1+ " exists (select 1 from batch where ba_whcode = wh_code and nvl(ba_remain,0) > 0 ) ";
		}else{
			sql1 = sql1+ " exists (select 1 from productwh where pw_whcode = wh_code and nvl(pw_onhand,0) > 0 ) ";
		}
		if(inventoryIgnoreEntring){//产生盘点底稿时略过已存在在录入单据的仓库
			sql1 = sql1+" and not exists (select 1 from StockTaking where wh_code = st_whcode and nvl(st_statuscode,' ')='ENTERING' and to_char(st_date,'yyyymm')=to_char(sysdate,'yyyymm'))";
		}
		SqlRowList rs = baseDao.queryForRowSet(sql1);
		int j = 0;
		while (rs.next()) {
			int c=0;
			if(inventoryByBatch){
				 c = baseDao.getCountByCondition("batch", "ba_whcode='"
						+ rs.getString("wh_code") + "' and nvl(ba_remain,0)>0 order by ba_prodcode,ba_id");
			}else{
				 c = baseDao.getCountByCondition("productwh", "pw_whcode='"
						+ rs.getString("wh_code") + "' and nvl(pw_onhand,0)>0 order by pw_prodcode");				
			}
			if(c > 0) {
				j++;
				//主单据
				int stid = baseDao.getSeqId("STOCKTAKING_SEQ");
				String checkCode = baseDao.sGetMaxNumber("StockTaking", 2);
				baseDao.getJdbcTemplate().update(StockTaking, stid, checkCode, method,
								SystemSession.getUser().getEm_name(),
								BaseUtil.getLocalMessage("ENTERING"), rs.getString("wh_code"),
								"ENTERING");
				if(inventoryByBatch){
					//从表
					baseDao.getJdbcTemplate().update(StockTakingDetailByBatch.replace("@std_stid", String.valueOf(stid))
							.replace("@std_code", "'" + checkCode + "'"), rs.getString("wh_code"));
				}else{
					//从表
					baseDao.getJdbcTemplate().update(StockTakingDetail.replace("@std_stid", String.valueOf(stid))
							.replace("@std_code", "'" + checkCode + "'"), rs.getString("wh_code"));
				}				
			}
		}
		if(j > 0){
			if(errorLog.length()>0){
				return "全部物料盘点成功,共产生" + j + "张盘点单据!<hr>"+errorLog.toString();
			}else
				return "全部物料盘点成功,共产生" + j + "张盘点单据!";
		}
		
		return errorLog.toString();
	}
	@Override
	public String inventoryByCondition(String method, String whcode,
			String condition) {
		String sql1 = "Select wh_code from WareHouse";
		if (whcode != null && !"".equals(whcode) ) {
			sql1 = sql1 + " where wh_code in (" + whcode + ")";
		}
		SqlRowList rs = baseDao.queryForRowSet(sql1);
		int j = 0;
		String con="";
		if(condition != null && !"".equals(condition)){
			con=" and "+condition;
		}
		while (rs.next()) {
			int c = baseDao.getCount("select count(1) from productwh left join product on pr_code=pw_prodcode where pw_whcode='"+ rs.getString("wh_code") + "' "
					+ "and nvl(pw_onhand,0)>0 "+con);
			if(c > 0) {
				j++;
				//主单据
				int stid = baseDao.getSeqId("STOCKTAKING_SEQ");
				String checkCode = baseDao.sGetMaxNumber("StockTaking", 2);
				baseDao.getJdbcTemplate().update(StockTaking, stid, checkCode, method,
								SystemSession.getUser().getEm_name(),
								BaseUtil.getLocalMessage("ENTERING"), rs.getString("wh_code"),
								"ENTERING");
				//从表
				baseDao.getJdbcTemplate().update(StockTakingDetailByCondition.replace("@std_stid", String.valueOf(stid))
						.replace("@std_code", "'" + checkCode + "'").replace("@con", con), rs.getString("wh_code"));
			}
		}
		return "全部物料盘点成功,共产生" + j + "张盘点单据!";
	}
}
