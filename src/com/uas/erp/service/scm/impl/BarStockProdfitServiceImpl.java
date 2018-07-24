package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VerifyApplyDao;
import com.uas.erp.service.scm.BarStockProfitService;

@Service("barStockProfitService")
public class BarStockProdfitServiceImpl implements BarStockProfitService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired 
    private  VerifyApplyDao verifyApplyDao;	
	@Override
	public void saveProfit(String formStore, String param, String caller) {
		Map<Object,Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid =BaseUtil.parseGridStoreToMaps(param);
		handlerService.beforeSave(caller, new Object[] {store, grid});
		//保存BarStocktaking
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BarStocktaking", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存BarStocktakingDetail
		for(Map<Object, Object> m : grid) {
			m.put("bsd_id", baseDao.getSeqId("BARSTOCKTAKINGDETAIL_SEQ"));
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "BarStocktakingDetail"));
		//如果仓库为空,就将主表的仓库赋值给它
		baseDao.execute("update BarStocktakingDetail set bsd_whcode = (select bs_whcode from BarStocktaking where bsd_bsid = bs_id) where bsd_bsid = ? and nvl(bsd_whcode,' ') = ' '",store.get("bs_id"));
		//更新batchid
		baseDao.execute("update BarStocktakingDetail set bsd_batchid = (select ba_id from batch where ba_code = bsd_batchcode and ba_prodcode = bsd_prodcode and ba_whcode = bsd_whcode) where bsd_bsid = ? and nvl(bsd_batchcode,' ')<> ' '",store.get("bs_id"));
		//记录操作
		baseDao.logger.save(caller, "bs_id", store.get("bs_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store, grid});		
	}

	@Override
	public void deleteProfit(int id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		//删除BarStocktaking
		baseDao.deleteById("BarStocktaking", "bs_id", id);
		//删除BarStocktakingDetail
		baseDao.deleteById("BarStocktakingDetail", "bsd_bsid", id);
		//记录操作
		baseDao.logger.delete(caller, "bs_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, id);	
	}

	@Override
	public void updateProfit(String formStore, String param, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(param);
		//只能修改[在录入]的单据资料!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + store.get("bs_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store, gstore});
		//修改BarStocktaking
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BarStocktaking", "bs_id");
		baseDao.execute(formSql);
		//修改BarStocktakingDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "BarStocktakingDetail", "bsd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("bsd_id") == null || s.get("bsd_id").equals("") || s.get("bsd_id").equals("0") ||
					Integer.parseInt(s.get("bsd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("BARSTOCKTAKINGDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "BarStocktakingDetail", new String[]{"bsd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//如果仓库为空,就将主表的仓库赋值给它
		baseDao.execute("update BarStocktakingDetail set bsd_whcode = (select bs_whcode from BarStocktaking where bsd_bsid = bs_id) where bsd_bsid = ? and nvl(bsd_whcode,' ') = ' '",store.get("bs_id"));
		//更新batchid
		baseDao.execute("update BarStocktakingDetail set bsd_batchid = (select ba_id from batch where ba_code = bsd_batchcode and ba_prodcode = bsd_prodcode and ba_whcode = bsd_whcode) where bsd_bsid = ? and nvl(bsd_batchcode,' ')<> ' '",store.get("bs_id"));
		//记录操作
		baseDao.logger.update(caller, "bs_id", store.get("bs_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store, gstore});
	
	}

	@Override
	public void auditProfit(int id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, id);
		//审核判断是否存在明细行物料条码数量总和小于入库数量
		checkNumber(id);
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn ,wmsys.wm_concat(bsd_detno) detno from  BarStocktaking inner join barstocktakingdetail on bs_id=bsd_bsid  left join"+
		          "(select bdd_bsdid ,sum(NVL(bdd_inqty,0)) bdd_inqty from BarStocktakingDetailDet group by bdd_bsdid)A  on A.bdd_bsdid=bsd_id where NVL(A.bdd_inqty,0)<>bsd_inqty and bs_id="+id);		
		if(rs.next()){
			if(rs.getInt("cn") > 0){
			      BaseUtil.showError("明细行号:"+rs.getString("detno")+"物料条码总数必须等于入库数");
			  }
		}	
		baseDao.execute("INSERT INTO barcode(bar_id,bar_code,bar_prodid,bar_prodcode,bar_piid,bar_pdid,bar_batchcode,bar_batchid,bar_ordercode,"
	             +" bar_inoutno,bar_pucode,bar_vendcode,bar_indate,bar_batchqty,bar_remain,bar_kind,bar_lastindate,bar_whcode,"
	             +" bar_vendbarcode,bar_madedate,bar_validdate,bar_location,bar_outboxcode1,bar_status)"
	             +" select  barcode_seq.nextval,bdd_barcode,pr_id,pr_code,0,0,bsd_batchcode,ba_id,ba_ordercode,"
	             +" ba_sourcecode,ba_ordercode, ba_custvendcode,ba_date,bsd_inqty,bdd_inqty,'0',sysdate,bs_whcode,"
	             +" bdd_vendbarcode,bdd_madedate,bdd_validdate,bdd_location,bdd_outboxcode,1 from barstocktaking inner join barstocktakingdetail on bs_id=bsd_bsid inner join barstocktakingdetaildet on bsd_id=bdd_bsdid left join product on pr_code=bsd_prodcode left join batch on ba_code=bsd_batchcode and ba_prodcode=bsd_prodcode and ba_whcode=bs_whcode where bs_id="+id+" and NVL(bsd_status,0)=0 ");
		baseDao.execute("update barstocktakingdetail set bsd_status=99 where bsd_bsid="+id);
		baseDao.execute("update batch set ba_hasbarcode = -1 where ba_id in (select ba_id from barstocktaking inner join barstocktakingdetail on bs_id=bsd_bsid inner join barstocktakingdetaildet on bsd_id=bdd_bsdid left join product on pr_code=bsd_prodcode left join batch on ba_code=bsd_batchcode and ba_prodcode=bsd_prodcode and ba_whcode=bs_whcode where bs_id = ?)",id);
		//执行审核操作g
		baseDao.audit("BarStocktaking", "bs_id=" + id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "bs_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);	
	}

	@Override
	public void resAduitProfit(int id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, id);
		//执行反审核操作
		baseDao.resOperate("BarStocktaking", "bs_id=" + id, "bs_status", "bs_statuscode");
		baseDao.execute("update barstocktakingdetail set bsd_status=0 where bsd_bsid="+id);
		//记录操作
		baseDao.logger.resAudit(caller, "bs_id", id);
		handlerService.afterResAudit(caller, id);
	
	}

	@Override
	public void submitProfit(int id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + id);
		StateAssert.submitOnlyEntering(status);		
		SqlRowList rs = baseDao.queryForRowSet("select wm_concat(bsd_detno) detno,count(1) cn from BarStocktakingDetail where bsd_bsid = ? and nvl(bsd_whcode,' ') = ' '",id);
		if(rs.next() && rs.getInt("cn") > 0){
			 BaseUtil.showError("明细行号:"+rs.getString("detno")+"仓库为空,不允许提交");
		}
		rs = baseDao.queryForRowSet("select wm_concat(bsd_detno) detno,count(1) cn from BarStocktakingDetail left join batch on ba_code = bsd_batchcode where bsd_bsid = ? and nvl(ba_id,0) = 0 and nvl(bsd_batchcode,' ') <> ' '",id);
		if(rs.next() && rs.getInt("cn") > 0){
			 BaseUtil.showError("明细行号:"+rs.getString("detno")+"批次号不存在");
		}
		rs = baseDao.queryForRowSet("select wm_concat(distinct bsd_batchcode) bsd_batchcode from BarStocktakingDetail where bsd_bsid =? and nvl(bsd_batchcode,' ')<>' ' group by bsd_batchid having count(bsd_batchid)>1",id);
		if(rs.next()){
			 BaseUtil.showError("批次:"+rs.getString("bsd_batchcode")+"重复");
		}
		checkNumber(id);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		//执行提交操作
		baseDao.submit("BarStocktaking", "bs_id=" + id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bs_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);	
	}

	@Override
	public void resSubmitProfit(int id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行提交前的其它逻辑
		handlerService.beforeResSubmit(caller, id);
		//执行提交操作
		baseDao.resOperate("BarStocktaking", "bs_id=" + id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bs_id", id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, id);	
	}

	@Override
	public void batchGenBarcode(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("BarStocktaking ", "bs_statuscode", "bs_id=" + id);
		if(status.equals("AUDITED")){
			BaseUtil.showError("单据已审核不允许操作!");
		}
		checkNumber(id);
		//后台在点击批量生成条码的时候提示是否已经生成了条码
		int cn = baseDao.getCount("select count(0) cn  from  BARSTOCKTAKINGDETAILDET left join BarStocktakingDetail on bsd_id=bdd_bsdid left join BarStocktaking on bs_id=bsd_bsid where bs_id="+id) ;
		if(cn > 0 ){
			BaseUtil.showError("已经批量生成了条码！");
		}
		//bdd_barcode,bdd_prodid,bdd_bsdid,bdd_id,bdd_indate[ba_date],bdd_madedate [ba_date],bdd_vendcode[bsd_custvendcode] 
		SqlRowList rs = baseDao.queryForRowSet("select bsd_prodcode ,bsd_id ,to_char(ba_date,'yyyy-MM-dd') ba_date ,pr_id,bsd_inqty from BarStocktaking left join BarStocktakingDetail on bs_id=bsd_bsid" +
				            " left join batch on ba_code=bsd_batchcode and ba_prodcode=bsd_prodcode and ba_whcode=bs_whcode " +
				            "left join product on pr_code=bsd_prodcode where bs_id="+id);
		if(rs.next()){
			for(Map<String,Object> map:rs.getResultList()){
				Object[] obs  = baseDao.getFieldsDataByCondition("Vendor left join BarStocktakingDetail on bsd_custvendcode=ve_code OR bsd_custvendcode=ve_name OR ve_apvendname=bsd_custvendcode", new String []{"ve_id","ve_code"}, "bsd_id='"+map.get("bsd_id")+"'");
				if(obs != null){
					String bar_code = verifyApplyDao.barcodeMethod(map.get("bsd_prodcode").toString(),obs[0].toString(),0);
					baseDao.execute("insert into BARSTOCKTAKINGDETAILDET(bdd_id,bdd_barcode,bdd_inqty,bdd_prodcode,bdd_prodid,bdd_bsdid,bdd_madedate,bdd_indate,bdd_vendcode) values(" +
						baseDao.getSeqId("BARSTOCKTAKINGDETAILDET_SEQ")+",'"+bar_code+"','"+map.get("bsd_inqty")+"','"+map.get("bsd_prodcode")+"',"+map.get("pr_id")+","+map.get("bsd_id")+","+DateUtil.parseDateToOracleString("yyyy-MM-dd", map.get("ba_date").toString())+","+DateUtil.parseDateToOracleString("yyyy-MM-dd", map.get("ba_date").toString())+",'"+obs[1].toString()+"')");
				}else{
					String bar_code = verifyApplyDao.barcodeMethod(map.get("bsd_prodcode").toString(),"",0);
					baseDao.execute("insert into BARSTOCKTAKINGDETAILDET(bdd_id,bdd_barcode,bdd_inqty,bdd_prodcode,bdd_prodid,bdd_bsdid,bdd_madedate,bdd_indate,bdd_vendcode) values(" +
						baseDao.getSeqId("BARSTOCKTAKINGDETAILDET_SEQ")+",'"+bar_code+"','"+map.get("bsd_inqty")+"','"+map.get("bsd_prodcode")+"',"+map.get("pr_id")+","+map.get("bsd_id")+","+DateUtil.parseDateToOracleString("yyyy-MM-dd", map.get("ba_date").toString())+","+DateUtil.parseDateToOracleString("yyyy-MM-dd", map.get("ba_date").toString())+")");
				
				}
			}
		}
			
	
	}
	
	private void checkNumber(Integer id){
		SqlRowList rs = baseDao.queryForRowSet("select count(0) cn ,wmsys.wm_concat(bsd_detno) detno from "
         +"   barstocktakingdetail left join batch on bsd_batchid = ba_id left join (select sum(bar_remain) bar_remain,bar_batchid from barcode where bar_status=1 "
         +"   group by bar_batchid) A on A.bar_batchid= bsd_batchid where "
         +"   bsd_bsid=? and nvl(bsd_batchcode,' ')<>' ' and nvl(bsd_inqty,0) > (nvl(ba_remain,0)-nvl(bar_remain,0)) and rownum<30",id);
		if(rs.next()){
			if(rs.getInt("cn") > 0){
			      BaseUtil.showError("明细行号:"+rs.getString("detno")+"应补数量+已生成条码库存数量不能超过批号库存数");
			  }
		}
		/*rs = baseDao.queryForRowSet("select count(0) cn ,wmsys.wm_concat(bsd_detno) detno from "
		         +"   barstocktakingdetail left join productWH on pw_prodcode=bsd_prodcode and pw_whcode = bsd_whcode left join (select sum(bar_remain) bar_remain,bar_prodcode,bar_whcode from barcode where bar_status=1 "
		         +"   group by bar_prodcode,bar_whcode) A on A.bar_prodcode= bsd_prodcode and A.bar_whcode = bsd_whcode where "
		         +"   bsd_bsid=? and nvl(bsd_batchcode,' ')=' ' and nvl(bsd_inqty,0) > (nvl(pw_onhand,0)-nvl(bar_remain,0)) and rownum<30",id);
				if(rs.next()){
					if(rs.getInt("cn") > 0){
					      BaseUtil.showError("明细行号:"+rs.getString("detno")+"应补数量+已生成条码库存数量不能超过物料库存数");
					  }
				}*/
	}
}
