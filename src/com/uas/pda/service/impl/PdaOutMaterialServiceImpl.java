package com.uas.pda.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.APIErrorException;
import com.uas.erp.core.exception.APIErrorException.APIErrorCode;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.pda.dao.PdaCommonDao;
import com.uas.pda.service.PdaBatchService;
import com.uas.pda.service.PdaMsdService;
import com.uas.pda.service.PdaOutMaterialService;


@Service("pdaOutMaterialServiceImpl")
public class PdaOutMaterialServiceImpl implements PdaOutMaterialService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PdaCommonDao pdaCommonDao;
	@Autowired
	private PdaMsdService pdaMsdService;
	@Autowired 
	private  VerifyApplyDao verifyApplyDao;
	@Autowired
	private PdaBatchService  pdaBatchService;
	
	@Override
	public List<Map<String, Object>> fuzzySearch(String inoutNo, String whcode) {
		SqlRowList rs;
		inoutNo = inoutNo.toUpperCase();
		rs = baseDao
				.queryForRowSet("select * from (select  pi_inoutno from prodinout left join documentsetup  on pi_class=ds_name where pi_inoutno like ? "
					+ " and (ds_inorout = '-IN' OR ds_inorout = 'OUT') order by pi_id desc) where rownum<=10","%"+inoutNo+"%");
		if (rs.next()) {
			return rs.getResultList();
		}
		return null;
	}
	
	@Override
	public List<Map<String, Object>> getProdOut(String inoutNo, String whcode) {
		return pdaCommonDao.getProdInOut("pd_outqty", inoutNo, whcode);
	}
	
	@Override
	public List<Map<String, Object>> getNeedGetList(Integer id,String whocde, String type) {
		String ioType =baseDao.getDBSetting("BarCodeSetting", "BarcodeInOutType");		
		String sql2 = "SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,bar_location,hasbarcode FROM (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty"
					+ ",pr_zxbzs,bar_location,case when nvl(bar_id,0)=0 then 0 else 1 end hasbarcode,ROW_NUMBER() OVER(partition by pd_prodcode order by bar_location asc) as RN FROM (select pd_prodcode,max(pr_detail) "
					+ " pr_detail, max(pr_spec) pr_spec,sum(pd_outqty) pd_outqty,sum(pd_outqty)-sum(nvl(pd_barcodeoutqty,0)) pd_restqty,max(pr_zxbzs) pr_zxbzs from "
					+ "prodiodetail left join product on Pd_prodcode=pr_code where pd_piid=? and nvl(pd_barcodeoutqty,0)<pd_outqty group by "
					+ " pd_prodcode)T left join "
					+ "(select * from barcode where nvl(bar_outno,' ')=' ' AND bar_status=1 )T2 on pd_prodcode=T2.bar_prodcode )T1 WHERE T1.RN=1";
		
		String sql3=  "SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,bar_location,hasbarcode FROM (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty"
					+ ",pr_zxbzs,bar_location,case when nvl(bar_id,0)=0 then 0 else 1 end hasbarcode,ROW_NUMBER() OVER(partition by pd_prodcode order by bar_location asc) as RN FROM (select pd_prodcode,max(pr_detail) "
					+ " pr_detail, max(pr_spec) pr_spec,sum(pd_outqty) pd_outqty,sum(pd_outqty)-sum(nvl(pd_barcodeoutqty,0)) pd_restqty,max(pr_zxbzs) pr_zxbzs from "
					+ "prodiodetail left join product on Pd_prodcode=pr_code where pd_piid=? and nvl(pd_barcodeoutqty,0)<pd_outqty and pr_tracekind>0 group by "
					+ " pd_prodcode)T left join "
					+ "(select * from barcode where nvl(bar_outno,' ')=' ' AND bar_status=1 )T2 on pd_prodcode=T2.bar_prodcode)T1 WHERE T1.RN=1";
		
		String sql4=  "SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,pd_batchcode,bar_location,ba_remain,hasbarcode FROM (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty"
					+ ",pr_zxbzs,pd_batchcode,bar_location,ba_remain,case when nvl(bar_id,0)=0 then 0 else 1 end hasbarcode,ROW_NUMBER() OVER(partition by pd_batchcode order by bar_location asc) as RN FROM (select pd_prodcode,max(pr_detail) "
					+ " pr_detail, max(pr_spec) pr_spec,sum(pd_outqty) pd_outqty,sum(pd_outqty)-sum(nvl(pd_barcodeoutqty,0)) pd_restqty,max(pr_zxbzs) pr_zxbzs,pd_batchcode,max(pd_batchid)pd_batchid from "
					+ "prodiodetail left join product on Pd_prodcode=pr_code where pd_piid=? and nvl(pd_barcodeoutqty,0)<pd_outqty group by pd_batchcode"
					+ " ,pd_prodcode)T left join batch on ba_id=pd_batchid left join "
					+ "(select * from barcode where nvl(bar_outno,' ')=' ' AND bar_status=1 )T2 on pd_prodcode=T2.bar_prodcode and T2.bar_batchcode=pd_batchcode)T1 WHERE T1.RN=1";
		
		String sql5=  "SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,pd_batchcode,bar_location,ba_remain,hasbarcode FROM (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty"
					+ ",pr_zxbzs,pd_batchcode,bar_location,ba_remain,case when nvl(bar_id,0)=0 then 0 else 1 end hasbarcode,ROW_NUMBER() OVER(partition by pd_batchcode order by bar_location asc) as RN FROM (select pd_prodcode,max(pr_detail) "
					+ " pr_detail, max(pr_spec) pr_spec,sum(pd_outqty) pd_outqty,sum(pd_outqty)-sum(nvl(pd_barcodeoutqty,0)) pd_restqty,max(pr_zxbzs) pr_zxbzs,pd_batchcode,max(pd_batchid)pd_batchid from "
					+ "prodiodetail left join product on Pd_prodcode=pr_code where pd_piid=? and nvl(pd_barcodeoutqty,0)<pd_outqty and pr_tracekind>0 group by pd_batchcode"
					+ " ,pd_prodcode)T left join batch on ba_id=pd_batchid left join "
					+ "(select * from barcode where nvl(bar_outno,' ')=' ' AND bar_status=1 )T2 on pd_prodcode=T2.bar_prodcode and T2.bar_batchcode=pd_batchcode)T1 WHERE T1.RN=1";
		
		int cn = 0;
		if(("byAll").equals(ioType)){
			cn = baseDao.getCount(" select count(1) cn from prodiodetail where pd_piid="+id+" and nvl(pd_outqty,0)>nvl(pd_barcodeoutqty,0)");
			if( cn == 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该出库单对应的仓库已经完成备料");
			}
		}else if(("byWhcode").equals(ioType)){
		    cn = baseDao.getCount("select count(1) cn from prodiodetail left join warehouse on wh_code=pd_whcode where pd_piid="+id+" and nvl(wh_ifbarcode,0)<>0 and nvl(pd_barcodeoutqty,0)<nvl(pd_outqty,0)");
		    if(cn == 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该出库单对应的仓库已经完成备料");
			}
		}else{
			cn = baseDao.getCount("select count(1) cn from prodiodetail left join warehouse on wh_code=pd_whcode left join product on pr_code=pd_prodcode where pd_piid="+id+" and nvl(pd_barcodeoutqty,0)<pd_outqty  and nvl(wh_ifbarcode,0)<>0 and pr_tracekind>0 ");
			if(cn == 0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"该出库单对应的仓库已经完成备料");
			}
		}		
		
		String sql = "";
		if("byProdcode".equals(type)){
			if(("byAll").equals(ioType) || ("byWhcode").equals(ioType)){
				sql=sql2;				
			}else{
				sql=sql3;
			}
		}else{
			if(("byAll").equals(ioType) || ("byWhcode").equals(ioType)){
				sql=sql4;				
			}else{
				sql=sql5;
			}
		}
		SqlRowList rs = baseDao.queryForRowSet(sql,id);
		if(rs.next()){
			return rs.getResultList();
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有需要采集的明细，或已经采集完成");
		}
	}
	
	static final String INSERTBARCODEP = "insert into barcodeio (bi_id,bi_barcode,bi_outboxcode,bi_piid,bi_inoutno,bi_status,bi_printstatus,"
		        				  + " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_location,bi_prodid,bi_pdaget,bi_barid,bi_piclass,bi_type,bi_batchcode,bi_oldbarcode,bi_inman)"
		        				  + "values(?,?,?,?,?,99,0,?,?,?,?,?,?,2,?,?,'物料汇总',?,?,?)";
	@Override
	public Map<String, Object> outByProdcode(String barcode, int id,String whcode, String type,boolean msdcheck) {
		SqlRowList rs, rs0;
		Map<String, Object> rMap = new HashMap<String, Object>();		
		String pr_code,pi_inoutno,pi_class,on_whcode = null;
		double remain = 0,restqty=0;
		if("barcode".equals(type)){// 判断条码是否存在，条码状态，条码的剩余数量，条码所属仓库是否正确
			rs = baseDao
					.queryForRowSet("select bar_code,bar_prodcode,bar_location,bar_remain,bar_whcode,nvl(bar_status,0)bar_status,pr_id,bar_batchcode,pr_detail,pr_spec,"
							+ "bar_id,to_char(bar_indate,'yyyymmdd')bar_indate ,bar_lastcode from barcode left join product on pr_code=bar_prodcode where bar_code=? and bar_status = 1",barcode);
			if (rs.next()) {
				pr_code = rs.getString("bar_prodcode");
				remain = rs.getGeneralDouble("bar_remain");
				on_whcode = rs.getString("bar_whcode");
				if (remain <= 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "库存数为0]");
				}
				rs0 = baseDao.queryForRowSet("select sum(pd_outqty)-nvl(sum(pd_barcodeoutqty),0) restqty,count(1)cn, max(pi_inoutno)pi_inoutno,max(pi_class)pi_class  from ProdIODetail left join ProdInOut on pi_id=pd_piid where pi_id=? and pd_prodcode=? and pd_whcode=?",id,pr_code,on_whcode);
				if (rs0.next() && rs0.getInt("cn")>0){
					restqty = rs0.getDouble("restqty");
					pi_inoutno = rs0.getString("pi_inoutno");
					pi_class = rs0.getString("pi_class");
					if (restqty <= 0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码所属物料[" +pr_code+ "]已经采集完成");
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属物料[" + pr_code+ "]不存在该出库单+仓库中");
				}
				
				rs0 = baseDao.queryForRowSet("select bi_inoutno from barcodeio where bi_barcode=? and bi_prodcode=? and nvl(bi_status,0)<>99",barcode,pr_code);
				if(rs0.next()){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码：[" + barcode + "]重复采集,在单据"+rs0.getObject("bi_inoutno")+"中已经采集");
				}
				
				//控制先进先出
				String str = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wm_concat(bar_code) from barcode where bar_prodcode=? "
										+ " and bar_whcode=? and bar_status=1 and bar_remain>0 and "
										+ " to_char(bar_indate+7,'yyyymmdd')<? and bar_code<>? and rownum<20 order by bar_indate asc",
								String.class,pr_code,on_whcode,rs.getGeneralInt("bar_indate"),barcode);
				if (str != null) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"存在早于此条码：[" + str + "]的库存");
				}
				
				// 查看是否为湿敏元件
				Object ob = baseDao.getFieldDataByCondition("barcode left join product on pr_code=bar_prodcode", "pr_code", "bar_code='"
						+ barcode + "' and nvl(pr_msdlevel,' ') not in('1',' ') and pr_ismsd='Y'");
				if(!msdcheck && (ob==null ||("").equals(ob))){//条码需要分拆
					if(remain > restqty){
						rMap.put("ISMSD", false);
						rMap.put("RESTQTY", restqty);
						rMap.put("BAR_REMAIN", remain);
						rMap.put("BAR_CODE", barcode);
					 Map<String, Object> map = new HashMap<String, Object>();
					    map.put("barcode", rMap);
					    map.put("next",null );
						return map;
					}
				}else if(!msdcheck && ob != null){// 如果是湿敏元件,返回湿敏元件的相关记录
						rMap.put("ISMSD", true);
						rMap.put("RESTQTY", restqty);
						rMap.put("BAR_REMAIN", remain);
						rMap.put("msd", pdaMsdService.loadMSDLog(barcode));
						Map<String, Object> map = new HashMap<String, Object>();
					    map.put("barcode", rMap);
					    map.put("next",null);
						return map;
			    }
        		  // 材料出库采集
        		 baseDao.execute(INSERTBARCODEP,new Object[]{baseDao.getSeqId("BARCODEIO_SEQ"),barcode,"",id,pi_inoutno,pr_code,on_whcode,remain, 
        				 rs.getObject("bar_madedate"),rs.getString("bar_location"),rs.getInt("pr_id"),rs.getInt("bar_id"),pi_class,rs.getString("bar_batchcode"),rs.getString("bar_lastcode"),SystemSession.getUser().getEm_name()});
        		 baseDao.execute("update barcode set bar_status=2,bar_lastoutdate=sysdate,bar_outno=? where bar_id=?",pi_inoutno,rs.getInt("bar_id"));
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码[" + barcode + "]不存在或者不是在库状态");
			}
		}else{//采集箱号
			rs = baseDao.queryForRowSet("select bar_outboxcode1,max(bar_whcode) bar_whcode,max(bar_location) bar_location,bar_prodcode,sum(bar_remain) bar_remain,bar_batchcode,pr_detail,pr_spec,min(bar_madedate) bar_madedate from barcode left join product on bar_prodcode=pr_code where bar_outboxcode1=? and bar_status=1  and nvl(bar_outno,' ')=' ' GROUP BY bar_outboxcode1, bar_whcode, bar_prodcode,bar_batchcode, pr_detail, pr_spec",barcode);
			if(rs.next()){
				remain = rs.getGeneralDouble("bar_remain");
				pr_code = rs.getString("bar_prodcode");
				on_whcode = rs.getString("bar_whcode");
				if(remain<=0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode+ "箱内总数为0");
				}
				rs0 = baseDao.queryForRowSet("select bar_code from barcode where bar_outboxcode1=? and bar_status=1",barcode);
				if(!rs0.next()){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode+ "全部已出库");
				}			
				//物料是否已经完成采集任务
				rs0 = baseDao.queryForRowSet("select sum(pd_outqty)-nvl(sum(pd_barcodeoutqty),0) restqty,count(1)cn,max(pi_inoutno)pi_inoutno,max(pi_class)pi_class from ProdIODetail left join ProdInOut on pi_id=pd_piid where pi_id=? and pd_prodcode=? and pd_whcode=?",id,pr_code,on_whcode);
				if (rs0.next() && rs0.getInt("cn") > 0) {
					restqty = rs0.getDouble("restqty");
					pi_inoutno = rs0.getString("pi_inoutno");
					pi_class = rs0.getString("pi_class");
					if (restqty <= 0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号所属物料[" + pr_code + "]已经采集完成");
					else{
						if(remain>restqty){
							throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱内总数["+remain+"]大于需要剩余需要采集数量[" + restqty + "],如需采集请进行拆箱操作!");
						}
					}
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号所属物料[" + pr_code + "],不存在该出库单+仓库中");
				}
				
        		//插入barcodeio 数据
 				baseDao.execute(INSERTBARCODEP,new Object[]{baseDao.getSeqId("BARCODEIO_SEQ"),"",barcode,id,pi_inoutno,pr_code,on_whcode,remain, 
 						rs.getObject("bar_madedate"),rs.getString("bar_location"),rs.getInt("pr_id"),0,pi_class,rs.getString("bar_batchcode"),"",SystemSession.getUser().getEm_name()});	
 				baseDao.execute("update barcode set bar_status=2,bar_lastoutdate=sysdate ,bar_outno=? where bar_outboxcode1=? and bar_whcode=? and nvl(bar_status,0)=1  and nvl(bar_outno,' ')=' '",pi_inoutno,barcode,on_whcode);
			}else{
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号:"+barcode+"，不存在或者不是在库状态");
			}	
		}
		if(remain == restqty){
			baseDao.execute("update prodiodetail set pd_barcodeoutqty=pd_outqty where pd_piid=? and pd_prodcode=? and pd_whcode=? and nvl(pd_barcodeoutqty,0)<>pd_outqty",id,pr_code,on_whcode);
		}else{
			// 更新出库明细表中的pd_barcodeouqty
			rs0 = baseDao.queryForRowSet("select pd_id,pd_outqty-nvl(pd_barcodeoutqty,0) pd_rest from prodiodetail where pd_piid=?"
					     + " and pd_prodcode=? and pd_whcode=? and pd_outqty-nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc",id,pr_code,on_whcode);
			while (rs0.next()) {
				double rest = rs0.getDouble("pd_rest");
				if (remain > 0) {
					if (rest > 0 && rest < remain) {
						baseDao.execute("update prodiodetail set pd_barcodeoutqty=pd_outqty where pd_id=?",rs0.getInt("pd_id"));
						remain = NumberUtil.sub(remain, rest);
					} else if (rest > 0 && rest >= remain) {
						baseDao.execute("update prodiodetail set pd_barcodeoutqty=nvl(pd_barcodeoutqty,0)+? where pd_id=?",remain,rs0.getInt("pd_id"));
						remain = 0;
					}
				}
			}
		}
		updateOutStatus(id);
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("barcode", rs.getCurrentMap());
	    try{ 
	    	map.put("next", getNextByProdcode(id, on_whcode));	
	    }
	    catch (Exception e) {
	    	map.put("next", null);		
		}   	
		return map;
	}
	
	static final String INSERTBARCODEB ="insert into barcodeio (bi_id,bi_barcode,bi_outboxcode,bi_piid,bi_inoutno,bi_status,bi_printstatus,"
								+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_location,bi_prodid,bi_pdaget,bi_batchcode,bi_piclass,bi_barid,bi_type,bi_oldbarcode,bi_inman,bi_batchid)"
								+ "values(?,?,?,?,?,99,0,?,?,?,?,?,?,2,?,?,?,'批次汇总',?,?,?)";
	@Override
	public Map<String, Object> outByBatch (String barcode, int id, String whcode, String type, boolean msdcheck,String old_barcode ) {
		// 按批次数量汇总采集，type 采集的是箱号或条码
		SqlRowList rs,rs0;
		Map<String, Object> rMap = new HashMap<String, Object>();
		String ba_code,pi_inoutno,pi_class,on_whcode = null;
		int ba_id;
		double remain = 0, restqty=0;
		if ("barcode".equals(type)){// 条码,判断条码是否存在，条码状态，条码的剩余数量，条码所属仓库是否正确
			rs = baseDao.queryForRowSet("select bar_id,bar_code,bar_prodcode,bar_whcode,bar_remain,nvl(bar_status,0) bar_status,pr_id,pr_detail,pr_spec,bar_batchcode,bar_outboxcode1,bar_location ,bar_lastcode,bar_batchid,bar_lockstatus from barcode left join product on pr_code=bar_prodcode where bar_code=?"
							+" and bar_status=1",barcode);
			if (rs.next()) {
				ba_code = rs.getString("bar_batchcode");
				remain = rs.getGeneralDouble("bar_remain");
				on_whcode= rs.getString("bar_whcode");
				ba_id = rs.getGeneralInt("bar_batchid");
				if(rs.getGeneralInt("bar_lockstatus")!=0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]已被冻结！");
				}
				if (remain <= 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]库存数为0");
				}
				rs0 = baseDao.queryForRowSet("select sum(pd_outqty)-nvl(sum(pd_barcodeoutqty),0) restqty,count(1)cn,max(pi_inoutno)pi_inoutno,max(pi_class)pi_class from ProdIODetail left join ProdInOut on pi_id=pd_piid where pi_id=? and pd_batchcode=? and pd_whcode=?",id, ba_code, on_whcode);
				if (rs0.next()  && rs0.getInt("cn") > 0 ) {
					 restqty = rs0.getDouble("restqty");
					 pi_inoutno = rs0.getString("pi_inoutno");
					 pi_class = rs0.getString("pi_class");
					 if (restqty <= 0){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码所属批次[" + ba_code + "]已经采集完成");
					}
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属批次[" + ba_code + "]或者仓库["+on_whcode+"]不在该出库单中");
				}
				
				rs0 = baseDao.queryForRowSet("select bi_piid,bi_inoutno from barcodeio where bi_barcode=? and bi_whcode=? and bi_prodcode=? and nvl(bi_status,0)=0 ",ba_code,on_whcode,rs.getString("bar_prodcode"));
				if(rs0.next()){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码编号：[" + barcode + "]已经被单据：["+rs0.getString("bi_inoutno")+"]采集");
				}
				
				// 查看是否为湿敏元件
				Object ob = baseDao.getFieldDataByCondition("barcode left join product on pr_code=bar_prodcode", "pr_code", "bar_code='"
						+ barcode + "' and nvl(pr_msdlevel,' ') not in('1',' ') and pr_ismsd='Y'");
				if(!msdcheck && (ob == null ||("").equals(ob))){
					if(remain > restqty){
						rMap.put("ISMSD", false);
						rMap.put("RESTQTY", restqty);
						rMap.put("BAR_REMAIN", remain);
						rMap.put("BAR_CODE", barcode);
					    Map<String, Object> map = new HashMap<String, Object>();
					    map.put("barcode", rMap);
					    map.put("next",null);
						return map;
					}
				}else if(!msdcheck && ob != null){// 如果是湿敏元件,返回湿敏元件的相关记录
						rMap.put("ISMSD", true);
						rMap.put("RESTQTY", restqty);
						rMap.put("BAR_REMAIN", remain);
						rMap.put("msd", pdaMsdService.loadMSDLog(barcode));
					    Map<String, Object> map = new HashMap<String, Object>();
					    map.put("barcode", rMap);
					    map.put("next",null);
						return map;
			    }
				// 材料出库采集
				baseDao.execute(INSERTBARCODEB,new Object[]{baseDao.getSeqId("BARCODEIO_SEQ"),barcode,"",id,pi_inoutno,rs.getString("bar_prodcode"),on_whcode,remain,
						rs.getObject("bar_madedate"),rs.getString("bar_location"),rs.getInt("pr_id"),ba_code,pi_class,rs.getInt("bar_id"),old_barcode,SystemSession.getUser().getEm_name(),ba_id});
				
				baseDao.execute("update barcode set bar_status=2,bar_lastoutdate=sysdate ,bar_outno=? where bar_id=?",pi_inoutno,rs.getInt("bar_id")); 	
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码[" + barcode + "]不存在或者不是在库状态");
			}
		} else {// 箱号
			rs = baseDao.queryForRowSet("select bar_outboxcode1,bar_whcode,bar_prodcode,sum(bar_remain) bar_remain,bar_batchcode,bar_batchid,max(bar_location) bar_location,pr_detail,pr_spec,min(bar_madedate) bar_madedate from barcode left join product on bar_prodcode=pr_code where bar_outboxcode1=? and bar_whcode=? and bar_status=1  and nvl(bar_outno,' ')=' ' GROUP BY bar_outboxcode1, bar_whcode, bar_prodcode,bar_batchcode, pr_detail, pr_spec,bar_batchid",barcode,whcode);
			if (rs.next()) {
				remain = rs.getGeneralDouble("bar_remain");
				ba_code = rs.getString("bar_batchcode");
				on_whcode = rs.getString("bar_whcode");
				ba_id = rs.getGeneralInt("bar_batchid");
				if (remain <= 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode + "箱内总数为0");
				}
				rs0 = baseDao.queryForRowSet("select bar_code from barcode where bar_outboxcode1=? and bar_status=1 and nvl(bar_outno,' ')=' '",barcode);
				if(!rs0.next()){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode+ "已全部出库");
				}	
				rs0 = baseDao.queryForRowSet("select bar_batchcode,bar_code from barcode  where bar_outboxcode1=? and not exists (select 1 from prodiodetail where pd_piid=? and pd_whcode=? and pd_batchcode=bar_batchcode)",barcode, id, on_whcode);
				if (rs0.next()) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode + "中的子条码:" + rs0.getString("bar_code") + "所属批次："+ rs0.getString("bar_batchcode") + "不属于当前出库单");
				}
				//判断包装箱号所属批次已经采集
				rs0 = baseDao
						.queryForRowSet(
								"select sum(pd_outqty)-nvl(sum(pd_barcodeoutqty),0) restqty,count(1)cn,max(pi_inoutno)pi_inoutno,max(pi_class)pi_class from ProdIODetail left join ProdInOut on pi_id=pd_piid where pi_id=? and pd_batchcode=? and pd_whcode=?",id, ba_code, on_whcode);
				if (rs0.next() && rs0.getInt("cn") > 0) {
					restqty = rs0.getDouble("restqty");
					if (restqty <=0)
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号所属批次[" + ba_code + "]已经采集完成");
					else{
						if(remain > restqty){
							throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱内总数["+remain+"]大于需要剩余需要采集数量[" + restqty + "],如需采集请进行拆箱操作!");
						}
					}	
					pi_inoutno = rs0.getString("pi_inoutno");
					pi_class = rs0.getString("pi_class");
				}else{
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"箱号所属仓库["+on_whcode+"]+批次[" + ba_code + "]不在出库单中");
				}
				baseDao.execute(INSERTBARCODEB,new Object[]{baseDao.getSeqId("BARCODEIO_SEQ"),"",barcode,id,pi_inoutno,rs.getString("bar_prodcode"),
								whcode,remain,rs.getObject("bar_madedate"),rs.getString("bar_location"),rs.getInt("pr_id"),ba_code,pi_class,0,"",SystemSession.getUser().getEm_name(),ba_id});
				baseDao.execute("update barcode set bar_status=2,bar_lastoutdate=sysdate,bar_outno=? where bar_outboxcode1 =? and bar_whcode=? and nvl(bar_status,0)=1 and nvl(bar_outno,' ')=' '",pi_inoutno,barcode,whcode);   
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号:" + barcode + "，不存在或不是在库状态");
			}
		}
		if(remain == restqty){
			baseDao.execute("update prodiodetail set pd_barcodeoutqty=pd_outqty where pd_piid=? and pd_batchcode=? and pd_whcode=? and nvl(pd_barcodeoutqty,0)<>pd_outqty",id,ba_code,on_whcode);
		}else{
			// 更新出库明细表中的pd_barcodeouqty
			rs0 = baseDao
					.queryForRowSet(
							"select pd_id,pd_outqty-nvl(pd_barcodeoutqty,0) pd_rest from prodiodetail where pd_piid=? and pd_batchcode=? and pd_whcode=? and pd_outqty-nvl(pd_barcodeoutqty,0)>0",
							id, ba_code, on_whcode);
			while(rs0.next()){
				double rest = rs0.getDouble("pd_rest");
				if (remain > 0) {
					if (rest > 0 && rest < remain) {
						baseDao.execute("update prodiodetail set pd_barcodeoutqty=pd_outqty where pd_id=?", rs0.getInt("pd_id"));
						remain = NumberUtil.sub(remain, rest);
					} else if (rest > 0 && rest >= remain) {
						baseDao.execute("update prodiodetail set pd_barcodeoutqty=nvl(pd_barcodeoutqty,0)+? where pd_id=?",remain,rs0.getInt("pd_id"));
						remain = 0;
					}
				}
			}
		}
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("barcode", rs.getCurrentMap());
	    try{ 
	    	map.put("next",getNextByBatch(id, on_whcode));	//提示用户下一条采集数据
	    	baseDao.execute("update prodinout set pi_pdastatus='备料中' where pi_id=?",id);
	    }
	    catch (Exception e) {
	    	map.put("next", null);		
	    	updateOutStatus(id);
		}   	
		return map;
	}
	
	@Override
	@Transactional
	public synchronized Map<String, Object> outByBatchBreaking(int id, String whcode, String barcode, Double or_remain,
			Double bar_remain) {
		//获取出库单号
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno from prodinout where pi_id=?",id);
		if(rs.next()){
			List<Map<String,Object>> list = pdaBatchService.breakingBatch(barcode, or_remain, bar_remain,"出库单："+rs.getString("pi_inoutno")+"拆分！");
			if(list.size() == 0){
				BaseUtil.showError("条码拆分失败！");
			}
			return outByBatch(list.get(0).get("BAR_CODE").toString(), id, whcode, "barcode", true,barcode);
		}else{
			BaseUtil.showError("出库单号不存在！");
		}
		return null;
	}
	
	
	@Override
	public Map<String, Object> getNextByProdcode(int pi_id, String pd_whcode) {
		String ioType =baseDao.getDBSetting("BarCodeSetting", "BarcodeInOutType");		
		String sql ;
		if("byAll".equals(ioType) || "byWhcode".equals(ioType)){
			sql = "SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,bar_location FROM (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty"
					+ ",pr_zxbzs,bar_location,ROW_NUMBER() OVER(partition by pd_prodcode order by bar_location asc) as RN FROM (select pd_prodcode,max(pr_detail) "
					+ " pr_detail, max(pr_spec) pr_spec,sum(pd_outqty) pd_outqty,sum(pd_outqty)-sum(nvl(pd_barcodeoutqty,0)) pd_restqty,max(pr_zxbzs) pr_zxbzs from "
					+ "prodiodetail left join product on Pd_prodcode=pr_code where pd_piid=?  and nvl(pd_barcodeoutqty,0)<pd_outqty group by "
					+ " pd_prodcode)T left join "
					+ "(select * from barcode where nvl(bar_outno,' ')=' ' AND bar_status=1 )T2 on pd_prodcode=T2.bar_prodcode )T1 WHERE T1.RN=1 and rownum=1";
		}else{
			sql="SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,bar_location FROM (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty"
					+ ",pr_zxbzs,bar_location,ROW_NUMBER() OVER(partition by pd_prodcode order by bar_location asc) as RN FROM (select pd_prodcode,max(pr_detail) "
					+ " pr_detail, max(pr_spec) pr_spec,sum(pd_outqty) pd_outqty,sum(pd_outqty)-sum(nvl(pd_barcodeoutqty,0)) pd_restqty,max(pr_zxbzs) pr_zxbzs from "
					+ "prodiodetail left join product on Pd_prodcode=pr_code where pd_piid=? and nvl(pd_barcodeoutqty,0)<pd_outqty and pr_tracekind>0 group by "
					+ " pd_prodcode)T left join "
					+ "(select * from barcode where nvl(bar_outno,' ')=' ' AND bar_status=1 )T2 on pd_prodcode=T2.bar_prodcode)T1 WHERE T1.RN=1 and rownum=1";
		}
		SqlRowList rs = baseDao.queryForRowSet(sql,pi_id);
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有需要采集的明细，或已经采集完成");
		}
	
	}

	@Override
	public Map<String, Object> getNextByBatch(int pi_id, String pd_whcode) {
		String ioType =baseDao.getDBSetting("BarCodeSetting", "BarcodeInOutType");	
		String sql ;
		if(("byAll").equals(ioType) || ("byWhcode").equals(ioType)){
			sql= "SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,pd_batchcode,bar_location,ba_remain FROM (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty"
					+ ",pr_zxbzs,pd_batchcode,bar_location,ba_remain,ROW_NUMBER() OVER(partition by pd_batchcode order by bar_location asc) as RN FROM (select pd_prodcode,max(pr_detail) "
					+ " pr_detail, max(pr_spec) pr_spec,sum(pd_outqty) pd_outqty,sum(pd_outqty)-sum(nvl(pd_barcodeoutqty,0)) pd_restqty,max(pr_zxbzs) pr_zxbzs,pd_batchcode,max(pd_batchid)pd_batchid from "
					+ "prodiodetail left join product on Pd_prodcode=pr_code where pd_piid=? and nvl(pd_barcodeoutqty,0)<pd_outqty group by pd_prodcode,pd_batchcode "
					+ " )T inner join "
					+ "(select bar_code,bar_id,bar_batchcode,bar_prodcode,bar_location,bar_whcode from barcode where nvl(bar_outno,' ')=' ' AND bar_status=1 )T2 on pd_prodcode=T2.bar_prodcode and T2.bar_batchcode=pd_batchcode left join batch on ba_id=pd_batchid)T1 WHERE T1.RN=1 and rownum=1";				
		}else{
			sql="SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,pd_batchcode,bar_location,ba_remain FROM (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty"
					+ ",pr_zxbzs,pd_batchcode,bar_location,ba_remain,ROW_NUMBER() OVER(partition by pd_batchcode order by bar_location asc) as RN FROM (select pd_prodcode,max(pr_detail) "
					+ " pr_detail, max(pr_spec) pr_spec,sum(pd_outqty) pd_outqty,sum(pd_outqty)-sum(nvl(pd_barcodeoutqty,0)) pd_restqty,max(pr_zxbzs) pr_zxbzs,pd_batchcode,max(pd_batchid)pd_batchid  from "
					+ "prodiodetail left join product on Pd_prodcode=pr_code where pd_piid=? and nvl(pd_barcodeoutqty,0)<pd_outqty and pr_tracekind>0 group by pd_prodcode,pd_batchcode "
					+ " )T inner join "
					+ "(select bar_code,bar_id,bar_batchcode,bar_prodcode,bar_location,bar_whcode from barcode where nvl(bar_outno,' ')=' ' AND bar_status=1 )T2 on pd_prodcode=T2.bar_prodcode and T2.bar_batchcode=pd_batchcode left join batch on ba_id=pd_batchid)T1 WHERE T1.RN=1 and rownum=1";
		}
		SqlRowList rs = baseDao.queryForRowSet(sql,pi_id);
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有需要采集的明细，或已经采集完成");
		}
	}

	@Override
	public Map<String,Object> deleteDetail(Integer bi_piid, String barcode,String outboxcode, String whcode) {
		String inoutno = null;
		String on_whcode = null;
		SqlRowList rs1 = null;
		SqlRowList rs2 = null;
		double outqty = 0,rqty=0;
		if(barcode != null && !("").equals(barcode)){
			  SqlRowList rs = baseDao.queryForRowSet("select bi_inoutno,bi_outqty,bi_type,bi_whcode,bi_prodcode,bi_batchcode,bi_barid,bi_reason from barcodeio where bi_piid=? and bi_barcode=?",bi_piid,barcode);
			  if(rs.next()){
				   inoutno = rs.getString("bi_inoutno");
				   outqty = rs.getDouble("bi_outqty");
				   rqty = outqty;
				   on_whcode = rs.getString("bi_whcode");
				   rs1 = baseDao.queryForRowSet("select bar_id,bar_code,bar_status,bar_outno,bar_remain,bar_prodcode,pr_detail,pr_spec,bar_batchcode,bar_location from barcode left join product on bar_prodcode=pr_code where bar_code=? and bar_whcode=?",barcode,on_whcode);
				   if(rs1.next()){
						if(rs1.getInt("bar_status")!=2 || !rs1.getObject("bar_outno").equals(rs.getObject("bi_inoutno"))){
							throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤出库的条码未被采集");
						}
						if(rs1.getDouble("bar_remain")!= outqty){
							throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤出库的条码库存数量["+rs1.getDouble("bar_remain")+"]与出库备料的数量["+rs1.getDouble("bi_outqty")+"]不一致");
						}
				   }else{
						throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销出库的条码不存在");
				   }
				   if("批次汇总".equals(rs.getString("bi_type"))){
					    rs2 = baseDao.queryForRowSet("select pd_id,pd_barcodeoutqty from prodiodetail where pd_piid=? and pd_whcode=? and pd_batchcode=? and nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc",bi_piid,on_whcode,rs.getObject("bi_batchcode"));
				   }else{
					    rs2 = baseDao.queryForRowSet("select pd_id,pd_barcodeoutqty from prodiodetail where pd_piid=? and pd_whcode=? and pd_prodcode=? and nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc",bi_piid,on_whcode,rs.getObject("bi_prodcode"));
				   }
				   while(rs2.next()){
					  if(outqty > 0){
						 if(outqty > rs2.getGeneralDouble("pd_barcodeoutqty")){
							 baseDao.execute("update prodiodetail set PD_BARCODEOUTQTY =0  where pd_id=?",rs2.getInt("pd_id"));
							 outqty = NumberUtil.sub(outqty, rs2.getGeneralDouble("pd_barcodeoutqty"));
						 }else {
							 baseDao.execute("update prodiodetail set PD_BARCODEOUTQTY = NVL(pd_barcodeoutqty,0) - ?  where pd_id=?",outqty,rs2.getInt("pd_id"));
							 outqty = 0;
						 }								
					 }
				 }
				 List<String>sqls = new ArrayList<String>();
				//增加操作日志记录
				sqls.add("insert into barcodelogger(bl_id,bl_barcode,bl_barid,bl_prodcode,bl_whcode,bl_date,bl_inman,bl_action,bl_description,bl_inoutno)"
				 		+ "values(barcodelogger_seq.nextval,'"+barcode+"',"+rs1.getInt("bar_id")+",'"+rs1.getString("bar_prodcode")+"','"+on_whcode+"',sysdate,'"+SystemSession.getUser().getEm_name()+"','撤销出库','出库单号："+inoutno+",数量："+rqty+"','"+inoutno+"')");
				sqls.add("delete from barcodeIo where bi_piid="+bi_piid+" and bi_barcode='"+barcode+"'");
				sqls.add("update barcode set bar_status=1,bar_outno='' where bar_id="+rs1.getInt("bar_id"));
				baseDao.execute(sqls);
				//更新备料状态
				baseDao.execute("update prodinout set pi_pdastatus='备料中' where pi_id=? and exists (select 1 from barcodeio where bi_piid=pi_id) ",bi_piid);
				baseDao.execute("update prodinout set pi_pdastatus='未备料' where pi_id=? and not exists (select 1 from barcodeio where bi_piid=pi_id) ",bi_piid);	
				return rs1.getCurrentMap();	
			  }else{
			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"撤销的条码不存在该出库单已备料列表中");
		  }				  
       }else if(outboxcode!=null && !("").equals(outboxcode)){
    	  SqlRowList rs = baseDao.queryForRowSet("select bi_inoutno,bi_outqty,bi_type,bi_whcode,bi_prodcode,bi_batchcode,bi_barid from barcodeio where bi_piid=? and bi_outboxcode=? ",bi_piid,outboxcode);
    	  if(rs.next()){
    		  inoutno = rs.getString("bi_inoutno");
    		  outqty = rs.getDouble("bi_outqty");
    		  rs1 = baseDao.queryForRowSet("select max(bar_outboxcode1) bar_outboxcode1,max(bar_whcode) bar_whcode, sum(bar_remain) bar_remain,max(bar_prodcode) bar_prodcode,max(pr_detail) pr_detail,max(pr_spec) pr_spec from barcode left join product on bar_prodcode = pr_code where bar_outboxcode1=? and bar_status=2 and bar_outno=?",outboxcode,inoutno);
    		  if(rs1.next()){
    			  on_whcode = rs1.getString("bar_whcode");
    			  if(rs1.getDouble("bar_remain") != outqty){
					  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤出库的条码库存数量["+rs1.getDouble("bar_remain")+"]与出库备料的数量["+rs.getDouble("bi_outqty")+"]不一致");
				  }
    		  }else{
    			  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销出库的箱号不存在");
    		  }
    		  if("批次汇总".equals(rs.getString("bi_type"))){
				   rs2 = baseDao.queryForRowSet("select pd_id,pd_barcodeoutqty from prodiodetail where pd_piid=? and pd_whcode=? and pd_batchcode=? and nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc,pd_barcodeoutqty desc",bi_piid,on_whcode,rs.getObject("bi_batchcode"));
			  }else{
				   rs2 = baseDao.queryForRowSet("select pd_id,pd_barcodeoutqty from prodiodetail where pd_piid=? and pd_whcode=? and pd_prodcode=? and nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc,pd_barcodeoutqty desc",bi_piid,on_whcode,rs.getObject("bi_prodcode"));
			  }
			  while(rs2.next()){
				  if(outqty > 0){
					 if(outqty > rs2.getGeneralDouble("pd_barcodeoutqty")){
						 baseDao.execute("update prodiodetail set PD_BARCODEOUTQTY =0  where pd_id=?",rs2.getInt("pd_id"));
						 outqty = NumberUtil.sub(outqty, rs2.getGeneralDouble("pd_barcodeoutqty"));
					 }else {
						 baseDao.execute("update prodiodetail set PD_BARCODEOUTQTY = NVL(pd_barcodeoutqty,0) - ?  where pd_id=?",outqty,rs2.getInt("pd_id"));
						 outqty = 0;
					 }								
				 }
			 }
			baseDao.execute("update barcode set bar_outno='',bar_status=1 where bar_outboxcode1=? and bar_outno=? and bar_whcode=?",outboxcode,inoutno,on_whcode);
			baseDao.execute("delete from barcodeIo where bi_piid=? and bi_outboxcode=? and bi_whcode=?",bi_piid,outboxcode,on_whcode);
			//更新备料状态
			baseDao.execute("update prodinout set pi_pdastatus='备料中' where pi_id=? and exists (select 1 from barcodeio where bi_piid=pi_id) ",bi_piid);
			baseDao.execute("update prodinout set pi_pdastatus='未备料' where pi_id=? and not exists (select 1 from barcodeio where bi_piid=pi_id) ",bi_piid);	
			return rs1.getCurrentMap();	
    	  }else{
    		  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销出库的箱号不存在已备料列表中");
    	  }
        }
		return null;
	}
	
	//查询数据
	@Override
	public Map<String, Object> getBarcodeData(String barcode) {
		SqlRowList rs=baseDao.queryForRowSet("select bar_status,bar_prodcode,pr_detail,pr_spec,bar_batchcode,bar_whcode,bar_remain,bar_vendcode "
					+"from barcode left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1",barcode);
		if(rs.next()){
			return rs.getCurrentMap();
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该条码不存在或者不是在库状态");
		}
	}
	
	//修改数量
	@Override
	public Map<String, Object> updateBarCodeQty( String barcode,double nowqty) {
		SqlRowList rs = baseDao.queryForRowSet("select bar_id,bar_status,bar_prodcode,pr_detail,pr_spec,bar_batchcode, pr_tracekind,bar_whcode,bar_remain from barcode "
						+ "left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1 ",barcode);
		if(rs.next()){
			baseDao.execute("update barcode set bar_remain=? where bar_id=?",nowqty,rs.getInt("bar_id"));
			//增加操作日志记录
			baseDao.execute("insert into barcodelogger(bl_id,bl_barcode,bl_barid,bl_prodcode,bl_whcode,bl_date,bl_inman,bl_action,bl_description)"
					+ " values(barcodelogger_seq.nextval,?,?,?,?,sysdate,?,'修改数量',?)",barcode,rs.getInt("bar_id"),rs.getString("bar_prodcode"),rs.getString("bar_whcode"),SystemSession.getUser().getEm_name(),"原数量："+rs.getDouble("bar_remain")+"，新数量："+nowqty);
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该条码不存在或者不是在库状态");
		}
		return null;
	}
	
	@Override
	@Transactional
	public Map<String,Object> specialOut(String barcode,String reason ,Integer  id ,String wh_code){
		//特殊出库
		double restqty=0,remain=0;
		String pr_code,pi_inoutno,pi_class,on_whcode = null;
		if(!StringUtil.hasText(reason)){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,"请选择特殊出库原因！");
		}
		SqlRowList rs = baseDao.queryForRowSet("select bar_status,bar_prodcode,pr_detail,pr_spec,pr_id,bar_batchcode,bar_whcode,bar_remain,bar_id,bar_location,bar_madedate,bar_lastcode,bar_batchid "
						+"from barcode left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1",barcode);
		if(rs.next()){
			remain = rs.getDouble("bar_remain");
			pr_code = rs.getString("bar_prodcode");
			on_whcode = rs.getString("bar_whcode");
			if(remain <= 0){
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码剩余库存必须大于0");
			}
			/*不限制批次是否在原明细行中存在20170519
			 * SqlRowList rs0 = baseDao.queryForRowSet("select count(1) cn from prodiodetail where pd_piid=? and pd_whcode=? and pd_batchcode=?",id,wh_code,rs.getString("bar_batchcode"));
			if(rs0.next() && rs0.getInt("cn")>0){
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"该条码的批次["+rs.getString("bar_batchcode")+"]与该出库单存在的批次相同，不需要特殊出库");
			}
			*/
			SqlRowList rs0= baseDao.queryForRowSet("select nvl(sum(pd_outqty-nvl(pd_barcodeoutqty,0)),0) restqty,count(1)cn,max(pi_inoutno)pi_inoutno,max(pi_class)pi_class  from prodiodetail left join prodinout on pi_id=pd_piid "
							+"where pd_piid=? and pd_whcode=? and pd_prodcode=?",id,on_whcode,pr_code);
			if(rs0.next() && rs0.getInt("cn") > 0){
				restqty = rs0.getDouble("restqty");
				pi_inoutno = rs0.getString("pi_inoutno");
				pi_class = rs0.getString("pi_class");
				if(restqty <=0){
					throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码所属的物料在该出库单+仓库中已经完成了备料");
				}
				if(restqty < remain){
					//如果条码库存数量大于剩余需要出库的数量，返回条码数量，剩余需要出库的数量，条码号
					Map<String,Object> map = new HashMap<String, Object>();
					map.put("BAR_CODE", barcode);
					map.put("BAR_REMAIN", remain);
					map.put("RESTQTY", restqty);
					return map;
				}else{
					//插入到barcodeio表
					baseDao.execute("insert into barcodeio (bi_id,bi_barcode,bi_piid,bi_inoutno,bi_status,bi_printstatus,"
							+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_location,bi_prodid,bi_pdaget,bi_batchcode,bi_piclass,bi_barid,bi_reason,bi_oldbarcode,bi_inman,bi_batchid)"
							+ "values(?,?,?,?,0,0,?,?,?,?,?,?,2,?,?,?,?,?,?,?)",new Object[]{baseDao.getSeqId("BARCODEIO_SEQ"),barcode,id,pi_inoutno,pr_code,on_whcode,remain,rs.getObject("bar_madedate"),
								   rs.getString("bar_location"),rs.getInt("pr_id"),rs.getString("bar_batchcode"),pi_class,rs.getInt("bar_id"),reason,rs.getString("bar_lastcode"),SystemSession.getUser().getEm_name(),rs.getGeneralInt("bar_batchid")});
                    //更新barcode表
					baseDao.execute("update barcode set bar_status=2,bar_lastoutdate=sysdate ,bar_outno=? where bar_id=?",pi_inoutno,rs.getInt("bar_id")); 											
					if(restqty > remain){
						 // 更新出库明细表中的pd_barcodeouqty
		        		rs0 = baseDao.queryForRowSet("select pd_id,pd_outqty-nvl(pd_barcodeoutqty,0) pd_rest from prodiodetail where pd_piid=?" 
		        				  + " and pd_prodcode=? and pd_whcode=? and pd_outqty-nvl(pd_barcodeoutqty,0)>0 order by pd_pdno asc",id,pr_code,on_whcode);
						while (rs0.next()) {
								double rest = rs0.getDouble("pd_rest");
								if (remain > 0) {
									if (rest > 0 && rest < remain) {
										baseDao.execute("update prodiodetail set pd_barcodeoutqty=pd_outqty where pd_id=?" + rs0.getInt("pd_id"));
										remain = NumberUtil.sub(remain, rest);
									} else if (rest > 0 && rest >= remain) {
										baseDao.execute("update prodiodetail set pd_barcodeoutqty=nvl(pd_barcodeoutqty,0)+? where pd_id=?",remain,rs0.getInt("pd_id"));
										remain = 0;
									}
								}
						}
					}else{
						baseDao.execute("update prodiodetail set pd_barcodeoutqty=pd_outqty where pd_piid=? and pd_prodcode=?",id,rs.getString("bar_prodcode"));
					}
					
					try{ 
				    	getNextByBatch(id, on_whcode);	//提示用户下一条采集数据
				    	baseDao.execute("update prodinout set pi_pdastatus='备料中' where pi_id=?",id);
				    }catch (Exception e) {
				    	updateOutStatus(id);
					}   
				}
			}else{
				 throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码所属的物料在该出库单+仓库中不存在");
			}	
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码不存在或不是在库状态");
		}
		return null;
	}

	@Override
	public Map<String, Object> freeOut(String barcode, String type) {
		/* * 自由采集模式 判断是条码还是箱号 */
		SqlRowList rs = null;
		if ("barcode".equals(type)) {// 条码，有效 bar_status<>0，剩余数大于0 bar_remain>0，条码所在的箱号已经采集 ，有效期 ?
			rs = baseDao
					.queryForRowSet(
							"select bar_code,bar_prodcode,bar_whcode,pr_detail,bar_batchcode,bar_id,nvl(bar_status,0) bar_status,nvl(bar_remain,0)bar_remain,bar_outboxcode1 from barcode left join product on pr_code=bar_prodcode where bar_code=?",
							barcode);
			if (rs.next()) {
				if (rs.getInt("bar_status") != 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + barcode + "，无效!");
				} else if (rs.getDouble("bar_remain") <= 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码:" + barcode + "，剩余数为0!");
				}
				if (rs.getObject("bar_outboxcode1") != null) {
					// 判断条码所在的箱号是否已经采集
					SqlRowList rs2 = baseDao
							.queryForRowSet(
									"SELECT * FROM barcodeio left join mes_package_view on bi_outboxcode=V_OUTBOXCODE WHERE V_OUTBOXCODE=? AND V_BARCODE=? and nvl(bi_outqty,0)>0",
									rs.getObject("bar_outboxcode1"), barcode);
					if (rs2.next()) {
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码归属的箱号:" + rs.getObject("bar_outboxcode1") + "，已经出库采集!");
					}
				}
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码:" + barcode + "，不存在!");
			}
			// 插入barcodeio 数据
			baseDao.execute(
					"insert into barcodeio(bi_id,bi_barcode,bi_status,bi_printstatus,"
							+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_location,bi_prodid,bi_pdaget,bi_batchcode,bi_barid,bi_inman)"
							+ "select barcodeio_seq.nextval,bar_code,99,0,bar_prodcode,bar_whcode,bar_remain,bar_madedate,bar_location,pr_id,2,bar_batchcode,bar_id ,'"+SystemSession.getUser().getEm_name()
							+ "' from barcode left join product on pr_code=bar_prodcode where bar_code=?", barcode);
			// 更新bar_remain=0
			baseDao.execute("update barcode set bar_status=2,bar_lastoutdate=sysdate where bar_code=?", barcode);
		} else {// 箱号
			rs = baseDao
					.queryForRowSet(
							"select pa_outboxcode,pa_totalqty,pa_whcode,pa_prodcode,pr_detail,pr_spec from package left join product on pr_code=pa_prodcode where pa_outboxcode=?",
							barcode);
			if (rs.next()) {
				if (rs.getGeneralDouble("pa_totalqty") <= 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode + "箱内总数为0");
				}
				SqlRowList rs2 = baseDao
						.queryForRowSet(
								"select  count(1) cn from barcodeio where bi_outboxcode=? and nvl(bi_outqty,0)>0",
								barcode);
				if (rs2.next() && rs2.getInt("cn") > 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode + "重复采集");
				}
				// 判断箱内条码是否已经采集
				rs2 = baseDao
						.queryForRowSet(
								"select count(1)cn from barcodeio left join mes_package_view on bi_barcode=v_barcode where v_outboxcode=? and nvL(bi_outqty,0)>0 and nvl(bi_barcode,' ')<>' '",
								barcode);
				if (rs2.next() && rs2.getInt("cn") > 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"包装箱号：" + barcode + "中的子条码已采集");
				}
				// 插入barcodeio 数据
				baseDao.execute(
						"insert into barcodeio(bi_id,bi_outboxcode,bi_status,bi_printstatus,"
								+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_prodid,bi_pdaget,bi_inman)"
								+ "select barcodeio_seq.nextval,pa_outboxcode,99,0,pa_prodcode,pa_whcode,pa_totalqty,pa_packdate,pr_id,2,'"+SystemSession.getUser().getEm_name()
								+ "' from package left join product on pr_code=pa_prodcode where pa_outboxcode=?",
						barcode);
				// 更新bar_remain=0
				baseDao.execute("update package set pa_totalqty=0 where pa_outboxcode=?", barcode);
				baseDao.execute(
						"update barcode set bar_remain=0 where exists(select 1 from mes_package_view where v_outboxcode=? and v_barcode=bar_code)",
						barcode);
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"箱号:" + barcode + "，不存在!");
			}
		}
		return rs.getCurrentMap();
	}
	
	@Override
	public List<Map<String, Object>> getNeedGetListDeal(String ids, String type,Integer page, Integer pagesize) {
		//String ioType =baseDao.getDBSetting("BarCodeSetting", "BarcodeInOutType");
		int start = ((page - 1) * pagesize + 1);
		int end = page * pagesize;
		String sql = "select * from (select tt.*,rownum rn from ( select pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,pd_batchcode,ba_hasbarcode,bar_location,pd_batchid,bar_remain from (select pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_restqty,pr_zxbzs,pd_batchcode,ba_hasbarcode,bar_location,pd_batchid,bar_remain,"
				+" ROW_NUMBER() OVER(partition by pd_batchcode order by bar_location asc) as RN from "
				+" (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_batchcode,pd_outqty-nvl(pdaqty,0)pd_restqty,pd_batchid,pr_zxbzs ,nvl(ba_hasbarcode,0)ba_hasbarcode "
				+" FROM (SELECT MAX(pd_batchcode)pd_batchcode,max(pd_prodcode)pd_prodcode,pd_batchid,sum(pd_outqty)pd_outqty "
				+" from prodiodetail where pd_piid in("+ids+") group by pd_batchid) A LEFT JOIN batch on ba_id=pd_batchid "
				+" left join (select bi_batchid,nvl(sum(nvL(pdaqty,0)),0)pdaqty from SCM_BARCODEIO_OUT_VIEW where BI_PIID in("+ids+")  "
				+" group by BI_BATCHID) on bi_batchid=pd_batchid left join product on pr_code=pd_prodcode)T  "
				+" LEFT JOIN barcode ON  pd_prodcode=bar_prodcode AND pd_batchid=bar_batchid where nvl(bar_outno,' ')=' ' AND bar_status=1 and pd_restqty>0 order by pd_prodcode)T1 where T1.RN=1) tt"
				+" where rownum<=? )where rn>=?";	
		
	    SqlRowList rs0 = baseDao.queryForRowSet(sql,end,start);
		if(rs0.next()){
			List<Map<String,Object>> list = rs0.getResultList();
			int cn = list.size();
			for(int i=0;i<cn;i++){
				SqlRowList rs = baseDao.queryForRowSet("SELECT pd_inoutno,pd_outqty,pd_outqty-nvl(pdaqty,0)pd_restqty FROM "
							+" (select max(pd_piid)pd_piid, max(pd_batchid)pd_batchid,pd_inoutno,sum(pd_outqty)pd_outqty from prodiodetail where pd_piid in ("+ids+") "
							+" and pd_batchid=? group by pd_inoutno )t  left join SCM_BARCODEIO_OUT_VIEW on bi_piid=pd_piid and pd_batchid=bi_batchid"
							+" where pd_outqty>nvl(pdaqty,0)",list.get(i).get("PD_BATCHID"));
				if(rs.next()){
					list.get(i).put("NOS", rs.getResultList());
				}
			}
			return list;
		}else{
			return null;
		}
	}
	
	static final String INSERTINTOBARCODEIODEAL ="insert into barcodeio (bi_id,bi_indate,bi_status,bi_printstatus,bi_type,bi_inman,bi_barcode,bi_outboxcode,bi_piid,bi_inoutno,"
						+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_location,bi_prodid,bi_pdaget,bi_batchcode,bi_batchid,bi_piclass,bi_barid,bi_oldbarcode,bi_reason)"
						+ "values(barcodeio_seq.nextval,sysdate,99,0,'批次汇总',";
	@Override
	public synchronized Map<String, Object> outByBatchDeal(String barcode, String ids, String type, boolean msdcheck) {
		// 按批次数量汇总采集，type 采集的是箱号或条码
		SqlRowList rs = null,rs0;
		Map<String, Object> rMap = new HashMap<String, Object>();
		List<Map<String,Object>> rList = new ArrayList<Map<String,Object>>();
		String ba_code,pi_class = null,whcode = null,pi_ids=null,pr_code,pi_inoutno;
		Integer pi_id,ba_id,bar_id;
		StringBuffer rMessage = new StringBuffer();
		int length;
		double remain = 0, restqty=0;
		if ("barcode".equals(type)){// 条码,判断条码是否存在，条码状态，条码的剩余数量，条码所属仓库是否正确
			rs = baseDao.queryForRowSet("select bar_id,bar_code,bar_prodcode,bar_whcode,bar_remain,nvl(bar_status,0) bar_status,pr_id,pr_detail,pr_spec,bar_batchcode,bar_batchid,bar_outboxcode1,bar_location,bar_lastcode,nvl(bar_lockstatus,0) bar_lockstatus,bar_madedate,case when bar_usingtime is null then 1 when bar_usingtime+5/(24*60)<=sysdate then 1 else 0 end bar_using from barcode left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1",barcode);
			if (rs.next()) {
				if(rs.getGeneralInt("bar_using") == 0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"不允许同时操作一个条码，请稍后处理，条码[" + barcode + "]");
				}
				ba_code = rs.getString("bar_batchcode");
				remain = rs.getGeneralDouble("bar_remain");
				whcode = rs.getString("bar_whcode");
				pr_code = rs.getString("bar_prodcode");
				ba_id = rs.getInt("bar_batchid");
				bar_id = rs.getInt("bar_id");
				//判断条码是否被冻结
				if(rs.getInt("bar_lockstatus") == -1){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]已冻结，不允许采集");
				}
				if (remain <= 0) {
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]库存数为0");
				}
				rs0 = baseDao.queryForRowSet("SELECT wm_concat(pd_piid)pi_ids,count(1)cn,sum(pd_outqty)-sum(nvl(pdaqty,0))restqty "+
								" FROM (SELECT MAX(pd_batchcode)pd_batchcode,max(pd_prodcode)pd_prodcode,max(pd_whcode)pd_whcode,pd_piid,pd_batchid,sum(pd_outqty)pd_outqty "+
								" from prodiodetail left join prodinout on pi_id=pd_piid where pi_id in("+ids+") group by pd_piid,pd_batchid ) A "+
								" left join SCM_BARCODEIO_OUT_VIEW ON PD_PIID=BI_PIID AND BI_BATCHID=PD_BATCHID where  "+
								" pd_batchid=?",ba_id);
				if (rs0.next() && rs0.getInt("cn") > 0 ) {
					 pi_ids = rs0.getString("pi_ids");
					 restqty = rs0.getDouble("restqty");
					 judgeStatus(pi_ids);
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属批次[" + ba_code + "]或者仓库["+whcode+"]不在该出库单中");
				}
				if(restqty<=0){
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属物料[" +pr_code + "]已完成采集！");
				}
				//更新条码在操作中
				baseDao.execute("update barcode set bar_usingtime=sysdate where bar_id=?",bar_id);	
				// 查看是否为湿敏元件
				Object ob = baseDao.getFieldDataByCondition("barcode left join product on pr_code=bar_prodcode", "pr_code", "bar_code='"
						+ barcode + "' and nvl(pr_msdlevel,' ') not in('1',' ') and pr_ismsd='Y'");
				if(!msdcheck && (ob == null ||("").equals(ob))){
					if(remain > restqty){
						//更新条码在操作中，
						baseDao.execute("update barcode set bar_usingtime='' where bar_id=?",bar_id);
						rMap.put("ISMSD", false);
						rMap.put("RESTQTY", restqty);
						rMap.put("BAR_REMAIN", remain);
						rMap.put("BAR_CODE", barcode);
					    Map<String, Object> map = new HashMap<String, Object>();
					    map.put("barcode", rMap);
					    map.put("next",null);
						return map;
					}
				}else if(!msdcheck && ob != null){// 如果是湿敏元件,返回湿敏元件的相关记录
						//更新条码在操作中，
						baseDao.execute("update barcode set bar_usingtime='' where bar_id=?",bar_id);
						rMap.put("ISMSD", true);
						rMap.put("RESTQTY", restqty);
						rMap.put("BAR_REMAIN", remain);
						rMap.put("msd", pdaMsdService.loadMSDLog(barcode));
					    Map<String, Object> map = new HashMap<String, Object>();
					    map.put("barcode", rMap);
					    map.put("next",null);
						return map;
			    }
				String[] tempids = pi_ids.split(",");
				length = tempids.length;
				int [] piids = new int [length];
				for(int i=0;i<length;i++){
					piids[i]=Integer.parseInt(tempids[i]);
				}
				Arrays.sort(piids);//升序排序
				double remains = remain,rqty=0;
				List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
				StringBuffer inoutnos = new StringBuffer();
				for(int i=0;i<length;i++){
					if(remains>0){
						SqlRowList rsm = baseDao.queryForRowSet(" select pd_batchcode,pd_prodcode, pd_whcode,pi_class,pi_inoutno,pd_batchid,pd_outqty-nvl(pdaqty,0)restqty from "
										+" (SELECT MAX(pd_batchcode)pd_batchcode,max(pd_prodcode)pd_prodcode,max(pd_whcode)pd_whcode,"
										+ " max(pi_class)pi_class,max(pi_inoutno)pi_inoutno,pd_batchid,sum(pd_outqty)pd_outqty ,"
										+" (select sum(pdaqty) from SCM_BARCODEIO_OUT_VIEW where bi_piid=? and bi_batchid=?)pdaqty "
										+" from prodiodetail left join prodinout on pi_id=pd_piid where pi_id=? and pd_batchid=? group by pd_batchid) where  pd_outqty-nvl(pdaqty,0)>0",piids[i],ba_id,piids[i],ba_id);
					    if(rsm.next()){
					    	pi_inoutno = rsm.getString("pi_inoutno");
					    	inoutnos.append(pi_inoutno).append(",");
					    	rqty = rsm.getDouble("restqty");
					    	if(rqty<remains || (rqty>=remains && i!=0)){
						    	Map<String,Object> map =  new HashMap<String, Object>();
						    	map.put("pi_id", piids[i]);
						    	map.put("pi_inoutno", pi_inoutno);
						    	if(rqty<remains){
						    	     map.put("restqty", rqty);
						    	     remains = NumberUtil.sub(remains,rqty);
						    	}else{
						    		 map.put("restqty", remains);
							    	 remains = 0;
						    	}
						    	map.put("pi_class", rsm.getString("pi_class"));
						    	lists.add(map);
					    	}else{
					    		//无需拆
					    		pi_class = rsm.getString("pi_class");
					    		baseDao.execute(INSERTBARCODEB,new Object[]{baseDao.getSeqId("BARCODEIO_SEQ"),barcode,"",piids[i],pi_inoutno,pr_code,whcode,remain,
					    				 rs.getObject("bar_madedate"),rs.getString("bar_location"),rs.getInt("pr_id"),ba_code,pi_class,bar_id,"",SystemSession.getUser().getEm_name(),ba_id});
								baseDao.execute("update barcode set bar_status=2 ,bar_lastoutdate=sysdate,bar_outno=? where bar_id=?",pi_inoutno,bar_id); 	
								remains = 0;
								rMap.put("PI_INOUTNO", pi_inoutno);
								rMap.put("BAR_CODE", barcode);
								rMap.put("BAR_REMAIN", remain);
								rList.add(rMap);
								if(updateOutStatus(piids[i])){
									rMessage.append(pi_inoutno+",");
								}
					    	}
					    }
					}
				}
				if(lists.size()>0){//分配给多个出库单号。获取新条码
					breakingBatch(barcode,lists,inoutnos.toString());
			        for(Map<String,Object>tmp : lists){
			    		List<String>sqls = new ArrayList<String>();
			        	pi_id = Integer.valueOf(tmp.get("pi_id").toString());
			        	pi_inoutno = tmp.get("pi_inoutno").toString();
						sqls.add(INSERTINTOBARCODEIODEAL+"'"+SystemSession.getUser().getEm_name()+"','"+tmp.get("bar_code")+"','',"+pi_id+",'"+pi_inoutno+"','"+pr_code+"','"+whcode+"',"+tmp.get("restqty")+","+DateUtil.parseDateToOracleString(Constant.YMD_HMS, rs.getDate("bar_madedate"))+",'"
									+rs.getObject("bar_location")+"',"+rs.getInt("pr_id")+",2,'"+ba_code+"',"+ba_id+",'"+tmp.get("pi_class")+"',"+tmp.get("bar_id")+",'"+tmp.get("bar_lastcode")+"','')");
						sqls.add("update barcode set bar_status=2 ,bar_lastoutdate=sysdate,bar_outno='"+pi_inoutno+"' where bar_id="+tmp.get("bar_id"));
						baseDao.execute(sqls);
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("PI_INOUTNO", pi_inoutno);
						map.put("BAR_CODE", tmp.get("bar_code"));
						map.put("BAR_REMAIN", tmp.get("restqty"));
						rList.add(map);
						//更新备料状态					
						if(updateOutStatus(pi_id)){
							rMessage.append(pi_inoutno+",");
						}
			        }
				}
				baseDao.execute("update barcode set bar_usingtime='' where bar_id=?",bar_id);
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码[" + barcode + "]不存在或者不是在库状态");
			}
		} 
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("barcode",rs.getCurrentMap());  
	    map.put("nolist", rList);
	    try{ 
	    	map.put("next",getNextByBatchDeal(ids));	//提示用户下一条采集数据
	    }
	    catch (Exception e) {
	    	map.put("next", null);		
		}   
	    if(rMessage.length()>0){
	    	map.put("finishno", "出库单号："+rMessage.toString()+"已完成备料！");
	    }else{
	    	map.put("finishno",null);
	    }
		return map;
	} 
	
	public synchronized List<Map<String,Object>> breakingBatch(String barcode,List<Map<String,Object>> list,String inoutnos){
		SqlRowList rs = baseDao.queryForRowSet("select barcode.*,ve_id from barcode left join Vendor  on bar_vendcode=ve_code where bar_code=? and bar_status=1", barcode);
		if(rs.next()){
			String or_barid = rs.getString("bar_id");
			String ve_id = rs.getGeneralString("ve_id");
			double remain = rs.getDouble("bar_remain");
			int num = list.size();
			//判断状态，锁库状态，数量
			Map<String, Object> mp1 = rs.getCurrentMap();
			mp1.remove("BAR_PRINTCOUNT");
			mp1.remove("BAR_LASTPRINTMAN");
			mp1.remove("BAR_LASTPRINTDATE");
			mp1.remove("BAR_USINGTIME");
			mp1.remove("VE_ID");
			//原条码是否作废,true 作废，否则不作废
			boolean bo = baseDao.isDBSetting("BarCodeSetting", "BarInvalidAfBatch");
			String newCode;
			List<String>sqls = new ArrayList<String>();
			for(int i=0;i<num;i++){
				if(i==num-1 && !bo){//不作废
					sqls.add("update barcode set bar_remain="+list.get(i).get("restqty")+" where bar_id="+or_barid);
					list.get(i).put("bar_id", or_barid);
					list.get(i).put("bar_code", barcode);
					list.get(i).put("bar_lastcode","");				
				}else{
					int bar_id = baseDao.getSeqId("BARCODE_SEQ");
					newCode = verifyApplyDao.barcodeMethod(mp1.get("bar_prodcode").toString(),ve_id,0);// 生成条码
					mp1.put("BAR_LASTCODE", barcode);
					mp1.put("BAR_LASTID", or_barid);
					mp1.put("BAR_ID", bar_id);
					mp1.put("BAR_KIND", "1");// 类型为分拆 ：1,合并：2，原始：0
					mp1.put("BAR_CODE", newCode);
					mp1.put("BAR_REMAIN", list.get(i).get("restqty"));
					mp1.put("BAR_STATUS", "1");
					mp1.put("BAR_RECORDDATE", DateUtil.format(null, "yyyy-MM-dd HH:mm:ss"));
					sqls.add(SqlUtil.getInsertSqlByMap(mp1, "barcode"));
					sqls.add("insert into barcodeChange(bc_id,bc_prodcode,bc_kind,bc_indate,bc_inman,"
							+ "bc_reason,bc_qty,bc_barcode,bc_barid,bc_newbarcode,bc_newbarid,bc_newqty) "
							+ "values(barcodeChange_seq.nextval,'"+ mp1.get("bar_prodcode")+"',1,sysdate,'"+ SystemSession.getUser().getEm_name()+ "'"
							+ ",'多工单:"+inoutnos+"备料自动拆分',"+ remain+ ",'"+ barcode+ "',"+ or_barid+ ",'"+ newCode+ "',"+ bar_id+ ","+list.get(i).get("restqty")+ ")");
					list.get(i).put("bar_id", bar_id);
					list.get(i).put("bar_code", newCode);
					list.get(i).put("bar_lastcode", barcode);
					if(i==num-1 && bo){
					   sqls.add("update barcode set bar_status=-2 where bar_id="+or_barid);
					}
				}
			}
			baseDao.execute(sqls);
		}
		return list;
	}
	
	
	@Override
	public Map<String, Object> getNextByBatchDeal(String ids) {
		String ioType =baseDao.getDBSetting("BarCodeSetting", "BarcodeInOutType");	
		String sql;
		if(("byAll").equals(ioType) || ("byWhcode").equals(ioType)){
			   sql = "select * from (select pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_batchcode,pd_restqty,pr_zxbzs,bar_location,bar_remain,ROW_NUMBER() OVER(partition by pd_batchcode order by bar_location asc) as RN from "
						+" (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_batchcode,pd_outqty-nvl(pdaqty,0)pd_restqty,pd_batchid,pr_zxbzs "
						+" FROM (SELECT MAX(pd_batchcode)pd_batchcode,max(pd_prodcode)pd_prodcode,pd_batchid,sum(pd_outqty)pd_outqty "
						+" from prodiodetail where pd_piid in("+ids+") group by pd_batchid) A LEFT JOIN batch on ba_id=pd_batchid "
						+" left join (select bi_batchid,nvl(sum(nvL(pdaqty,0)),0)pdaqty from SCM_BARCODEIO_OUT_VIEW where BI_PIID in("+ids+") "
						+" group by BI_BATCHID) on bi_batchid=pd_batchid left join product on pr_code=pd_prodcode where nvl(ba_hasbarcode,0)<>0 and pd_outqty>nvl(pdaqty,0))T "
						+" LEFT JOIN barcode ON  pd_prodcode=bar_prodcode AND pd_batchid=bar_batchid "
						+" where nvl(bar_outno,' ')=' ' AND bar_status=1 order by pd_prodcode)T1 where T1.RN=1 and rownum=1";
			}else{
				sql="select * from (select pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_batchcode,pd_restqty,pr_zxbzs,bar_location,bar_remain,ROW_NUMBER() OVER(partition by pd_batchcode order by bar_location asc) as RN from "
						+" (SELECT pd_prodcode,pr_detail,pr_spec,pd_outqty,pd_batchcode,pd_outqty-nvl(pdaqty,0)pd_restqty,pd_batchid,pr_zxbzs "
						+" FROM (SELECT MAX(pd_batchcode)pd_batchcode,max(pd_prodcode)pd_prodcode,pd_batchid,sum(pd_outqty)pd_outqty "
						+" from prodiodetail where pd_piid in("+ids+") group by pd_batchid) A LEFT JOIN batch on ba_id=pd_batchid "
						+" left join (select bi_batchid,nvl(sum(nvL(pdaqty,0)),0)pdaqty from SCM_BARCODEIO_OUT_VIEW where BI_PIID in("+ids+") "
						+" group by BI_BATCHID) on bi_batchid=pd_batchid left join product on pr_code=pd_prodcode where nvl(ba_hasbarcode,0)<>0 and pd_outqty>nvl(pdaqty,0)  )T "   //and pr_tracekind>0
						+" LEFT JOIN barcode ON pd_prodcode=bar_prodcode AND pd_batchid=bar_batchid "
						+" where nvl(bar_outno,' ')=' ' AND bar_status=1 order by pd_prodcode)T1 where T1.RN=1 and rownum=1";
			}
			SqlRowList rs = baseDao.queryForRowSet(sql);
			if(rs.next()){
				return rs.getCurrentMap();
			}else{
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"没有需要采集的明细，或已经采集完成");
			}
	}

	@Override
	public List<Map<String, Object>> deleteDetailDeal(String ids, String barcode,String outboxcode) {
		//撤销备料传的是拆分后的条码，按照原处理方式
		//传的是原条码，找到ids进行撤销，返回数据值
		String inoutno = null,newbarcode;
		int pi_id ,newbarid,barid = 0;
		SqlRowList rs1 = null;
		double outqty = 0;
		boolean bo = true;
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		if(barcode != null && !("").equals(barcode)){
			  judgeStatus(ids);
			  SqlRowList rs = baseDao.queryForRowSet("select bi_piid,bi_inoutno,bi_outqty,bi_whcode,bi_prodcode,bi_batchcode,bi_barcode barcode,bi_barid barid,pr_detail,pr_spec from barcodeio left join product on pr_code=bi_prodcode left join prodinout on pi_id=bi_piid where bi_piid in("+ids+") and bi_barcode='"+barcode+"' and nvl(pi_statuscode,' ')<>'POSTED'");
			  if(rs.next()){
				   bo = false;
				   inoutno = rs.getString("bi_inoutno");
				   outqty = rs.getDouble("bi_outqty");
				   pi_id = rs.getInt("bi_piid");
				   barid = rs.getInt("barid");
				   rs1 = baseDao.queryForRowSet("select bar_code,bar_status,bar_outno,bar_remain,bar_prodcode,pr_detail,pr_spec,bar_batchcode,bar_location from barcode left join product on bar_prodcode=pr_code where bar_id=?",barid);
				   if(rs1.next()){
						if(rs1.getInt("bar_status")!=2 || !inoutno.equals(rs1.getString("bar_outno"))){
							throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销出库的条码未被采集");
						}
						if(rs1.getDouble("bar_remain")!= outqty){
							throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销出库的条码库存数量["+rs1.getDouble("bar_remain")+"]与出库备料的数量["+outqty+"]不一致");
						}
				   }else{
						throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销出库的条码不存在");
				   }
				 //增加操作日志记录
				  baseDao.execute("insert into barcodelogger(bl_id,bl_barcode,bl_barid,bl_prodcode,bl_whcode,bl_date,bl_inman,bl_action,bl_description,bl_inoutno)"
					 		+ "values(barcodelogger_seq.nextval,'"+barcode+"',"+barid+",'"+rs1.getString("bar_prodcode")+"','"+rs.getString("bi_whcode")+"',sysdate,'"+SystemSession.getUser().getEm_name()+"','撤销出库','出库单号："+inoutno+",数量："+outqty+"','"+inoutno+"')");
				  baseDao.execute("delete from barcodeIo where bi_piid=? and bi_barcode=?",pi_id,barcode);
				  baseDao.execute("update barcode set bar_status=1,bar_outno='' where bar_code=? and bar_outno=?",barcode,inoutno);
				  //更新备料状态
				  baseDao.execute("update prodinout set pi_pdastatus='备料中' where pi_id=? and exists (select 1 from barcodeio where bi_piid=pi_id) ",pi_id);
				  baseDao.execute("update prodinout set pi_pdastatus='未备料' where pi_id=? and not exists (select 1 from barcodeio where bi_piid=pi_id) ",pi_id);
				  returnList.add(rs.getCurrentMap());
			  }
			  rs = baseDao.queryForRowSet("select A.bar_id baraid,bi_piid,bi_inoutno,bi_outqty,bi_whcode,bi_prodcode,bi_batchcode,bi_barid,B.bar_code barcode,B.bar_id barid "
			  				+ "from barcode A left join barcode B on B.bar_lastcode=A.bar_code left join barcodeio on bi_barcode=B.bar_code left join prodinout on pi_id = bi_piid where A.bar_code='"+barcode+"' and bi_piid in("+ids+") and A.bar_status=1 and B.BAR_KIND=1 and nvl(pi_statuscode,' ')<>'POSTED'");
			  if(rs.hasNext()){//原条码条码作废不允许撤销
				  bo = false;
				  while(rs.next()){
					   barid = rs.getInt("baraid");
					   inoutno = rs.getString("bi_inoutno");
					   outqty = rs.getDouble("bi_outqty");
					   pi_id = rs.getInt("bi_piid");
					   newbarid = rs.getInt("barid");
					   newbarcode = rs.getString("barcode");
					   rs1 = baseDao.queryForRowSet("select bar_code,bar_status,bar_outno,bar_remain,bar_prodcode,pr_detail,pr_spec,bar_batchcode,bar_location from barcode left join product on bar_prodcode=pr_code where bar_id=?",rs.getInt("bi_barid"));
					   if(rs1.next()){
							if(rs1.getInt("bar_status")!=2 || !inoutno.equals(rs1.getString("bar_outno"))){
								throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤出库的条码未被采集");
							}
							if(rs1.getDouble("bar_remain")!= outqty){
								throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤出库的条码库存数量["+rs1.getDouble("bar_remain")+"]与出库备料的数量["+rs1.getDouble("bi_outqty")+"]不一致");
							}
					   }else{
							throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"需要撤销出库的条码不存在");
					   }
					 //增加操作日志记录
					  baseDao.execute("insert into barcodelogger(bl_id,bl_barcode,bl_barid,bl_prodcode,bl_whcode,bl_date,bl_inman,bl_action,bl_description,bl_inoutno)"
						 		+ "values(barcodelogger_seq.nextval,'"+newbarcode+"',"+newbarid+",'"+rs1.getString("bar_prodcode")+"','"+rs.getString("bi_whcode")+"',sysdate,'"+SystemSession.getUser().getEm_name()+"','撤销出库','还原至原条码出库单号："+inoutno+",数量："+outqty+"','"+inoutno+"')");
					  baseDao.execute("delete from barcodeIo where bi_piid=? and bi_barcode=?",pi_id,newbarcode);
					  baseDao.execute("update barcode set bar_status=-2,bar_outno='' where bar_id=? and bar_outno=?",newbarid,inoutno);
					  //将数量返回至原条码中
					  baseDao.execute("update barcode set bar_remain=bar_remain+?,bar_status=1 where bar_id=?",outqty,barid);
					  //更新备料状态
					  baseDao.execute("update prodinout set pi_pdastatus='备料中' where pi_id=? and exists (select 1 from barcodeio where bi_piid=pi_id) ",pi_id);
					  baseDao.execute("update prodinout set pi_pdastatus='未备料' where pi_id=? and not exists (select 1 from barcodeio where bi_piid=pi_id) ",pi_id);
					  returnList.add(rs.getCurrentMap());
				  }
			  }
			  if(bo){
				  throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"撤销的条码不存在该出库单已备料列表中或出库单已过账");
			  }
       }
	   return returnList;
	}
	
	@Override
	public Map<String, Object> specialOutDeal(String barcode, String reason, String ids) {
		double restqty=0,remain=0;
		String pi_ids, pr_code,whcode,ba_code,pi_inoutno;
		int length,pi_id,ba_id,bar_id;
		Map<String, Object> rMap = new HashMap<String, Object>();
		List<Map<String,Object>> rList = new ArrayList<Map<String,Object>>();
		StringBuffer rMessage = new StringBuffer();
		if(!StringUtil.hasText(reason)){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,"请选择特殊出库原因！");
		}
		judgeStatus(ids);
		SqlRowList rs = baseDao.queryForRowSet("select bar_status,bar_prodcode,pr_detail,pr_spec,pr_id,bar_batchcode,bar_whcode,bar_remain,bar_id,bar_location,bar_madedate,bar_lastcode,bar_lockstatus,bar_batchid, case when bar_usingtime is null then 1 when bar_usingtime+5/(24*60)<=sysdate then 1 else 0 end bar_using "
						+"from barcode left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1",barcode);
		if(rs.next()){
			if(rs.getGeneralInt("bar_using")==0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"不允许同时操作一个条码，请稍后处理，条码[" + barcode + "]");
		    }
			remain = rs.getDouble("bar_remain");
			pr_code = rs.getString("bar_prodcode");
			whcode = rs.getString("bar_whcode");
			ba_code= rs.getString("bar_batchcode");
			ba_id = rs.getInt("bar_batchid");
			bar_id = rs.getInt("bar_id");
			if(remain <= 0){
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码剩余库存必须大于0");
			}
			//判断条码是否被冻结
			if(rs.getGeneralInt("bar_lockstatus") == -1){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]已冻结，不允许采集");
			}
			if (remain <= 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]库存数为0");
			}
			SqlRowList rs0 = baseDao.queryForRowSet("select wm_concat(pd_piid)pi_ids,count(1)cn,sum(pd_outqty)-sum(nvl(pdaqty,0))restqty from "
							      + " (SELECT pd_piid,sum(pd_outqty)pd_outqty from prodiodetail left join prodinout on pi_id=pd_piid where pi_id in("+ids+") and pd_prodcode=? and pd_whcode=? group by pd_piid)"
							      + " left join (select bi_piid,sum(nvl(pdaqty,0))pdaqty from SCM_BARCODEIO_OUT_VIEW where bi_piid in("+ids+") and bi_prodcode=?  and bi_whcode=?  group by bi_piid)"
							      + " on bi_piid=pd_piid",pr_code,whcode,pr_code,whcode);
			if (rs0.next() && rs0.getInt("cn") > 0 ) {
				 pi_ids = rs0.getString("pi_ids");
				 restqty = rs0.getDouble("restqty");   
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属物料[" +pr_code + "]或者仓库["+whcode+"]不在该出库单中");
			}
			if(restqty<=0){
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属物料[" +pr_code + "]已完成采集！");
			}
			if(restqty < remain){
				//如果条码库存数量大于剩余需要出库的数量，返回条码数量，剩余需要出库的数量，条码号
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("BAR_CODE", barcode);
				map.put("BAR_REMAIN", remain);
				map.put("RESTQTY", restqty);
				return map;
			}else{
				//更新条码在操作中
				baseDao.execute("update barcode set bar_usingtime=sysdate where bar_id=?",bar_id);	
				//分配拆分
				String[] tempids = pi_ids.split(",");
				length = tempids.length;
				int [] piids = new int [length];
				for(int i=0;i<length;i++){
					piids[i]=Integer.parseInt(tempids[i]);
				}
				Arrays.sort(piids);//升序排序
				double remains = remain,rqty=0;
				StringBuffer inoutnos = new StringBuffer();
				List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
				for(int i=0;i<length;i++){
					if(remains>0){
						SqlRowList rsm = baseDao.queryForRowSet("select pi_inoutno,pd_outqty-nvl(pdaqty,0)restqty,pi_class from (SELECT pd_piid,sum(pd_outqty)pd_outqty,max(pi_class)pi_class,max(pi_inoutno)pi_inoutno"
													+" from prodiodetail left join prodinout on pi_id=pd_piid where pi_id=? and pd_prodcode=? and pd_whcode=? group by pd_piid) "
													+" left join (select bi_piid,sum(pdaqty)pdaqty from SCM_BARCODEIO_OUT_VIEW where bi_piid=? and bi_prodcode=? and bi_whcode=? group by bi_piid) on bi_piid=pd_piid where pd_outqty>nvl(pdaqty,0)",piids[i],pr_code,whcode,piids[i],pr_code,whcode);
					    if(rsm.next()){
					    	pi_inoutno = rsm.getString("pi_inoutno");
					    	rqty = rsm.getDouble("restqty");
					    	inoutnos.append(pi_inoutno).append(",");
					    	if(rqty<remains || (rqty>=remains && i!=0)){
						    	Map<String,Object> map =  new HashMap<String, Object>();
						    	map.put("pi_id", piids[i]);
						    	map.put("pi_inoutno", pi_inoutno);
						    	if(rqty<remains){
						    	     map.put("restqty", rqty);
						    	     remains = NumberUtil.sub(remains,rqty);
						    	}else{
						    		 map.put("restqty", remains);
							    	 remains = 0;
						    	}
						    	map.put("pi_class", rsm.getString("pi_class"));
						    	lists.add(map);
					    	}else{
					    		//无需拆
					    		baseDao.execute("insert into barcodeio (bi_id,bi_barcode,bi_outboxcode,bi_piid,bi_inoutno,bi_status,bi_printstatus,"
										+ " bi_prodcode,bi_whcode,bi_outqty,bi_madedate,bi_location,bi_prodid,bi_pdaget,bi_batchcode,bi_piclass,bi_barid,bi_type,bi_oldbarcode,bi_inman,bi_batchid,bi_reason)"
										+ "values(?,?,?,?,?,99,0,?,?,?,?,?,?,2,?,?,?,'批次汇总',?,?,?,?)",new Object[]{baseDao.getSeqId("BARCODEIO_SEQ"),barcode,"",piids[i],pi_inoutno,pr_code,whcode,remain,
										rs.getObject("bar_madedate"),rs.getString("bar_location"),rs.getInt("pr_id"),ba_code,rsm.getString("pi_class"),bar_id,"",SystemSession.getUser().getEm_name(),ba_id,reason});
								baseDao.execute("update barcode set bar_status=2 ,bar_lastoutdate=sysdate,bar_outno=? where bar_id=?",pi_inoutno,bar_id); 	
								remains = 0;
								rMap.put("PI_INOUTNO", pi_inoutno);
								rMap.put("BAR_CODE", barcode);
								rMap.put("BAR_REMAIN", remain);
								rList.add(rMap);
								if(updateOutStatus(piids[i])){
									rMessage.append(pi_inoutno+",");
								}
					    	}
					    }
					}
				}
				if(lists.size()>0){//分配给多个出库单号。获取新条码
					breakingBatch(barcode,lists,inoutnos.toString());
			        for(Map<String,Object>tmp : lists){
			    		List<String>sqls = new ArrayList<String>();
			    		pi_id = Integer.valueOf(tmp.get("pi_id").toString());
			        	pi_inoutno = tmp.get("pi_inoutno").toString();
						sqls.add(INSERTINTOBARCODEIODEAL+"'"+SystemSession.getUser().getEm_name()+"','"+tmp.get("bar_code")+"','',"+pi_id+",'"+pi_inoutno+"','"+pr_code+"','"+whcode+"',"+tmp.get("restqty")+","+DateUtil.parseDateToOracleString(Constant.YMD_HMS, rs.getDate("bar_madedate"))+",'"
									+rs.getObject("bar_location")+"',"+rs.getInt("pr_id")+",2,'"+ba_code+"',"+ba_id+",'"+tmp.get("pi_class")+"',"+tmp.get("bar_id")+",'"+tmp.get("bar_lastcode")+"','"+reason+"')");
						sqls.add("update barcode set bar_status=2 ,bar_lastoutdate=sysdate,bar_outno='"+pi_inoutno+"' where bar_id="+tmp.get("bar_id"));
						baseDao.execute(sqls);
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("PI_INOUTNO", pi_inoutno);
						map.put("BAR_CODE", tmp.get("bar_code"));
						map.put("BAR_REMAIN", tmp.get("restqty"));
						rList.add(map);
						//更新备料状态					
						if(updateOutStatus(pi_id)){
							rMessage.append(pi_inoutno+",");
						}
			        }
				}	
				//更新条码完成操作
				baseDao.execute("update barcode set bar_usingtime='' where bar_id=?",bar_id);	
			}
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码不存在或不是在库状态");
		}
		 Map<String, Object> map = new HashMap<String, Object>();
	     map.put("barcode",rs.getCurrentMap());  
	     map.put("nolist", rList);
	     if(rMessage.length()>0){
	    	map.put("finishno", "出库单号："+rMessage.toString()+"已完成备料！");
	     }else{
	    	 map.put("finishno",null);
	     }
		 return map;
		} 
	
	private boolean updateOutStatus(Integer piid){
		//更新备料状态					
		boolean status = false;
		SqlRowList rs = baseDao.queryForRowSet("select count(1)cn from barcodeio where bi_piid=? and bi_outqty>0",piid);
		if(rs.next() && rs.getInt("cn") > 0){ //有一行以上备料记录则是备料中
			baseDao.execute("update prodinout set pi_pdastatus ='备料中' where pi_id=?",piid);
			rs = baseDao.queryForRowSet("select count(1) cn from (select pd_prodcode,pd_whcode,sum(pd_outqty)qty from prodiodetail left join batch on pd_batchid=ba_id "
			        + " where pd_piid=? and ba_hasbarcode<>0 group by pd_prodcode,pd_whcode)A left join (select bi_prodcode,bi_whcode,sum(bi_outqty)qty "
			        +" from barcodeio where bi_piid=? group by bi_prodcode,bi_whcode)B on (pd_prodcode=bi_prodcode and pd_whcode=bi_whcode) where A.qty>NVL(B.qty,0)",piid,piid);
			if (rs.next() && rs.getInt("cn") == 0) {
				baseDao.execute("update prodinout set pi_pdastatus ='已备料' where pi_id=?", piid);
				status = true;
			}
		}
		return status;
	}

	@Override
	public List<Map<String, Object>> getHaveSubmitListDeal(String ids,Integer page, Integer pagesize) {
		SqlRowList rs = null;
		int start = ((page - 1) * pagesize + 1);
		int end = page * pagesize;
		rs = baseDao
				.queryForRowSet("select * from (select tt.*,rownum rn from ( select NVL(bi_barcode,'') bi_barcode,bi_prodcode ,bi_inoutno,bi_piid,"
						+" bi_pdid,bi_whcode,NVL(bi_inqty,0) bi_inqty,NVL(bi_outqty,0) bi_outqty,pr_detail,pr_id,pr_spec,bi_location,bi_batchcode from barcodeio left join product on bi_prodcode=pr_code "
                        +" where bi_piid in ("+ids+")  group by bi_barcode,bi_prodcode ,bi_inoutno,bi_piid,bi_pdid,bi_whcode, bi_inqty, bi_outqty,pr_detail,"
                        +" pr_id,pr_spec,bi_location,bi_batchcode order by bi_prodcode,bi_batchcode desc  ) tt	where rownum<= "+end+")where rn>="+start);
		if(rs.next()){
		     return rs.getResultList();
		}else {
			List<Map<String,Object>> rList = new ArrayList<Map<String,Object>>();
			return rList;
		}
	}

	 @Override
	 public synchronized Map<String, Object> outByBatchBreakingDeal(String ids,String barcode,Double or_remain,Double bar_remain,String type) {
			// 按批次数量汇总采集，type 采集的是箱号或条码
			SqlRowList rs = null,rs0;
			Map<String, Object> rMap = new HashMap<String, Object>();
			List<Map<String,Object>> rList = new ArrayList<Map<String,Object>>();
			String ba_code,whcode = null,pi_ids=null,pr_code,pi_inoutno;
			Integer pi_id,ba_id,bar_id;
			StringBuffer rMessage = new StringBuffer();
			int length;
			double remain = 0, restqty=0,zxbzs;
			if ("barcode".equals(type)){// 条码,判断条码是否存在，条码状态，条码的剩余数量，条码所属仓库是否正确
				//判断是否bar_remain>最小包数量，如果大于，判断是否还有相同批次的其他尾数条码，如果有则需要限制用户必须采集其他尾数条码
				rs = baseDao.queryForRowSet("select bar_id,bar_code,bar_prodcode,bar_whcode,bar_remain,nvl(bar_status,0) bar_status,pr_id,pr_detail,pr_spec,bar_batchcode,bar_batchid,bar_outboxcode1,bar_location,bar_lastcode,nvl(bar_lockstatus,0) bar_lockstatus,bar_madedate, case when bar_usingtime is null then 1 when bar_usingtime+5/(24*60)<=sysdate then 1 else 0 end bar_using,nvl(pr_zxbzs,0)pr_zxbzs from barcode left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1",barcode);
				if (rs.next()) {
					ba_code = rs.getString("bar_batchcode");
					remain = rs.getGeneralDouble("bar_remain");
					whcode = rs.getString("bar_whcode");
					pr_code = rs.getString("bar_prodcode");
					zxbzs = rs.getGeneralDouble("pr_zxbzs");
					if(zxbzs>0 && remain>=zxbzs){
						rs0 = baseDao.queryForRowSet("select count(1)cn from barcode where bar_batchcode=? and bar_prodcode=? and bar_whcode=? and bar_remain<? and bar_status=1 and bar_remain>0 and nvl(bar_lockstatus,0)=0",ba_code,pr_code,whcode,zxbzs);
						if(rs0.next() && rs0.getInt("cn")>0){
							throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请先采集本批号["+ba_code+"]的尾数条码");
						}
					}
					if(rs.getGeneralInt("bar_using")==0){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"不允许同时操作一个条码，请稍后处理，条码[" + barcode + "]");
					}									
					ba_id = rs.getInt("bar_batchid");
					bar_id = rs.getInt("bar_id");
					//判断条码是否被冻结
					if(rs.getInt("bar_lockstatus") == -1){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]已冻结，不允许采集");
					}
					if(remain != or_remain){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]已被拆分!");
					}
					if(remain < bar_remain){
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]库存数小于拆分数"+bar_remain);
					}
					if(remain <= 0) {
						throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]库存数为0");
					}
					rs0 = baseDao.queryForRowSet("SELECT wm_concat(pd_piid)pi_ids,count(1)cn,sum(pd_outqty)-sum(nvl(pdaqty,0))restqty "+
									" FROM (SELECT MAX(pd_batchcode)pd_batchcode,max(pd_prodcode)pd_prodcode,max(pd_whcode)pd_whcode,pd_piid,pd_batchid,sum(pd_outqty)pd_outqty "+
									" from prodiodetail left join prodinout on pi_id=pd_piid where pi_id in("+ids+") group by pd_piid,pd_batchid ) A "+
									" left join SCM_BARCODEIO_OUT_VIEW ON PD_PIID=BI_PIID AND BI_BATCHID=PD_BATCHID where  "+
									" pd_batchid=?",ba_id);
					if (rs0.next() && rs0.getInt("cn") > 0 ) {
						 pi_ids = rs0.getString("pi_ids");
						 restqty = rs0.getDouble("restqty");
					}else{
						throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属批次[" + ba_code + "]或者仓库["+whcode+"]不在该出库单中");
					}
					if(restqty<=0){
						throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属物料[" +pr_code + "]已完成采集！");
					}
					if(bar_remain > restqty){
						rMap.put("ISMSD", false);
						rMap.put("RESTQTY", restqty);
						rMap.put("BAR_REMAIN", remain);
						rMap.put("BAR_CODE", barcode);
					    Map<String,Object> map = new HashMap<String, Object>();
					    map.put("barcode", rMap);
					    map.put("next",null);
						return map;
					}
					//更新条码在操作中
					baseDao.execute("update barcode set bar_usingtime=sysdate where bar_id=?",bar_id);	
					
					String[] tempids = pi_ids.split(",");
					length = tempids.length;
					int [] piids = new int [length];
					for(int i=0;i<length;i++){
						piids[i]=Integer.parseInt(tempids[i]);
					}
					Arrays.sort(piids);//升序排序
					double remains = bar_remain,rqty=0;
					StringBuffer inoutnos = new StringBuffer();
					List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
					for(int i=0;i<length;i++){
						if(remains>0){
							SqlRowList rsm = baseDao.queryForRowSet(" select pd_batchcode,pd_prodcode, pd_whcode,pi_class,pi_inoutno,pd_batchid,pd_outqty-nvl(pdaqty,0)restqty from "
											+" (SELECT MAX(pd_batchcode)pd_batchcode,max(pd_prodcode)pd_prodcode,max(pd_whcode)pd_whcode,"
											+" max(pi_class)pi_class,max(pi_inoutno)pi_inoutno,pd_batchid,sum(pd_outqty)pd_outqty ,"
											+" (select sum(pdaqty) from SCM_BARCODEIO_OUT_VIEW where bi_piid=? and bi_batchid=?)pdaqty "
											+" from prodiodetail left join prodinout on pi_id=pd_piid where pi_id=? and pd_batchid=?  group by pd_batchid)where  pd_outqty-nvl(pdaqty,0)>0",piids[i],ba_id,piids[i],ba_id);
						    if(rsm.next()){
						    	pi_inoutno = rsm.getString("pi_inoutno");
						    	rqty = rsm.getDouble("restqty");
						    	inoutnos.append(pi_inoutno).append(",");
						    	Map<String,Object> map =  new HashMap<String, Object>();
						    	map.put("pi_id", piids[i]);
						    	map.put("pi_inoutno", pi_inoutno);
						    	map.put("pi_class", rsm.getString("pi_class"));
						    	if(rqty<remains){
						    		 map.put("restqty", rqty);
						    	     remains = NumberUtil.sub(remains,rqty);
						    	}else{
						    		 map.put("restqty", remains);
							    	 remains = 0;
						    	}
						    	lists.add(map);
						    }
						}
					}
					if(lists.size()>0){//分配给多个出库单号。获取新条码
						Map<String,Object> map1 =  new HashMap<String, Object>();
						map1.put("restqty",NumberUtil.sub(remain,bar_remain));
						lists.add(map1);
						breakingBatch(barcode,lists,inoutnos.toString());
				        for(Map<String,Object>tmp : lists){
				        	if(StringUtil.hasText(tmp.get("pi_inoutno"))){
				        		List<String>sqls = new ArrayList<String>();
					        	pi_id = Integer.valueOf(tmp.get("pi_id").toString());
					        	pi_inoutno = tmp.get("pi_inoutno").toString();
								sqls.add(INSERTINTOBARCODEIODEAL+"'"+SystemSession.getUser().getEm_name()+"','"+tmp.get("bar_code")+"','',"+pi_id+",'"+pi_inoutno+"','"+pr_code+"','"+whcode+"',"+tmp.get("restqty")+","+DateUtil.parseDateToOracleString(Constant.YMD_HMS, rs.getDate("bar_madedate"))+",'"
											+rs.getObject("bar_location")+"',"+rs.getInt("pr_id")+",2,'"+ba_code+"',"+ba_id+",'"+tmp.get("pi_class")+"',"+tmp.get("bar_id")+",'"+tmp.get("bar_lastcode")+"','')");
								sqls.add("update barcode set bar_status=2 ,bar_lastoutdate=sysdate,bar_outno='"+pi_inoutno+"' where bar_id="+tmp.get("bar_id"));
								baseDao.execute(sqls);
								Map<String,Object> map = new HashMap<String, Object>();
								map.put("PI_INOUTNO", pi_inoutno);
								map.put("BAR_CODE", tmp.get("bar_code"));
								map.put("BAR_REMAIN", tmp.get("restqty"));
								rList.add(map);
								//更新备料状态					
								if(updateOutStatus(pi_id)){
									rMessage.append(pi_inoutno+",");
								}
				        	}
				        }
					}
					//更新条码完成操作
					baseDao.execute("update barcode set bar_usingtime='' where bar_id=?",bar_id);	
				} else {
					throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码[" + barcode + "]不存在或者不是在库状态");
				}
			} else {}
		    Map<String, Object> map = new HashMap<String, Object>();
		    map.put("barcode",rs.getCurrentMap());  
		    map.put("nolist", rList);
		    try{ 
		    	map.put("next",getNextByBatchDeal(ids));	//提示用户下一条采集数据
		    }
		    catch (Exception e) {
		    	map.put("next", null);		
			}   
		    if(rMessage.length()>0){
		    	map.put("finishno", "出库单号："+rMessage.toString()+"已完成备料！");
		    }else{
		    	map.put("finishno",null);
		    }
			return map;
	}

	
	@Override
	public List<Map<String, Object>> getProdOutStatus(String ids) {
		SqlRowList rs = baseDao.queryForRowSet(" select pi_id,pi_pdastatus from prodinout where pi_id in ("+ids+")");
		if(rs.next()){
			return rs.getResultList();
		}else{
			throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"工单号不存在!");
		}
	}
	
	@Override
	public Map<String, Object> specialOutBreaking(String ids,String barcode,Double or_remain,Double bar_remain,String reason) {
		double restqty=0,remain=0,zxbzs=0;
		String pi_ids, pr_code,whcode,ba_code,pi_inoutno;
		int length,pi_id,ba_id,bar_id;
		List<Map<String,Object>> rList = new ArrayList<Map<String,Object>>();
		SqlRowList rs0;
		StringBuffer rMessage = new StringBuffer();
		if(!StringUtil.hasText(reason)){
			throw new APIErrorException(APIErrorCode.ILLEGAL_ARGUMENTS,"请选择特殊出库原因！");
		}
		SqlRowList rs = baseDao.queryForRowSet("select bar_status,bar_prodcode,pr_detail,pr_spec,pr_id,bar_batchcode,bar_whcode,bar_remain,bar_id,bar_location,bar_madedate,bar_lastcode,bar_lockstatus,bar_batchid, case when bar_usingtime is null then 1 when bar_usingtime+5/(24*60)<=sysdate then 1 else 0 end bar_using,nvl(pr_zxbzs,0)pr_zxbzs "
						+" from barcode left join product on pr_code=bar_prodcode where bar_code=? and bar_status=1",barcode);
		if(rs.next()){
			remain = rs.getDouble("bar_remain");
			pr_code = rs.getString("bar_prodcode");
			whcode = rs.getString("bar_whcode");
			ba_code= rs.getString("bar_batchcode");
			zxbzs = rs.getGeneralDouble("pr_zxbzs");
			if(zxbzs>0 && remain>=zxbzs){
				rs0 = baseDao.queryForRowSet("select count(1)cn from barcode where bar_batchcode=? and bar_prodcode=? and bar_whcode=? and bar_remain<? and bar_status=1 and bar_remain>0 and nvl(bar_lockstatus,0)=0",ba_code,pr_code,whcode,zxbzs);
				if(rs0.next() && rs0.getInt("cn")>0){
					throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"请先采集本批号["+ba_code+"]的尾数条码");
				}
			}
			if(rs.getGeneralInt("bar_using")==0){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"不允许同时操作一个条码，请稍后处理，条码[" + barcode + "]");
			}
			ba_id = rs.getInt("bar_batchid");
			bar_id = rs.getInt("bar_id");
			if(remain <= 0){
				throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码剩余库存必须大于0");
			}
			//判断条码是否被冻结
			if(rs.getGeneralInt("bar_lockstatus") == -1){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]已冻结，不允许采集");
			}
			if(remain != or_remain){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]已被拆分!");
			}
			if(remain < bar_remain){
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]库存数小于拆分数"+bar_remain);
			}
			if(remain <= 0) {
				throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"条码[" + barcode + "]库存数必须大于0");
			}
			rs0 = baseDao.queryForRowSet("select wm_concat(pd_piid)pi_ids,count(1)cn,sum(pd_outqty)-sum(nvl(pdaqty,0))restqty from "
							      + " (SELECT pd_piid,sum(pd_outqty)pd_outqty from prodiodetail left join prodinout on pi_id=pd_piid where pi_id in("+ids+") and pd_prodcode=? and pd_whcode=? group by pd_piid)"
							      + " left join (select bi_piid,sum(nvl(pdaqty,0))pdaqty from SCM_BARCODEIO_OUT_VIEW where bi_piid in("+ids+") and bi_prodcode=?  and bi_whcode=?  group by bi_piid)"
							      + " on bi_piid=pd_piid",pr_code,whcode,pr_code,whcode);
			if (rs0.next() && rs0.getInt("cn") > 0 ) {
				 pi_ids = rs0.getString("pi_ids");
				 restqty = rs0.getDouble("restqty");   
			} else {
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属物料[" +pr_code + "]或者仓库["+whcode+"]不在该出库单中");
			}
			if(restqty<= 0){
				throw new APIErrorException(APIErrorCode.DATA_NOT_FOUND,"条码所属物料[" +pr_code + "]已完成采集！");
			}
			if(restqty < bar_remain){
				//如果条码库存数量大于剩余需要出库的数量，返回条码数量，剩余需要出库的数量，条码号
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("BAR_CODE", barcode);
				map.put("BAR_REMAIN", remain);
				map.put("RESTQTY", restqty);
				return map;
			}else{
				//更新条码在操作中
				baseDao.execute("update barcode set bar_usingtime=sysdate where bar_id=?",bar_id);	
				//分配拆分
				String[] tempids = pi_ids.split(",");
				length = tempids.length;
				int [] piids = new int [length];
				for(int i=0;i<length;i++){
					piids[i]=Integer.parseInt(tempids[i]);
				}
				Arrays.sort(piids);//升序排序
				double remains = bar_remain,rqty=0;
				StringBuffer inoutnos = new StringBuffer();
				List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
				for(int i=0;i<length;i++){
					if(remains>0){
						SqlRowList rsm = baseDao.queryForRowSet("select pi_inoutno,pd_outqty-nvl(pdaqty,0)restqty,pi_class from (SELECT pd_piid,sum(pd_outqty)pd_outqty,max(pi_class)pi_class,max(pi_inoutno)pi_inoutno"
													+" from prodiodetail left join prodinout on pi_id=pd_piid where pi_id=? and pd_prodcode=? and pd_whcode=? group by pd_piid) "
													+" left join (select bi_piid,sum(pdaqty)pdaqty from SCM_BARCODEIO_OUT_VIEW where bi_piid=? and bi_prodcode=? and bi_whcode=? group by bi_piid) on bi_piid=pd_piid where pd_outqty>nvl(pdaqty,0)",piids[i],pr_code,whcode,piids[i],pr_code,whcode);
					    if(rsm.next()){
					    	pi_inoutno = rsm.getString("pi_inoutno");
					    	rqty = rsm.getDouble("restqty");
					    	inoutnos.append(pi_inoutno).append(",");
					    	Map<String,Object> map =  new HashMap<String, Object>();
					    	map.put("pi_id", piids[i]);
					    	map.put("pi_inoutno", pi_inoutno);
					    	map.put("pi_class", rsm.getString("pi_class"));
					    	if(rqty<remains){
					    		  map.put("restqty", rqty);
						    	  remains = NumberUtil.sub(remains,rqty);
					    	}else{
					    		 map.put("restqty", remains);
						    	 remains = 0;
					    	}
					    	lists.add(map);
					    }
					}
				}
				if(lists.size()>0){//分配给多个出库单号。获取新条码
					Map<String,Object> map1 =  new HashMap<String, Object>();
					map1.put("restqty", NumberUtil.sub(remain,bar_remain));
					lists.add(map1);
					breakingBatch(barcode,lists,inoutnos.toString());
			        for(Map<String,Object>tmp : lists){
			        	if(StringUtil.hasText(tmp.get("pi_inoutno"))){
				    		List<String>sqls = new ArrayList<String>();
				    		pi_id = Integer.valueOf(tmp.get("pi_id").toString());
				        	pi_inoutno = tmp.get("pi_inoutno").toString();
							sqls.add(INSERTINTOBARCODEIODEAL+"'"+SystemSession.getUser().getEm_name()+"','"+tmp.get("bar_code")+"','',"+pi_id+",'"+pi_inoutno+"','"+pr_code+"','"+whcode+"',"+tmp.get("restqty")+","+DateUtil.parseDateToOracleString(Constant.YMD_HMS, rs.getDate("bar_madedate"))+",'"
										+rs.getObject("bar_location")+"',"+rs.getInt("pr_id")+",2,'"+ba_code+"',"+ba_id+",'"+tmp.get("pi_class")+"',"+tmp.get("bar_id")+",'"+tmp.get("bar_lastcode")+"','"+reason+"')");
							sqls.add("update barcode set bar_status=2 ,bar_lastoutdate=sysdate,bar_outno='"+pi_inoutno+"' where bar_id="+tmp.get("bar_id"));
							baseDao.execute(sqls);
							Map<String,Object> map = new HashMap<String, Object>();
							map.put("PI_INOUTNO", pi_inoutno);
							map.put("BAR_CODE", tmp.get("bar_code"));
							map.put("BAR_REMAIN", tmp.get("restqty"));
							rList.add(map);
							//更新备料状态					
							if(updateOutStatus(pi_id)){
								rMessage.append(pi_inoutno+",");
							}
			        	}
			        }
				}		
				//更新条码完成操作
				baseDao.execute("update barcode set bar_usingtime='' where bar_id=?",bar_id);	
			}
		}else{
			throw new APIErrorException(APIErrorCode.INVALID_ORDER_STATUS,"条码不存在或不是在库状态");
		}
		 Map<String, Object> map = new HashMap<String, Object>();
	     map.put("barcode",rs.getCurrentMap());  
	     map.put("nolist", rList);
	     if(rMessage.length()>0){
	    	map.put("finishno", "出库单号："+rMessage.toString()+"已完成备料！");
	     }else{
	    	 map.put("finishno",null);
	     }
		 return map;
	}

	private void judgeStatus( String ids ){
		SqlRowList rsPost = baseDao.queryForRowSet("select wm_concat(pi_inoutno) pi_inoutno from prodinout where pi_id in ("+ids+") and pi_statuscode = 'POSTED'");
		 if(rsPost.next() && rsPost.getString("pi_inoutno") != null && (!("").equals(rsPost.getString("pi_inoutno")))){
			 throw new APIErrorException(APIErrorCode.BUSINESS_FAILED,"出库单:"+rsPost.getString("pi_inoutno")+"已过账不允许采集");
		 }
	}
}
