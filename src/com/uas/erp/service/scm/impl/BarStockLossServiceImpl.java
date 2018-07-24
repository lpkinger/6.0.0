package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.scm.BarStockLossService;

@Service("barStockLossService")
public class BarStockLossServiceImpl implements BarStockLossService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveLoss(String formStore, String param, String caller) {
		// TODO Auto-generated method stub
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
		//记录操作
		baseDao.logger.save(caller, "bs_id", store.get("bs_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store, grid});		
	}

	@Override
	public void deleteLoss(int id, String caller) {
		// TODO Auto-generated method stub
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
	public void updateLoss(String formStore, String param, String caller) {
		// TODO Auto-generated method stub
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
		//记录操作
		baseDao.logger.update(caller, "bs_id", store.get("bs_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store, gstore});
	
	}

	@Override
	public void auditLoss(int id, String caller) {
		// TODO Auto-generated method stub
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, id);
		//条码编号不允许重复在一张表中
		SqlRowList rs = baseDao.queryForRowSet("  select COUNT(0) cn,wmsys.wm_concat(bsd_detno) detno  FROM BarStocktaking inner join BarStocktakingDetail"
		           +" on bs_id=bsd_bsid where bs_id="+id+" GROUP BY bsd_barcode HAVING COUNT(bsd_barcode) > 1 ");
		if(rs.next()&& rs.getInt("cn") > 0){
			BaseUtil.showError("明细行："+rs.getString("detno")+",条码号重复!");
		}
		//提交判断盘亏数量不允许超过条码的bar_remain 
		rs = baseDao.queryForRowSet("select count(0) cn,wmsys.wm_concat(bsd_detno) detno from BarStocktaking inner join BarStocktakingDetail on bs_id=bsd_bsid left join barcode on bar_code=bsd_barcode and bar_batchcode=bsd_batchcode and bar_whcode=bsd_whcode"
				   +" where  bar_remain<bsd_outqty");
		if(rs.next()&& rs.getInt("cn") > 0){
			BaseUtil.showError("明细行："+rs.getString("detno")+",盘亏数量大于条码数量!");
		}	
		//修改barcode 中的数量
		baseDao.execute("merge into barcode a using BarStocktakingDetail b on (a.bar_code=b.bsd_barcode and b.bsd_batchcode=a.bar_batchcode"
                +" and b.bsd_bsid="+id+" and NVL(bsd_status,0)=0 ) when  matched then update set a.bar_remain=a.bar_remain-b.bsd_outqty");
		/*baseDao.execute(" update  barcode set bar_remain=bar_remain-(select bsd_outqty from BarStocktakingDetail where bar_code=bsd_barcode"+
		             " and bar_batchcode=bsd_batchcode and bar_whcode=bsd_whcode) where bar_code in"+
					 " (select bsd_barcode from BarStocktakingDetail where NVL(bsd_status,0)=0 and bsd_bsid="+id+")");*/
		baseDao.execute("update barstocktakingdetail set bsd_status=99 where bsd_bsid="+id);
		//执行审核操作g
		baseDao.audit("BarStocktaking", "bs_id=" + id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "bs_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, id);	
	}

	@Override
	public void resAduitLoss(int id, String caller) {
		// TODO Auto-generated method stub
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
	public void submitLoss(int id, String caller) {
		// TODO Auto-generated method stub
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + id);
		StateAssert.submitOnlyEntering(status);		
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, id);
		//条码编号不允许重复在一张表中
		SqlRowList rs = baseDao.queryForRowSet(" select COUNT(0) cn,wmsys.wm_concat(bsd_detno) detno  FROM BarStocktaking inner join BarStocktakingDetail"
		           +" on bs_id=bsd_bsid where bs_id="+id+" GROUP BY bsd_barcode HAVING COUNT(bsd_barcode) > 1 ");
		if(rs.next()&& rs.getInt("cn") > 0){
			BaseUtil.showError("明细行："+rs.getString("detno")+",条码号重复!");
		}
		//提交判断盘亏数量不允许超过条码的bar_remain 
	    rs = baseDao.queryForRowSet("select count(0) cn,wmsys.wm_concat(bsd_detno) detno from BarStocktaking inner join BarStocktakingDetail on bs_id=bsd_bsid left join barcode on bar_code=bsd_barcode and bar_batchcode=bsd_batchcode and bar_whcode=bsd_whcode"
		        +" where  bar_remain<bsd_outqty");
		if(rs.next()&& rs.getInt("cn") > 0){
		    BaseUtil.showError("明细行："+rs.getString("detno")+",盘亏数量大于条码数量!");
		}			
		//执行提交操作
		baseDao.submit("BarStocktaking", "bs_id=" + id, "bs_status", "bs_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bs_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, id);	
	}

	@Override
	public void resSubmitLoss(int id, String caller) {
		// TODO Auto-generated method stub
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BarStocktaking", "bs_statuscode", "bs_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行提交前的其它逻辑
		handlerService.beforeResSubmit(caller, id);
		//执行提交操作
		baseDao.resOperate("BarStocktaking", "bs_id=" + id, "bs_status", "bs_statuscode");
		baseDao.resOperate("BarStocktakingDetail","bsd_bsid="+id,"bsd_status","bs_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bs_id", id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, id);	
	}
}
