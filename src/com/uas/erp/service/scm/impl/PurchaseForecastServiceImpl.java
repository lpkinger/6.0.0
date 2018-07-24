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
import com.uas.erp.dao.common.PurchaseForeCastDao;
import com.uas.erp.service.scm.PurchaseForecastService;

@Service("purchaseForecastService")
public class PurchaseForecastServiceImpl implements PurchaseForecastService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private PurchaseForeCastDao purchaseForeCastDao;
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void savePurchaseForecast(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PurchaseForecast", "pf_code='" + store.get("pf_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存PurchaseForecast
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PurchaseForecast", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存PurchaseForecastDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "PurchaseForecastDetail", "pfd_id");
		baseDao.execute(gridSql);
		baseDao.execute("update purchaseforecastDetail set pfd_statuscode='ENTERING', pfd_status='在录入' where pfd_pfid=" + store.get("pf_id"));
		baseDao.logger.save(caller, "pf_id", store.get("pf_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}
	
	@Override
	public void deletePurchaseForecast(int pf_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PurchaseForecast", "pf_statuscode", "pf_id=" + pf_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{pf_id});
		//删除PurchaseForecast
		baseDao.deleteById("PurchaseForecast", "pf_id", pf_id);
		//删除PurchaseForecastDetail
		baseDao.deleteById("purchaseforecastdetail", "pfd_pfid", pf_id);
		//记录操作
		baseDao.logger.delete(caller, "pf_id", pf_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{pf_id});
	}
	
	@Override
	public void updatePurchaseForecastById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PurchaseForecast", "pf_statuscode", "pf_id=" + store.get("pf_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改Inquiry	
		for(Map<Object, Object> s:gstore){
			if( ((s.get("pfd_delivery").toString()).compareTo (DateUtil.getCurrentDate()))<0){
				BaseUtil.showError("明细行号："+s.get("pfd_detno")+"，交货日期早于当天，不允许更新");
			}
		}
		
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PurchaseForecast", "pf_id");
		baseDao.execute(formSql);
		//修改InquiryDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PurchaseForecastDetail", "pfd_id");
		for(Map<Object, Object> s:gstore){		
			if(s.get("pfd_id") == null || s.get("pfd_id").equals("") || s.get("pfd_id").equals("0") ||
					Integer.parseInt(s.get("pfd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PURCHASEFORECASTDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PurchaseForecastDetail", new String[]{"pfd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update purchaseforecastDetail set pfd_statuscode='ENTERING', pfd_status='在录入' where pfd_pfid=" + store.get("pf_id"));
		//记录操作
		baseDao.logger.update(caller, "pf_id", store.get("pf_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
	
	@Override
	public void printPurchaseForecast(int pf_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[]{pf_id});
		//执行打印操作
		//TODO
		//记录操作
		baseDao.logger.print(caller, "pf_id", pf_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{pf_id});
	}
	
	@Override
	public void auditPurchaseForecast(int pf_id, String caller) {
		//只能对状态为[已提交]的单据进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseForecast", "pf_statuscode", "pf_id=" + pf_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{pf_id});
		//执行审核操作
		baseDao.audit("PurchaseForecast", "pf_id=" + pf_id, "pf_status", "pf_statuscode", "pf_auditdate", "pf_auditman");
		baseDao.audit("purchaseforecastDetail", "pfd_pfid=" + pf_id, "pfd_status", "pfd_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "pf_id", pf_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{pf_id});
	}
	
	@Override
	public void resAuditPurchaseForecast(int pf_id, String caller) {
		//只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseForecast", "pf_statuscode", "pf_id=" + pf_id);
		StateAssert.resAuditOnlyAudit(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pfd_detno) from PurchaseForecastDetail where nvl(pfd_statuscode, ' ') ='FINISH' and pfd_pfid=?", String.class, pf_id);
		if (dets != null) {
			BaseUtil.showError("明细行已结案，不允许反审核!行号：" + dets);
		}
		//执行反审核操作
		baseDao.resAudit("PurchaseForecast", "pf_id=" + pf_id, "pf_status", "pf_statuscode", "pf_auditdate", "pf_auditman");
		baseDao.resOperate("purchaseforecastDetail", "pfd_pfid=" + pf_id, "pfd_status", "pfd_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pf_id", pf_id);
	}
	
	@Override
	public void submitPurchaseForecast(int pf_id, String caller) {
		//只能对状态为[在录入]的单据进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseForecast", "pf_statuscode", "pf_id=" + pf_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{pf_id});
		//执行提交操作
		baseDao.submit("PurchaseForecast", "pf_id=" + pf_id, "pf_status", "pf_statuscode");
		baseDao.submit("purchaseforecastDetail", "pfd_pfid=" + pf_id, "pfd_status", "pfd_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "pf_id", pf_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{pf_id});
	}
	
	@Override
	public void resSubmitPurchaseForecast(int pf_id, String caller) {
		//只能对状态为[已提交]的单据进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PurchaseForecast", "pf_statuscode", "pf_id=" + pf_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[]{pf_id});
		//执行反提交操作
		baseDao.resOperate("PurchaseForecast", "pf_id=" + pf_id, "pf_status", "pf_statuscode");
		baseDao.resOperate("purchaseforecastDetail", "pfd_pfid=" + pf_id, "pfd_status", "pfd_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pf_id", pf_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{pf_id});
	}

	@Override
	public void getVendor(int[] id) {
		purchaseForeCastDao.getVendor(id);
	}

	@Override
	public void confirm(int[] id) {
		purchaseForeCastDao.confirm(id);
	}
}
