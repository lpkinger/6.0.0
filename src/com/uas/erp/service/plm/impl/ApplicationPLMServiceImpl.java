package com.uas.erp.service.plm.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.ApplicationDao;
import com.uas.erp.service.plm.ApplicationPLMService;

@Service("applicationPLMService")
public class ApplicationPLMServiceImpl implements ApplicationPLMService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ApplicationDao applicationDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveApplication(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Application", "ap_code='" + store.get("ap_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, grid});
		//保存Application
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Application", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存ApplicationDetail
		for(int i=0;i<grid.size();i++){
			grid.get(i).put("ad_id", baseDao.getSeqId("APPLICATIONDETAIL_SEQ"));
			grid.get(i).put("ad_status", BaseUtil.getLocalMessage("ENTERING"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ApplicationDetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "ap_id", store.get("ap_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, grid});
	}
	
	@Override
	public void deleteApplication(int ap_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, ap_id);
		//删除Application
		baseDao.deleteById("Application", "ap_id", ap_id);
		//删除ApplicationDetail
		baseDao.deleteById("applicationdetail", "ad_apid", ap_id);
		//记录操作
		baseDao.logger.delete(caller, "ap_id", ap_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, ap_id);
	}
	
	@Override
	public void updateApplicationById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + store.get("ap_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改Application
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Application", "ap_id");
		baseDao.execute(formSql);
		//修改ApplicationDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ApplicationDetail", "ad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ad_id") == null || s.get("ad_id").equals("") || s.get("ad_id").equals("0") ||
					Integer.parseInt(s.get("ad_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("APPLICATIONDETAIL_SEQ");
				s.put("ad_status", BaseUtil.getLocalMessage("ENTERING"));
				String sql = SqlUtil.getInsertSqlByMap(s, "ApplicationDetail", new String[]{"ad_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ap_id", store.get("ap_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}
	
	@Override
	public void printApplication(int ap_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.beforePrint(caller, ap_id);
		//执行打印操作
		//记录操作
		baseDao.logger.print(caller, "ap_id", ap_id);
		//执行打印后的其它逻辑
		handlerService.afterPrint(caller, ap_id);
	}
	
	@Override
	public void auditApplication(int ap_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ap_id);
		//执行审核操作
		baseDao.audit("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode", "ap_auditdate", "ap_auditman");
		baseDao.audit("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "ap_id", ap_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, ap_id);
	}
	
	@Override
	public void resAuditApplication(int ap_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		baseDao.resOperate("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ap_id", ap_id);
	}
	
	@Override
	public void submitApplication(int ap_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		StateAssert.submitOnlyEntering(status);
		//只能选择已审核的供应商!
		Object code = baseDao.getFieldDataByCondition("Application", "ap_vendcode", "ap_id=" + ap_id);
		status = baseDao.getFieldDataByCondition("Vendor", "ve_auditstatuscode", "ve_code='" + code + "'");
		if(status != null && !status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("vendor_onlyAudited") + 
				"<a href=\"javascript:openUrl('jsps/scm/purchase/vendor.jsp?formCondition=ve_codeIS" + code +  "')\">" + code + "</a>&nbsp;");
		}
		//只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("ApplicationDetail", "ad_prodcode", "ad_apid=" + ap_id);
		for(Object c:codes){
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if(status != null && !status.equals("AUDITED")){
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited") + 
						"<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c +  "')\">" + c + "</a>&nbsp;");
			}
		}
		String sql = " update applicationdetail set ad_prodid=(select pr_id from product where pr_code=ad_prodcode) where ad_apid="+ap_id;
		baseDao.execute(sql);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ap_id);
		//执行提交操作
		baseDao.submit("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		baseDao.submit("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ap_id", ap_id);
		handlerService.afterSubmit(caller, ap_id);
	}
	
	@Override
	public void resSubmitApplication(int ap_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Application", "ap_statuscode", "ap_id=" + ap_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ap_id);
		//执行反提交操作
		baseDao.resOperate("Application", "ap_id=" + ap_id, "ap_status", "ap_statuscode");
		baseDao.resOperate("ApplicationDetail", "ad_apid=" + ap_id, "ad_status", "ad_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ap_id", ap_id);
		handlerService.afterResSubmit(caller, ap_id);
	}
	
	@Override
	@Transactional
	public int turnPurchase(int ap_id, String caller) {
		int puid = 0;
		//判断该请购单是否已经转入过采购单
		Object code = baseDao.getFieldDataByCondition("application", "ap_code", "ap_id=" + ap_id);
		code = baseDao.getFieldDataByCondition("purchase", "pu_code", "pu_sourcecode='" + code + "'");
		if(code != null && !code.equals("")){
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.purchase.application.haveturn") + 
					"<a href=\"javascript:openUrl('jsps/scm/purchase/purchase.jsp?formCondition=pu_codeIS" + code + "&gridCondition=pd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			//转采购
			puid = applicationDao.turnPurchase(ap_id);
			//修改请购单状态
			baseDao.updateByCondition("Application", "ap_statuscode='TURNPURC',ap_status='" + 
					BaseUtil.getLocalMessage("TURNPURC") + "'", "ap_id=" + ap_id);
			baseDao.updateByCondition("ApplicationDetail", "ad_statuscode='TURNPURC',ad_status='" + 
					BaseUtil.getLocalMessage("TURNPURC") + "',ad_yqty=ad_qty", "ad_apid=" + ap_id);
			//记录操作
			baseDao.logger.turn("msg.turnPurchase", caller, "ap_id", ap_id);
		}
		return puid;
	}
	
	@Override
	public void getVendor(int[] id) {
		applicationDao.getVendor(id);
	}
	/**
	 * 请购单批量抛转
	 */
	@Override
	public String[] postApplication(int[] id, int ma_id_f, int ma_id_t) {
		//同一服务器，不同数据库账号间抛数据
		String from = baseDao.getFieldDataByCondition("master", "ma_name", "ma_id=" + ma_id_f).toString();
		String to = baseDao.getFieldDataByCondition("master", "ma_name", "ma_id=" + ma_id_t).toString();
		return applicationDao.postApplication(id, from, to);
		//不同服务器间数据抛转	
	}
}
