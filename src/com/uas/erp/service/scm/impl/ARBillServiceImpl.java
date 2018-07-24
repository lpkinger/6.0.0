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
import com.uas.erp.service.scm.ARBillService;

@Service("aRBillService")
public class ARBillServiceImpl implements ARBillService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveARBill(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("ARBill", "ab_code='" + store.get("ab_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, grid});
		//保存ARBill
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ARBill", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存ARBillDetail
		
		for(int i=0;i<grid.size();i++){
			grid.get(i).put("abd_id", baseDao.getSeqId("ARBILLDETAIL_SEQ"));
			grid.get(i).put("abd_status", BaseUtil.getLocalMessage("ENTERING"));
			grid.get(i).put("abd_statuscode", "ENTERING");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "ARBillDetail");
		baseDao.execute(gridSql);
		//记录日志
		baseDao.logger.save(caller, "ab_id", store.get("ab_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, grid});
	}
	@Override
	public void deleteARBill(int ab_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_statuscode", "ab_id=" + ab_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{ab_id});
		//删除ARBill
		baseDao.deleteById("ARBill", "ab_id", ab_id);
		//删除ARBillDetail
		baseDao.deleteById("ARBilldetail", "abd_abid", ab_id);
		//记录日志
		baseDao.logger.delete(caller, "ab_id", ab_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{ab_id});
	}
	
	@Override
	public void updateARBillById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_statuscode", "ab_id=" + store.get("ab_id"));
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		//执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[]{store, gstore});
		//修改ARBill
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ARBill", "ab_id");
		baseDao.execute(formSql);
		//修改ARBillDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ARBillDetail", "abd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("abd_id") == null || s.get("abd_id").equals("") || s.get("abd_id").equals("0") ||
					Integer.parseInt(s.get("abd_id").toString()) == 0){//新添加的数据，id不存在
				int id = baseDao.getSeqId("ARBILLDETAIL_SEQ");
				s.put("abd_status", BaseUtil.getLocalMessage("ENTERING"));
				s.put("abd_status", "ENTERING");
				String sql = SqlUtil.getInsertSqlByMap(s, "ARBillDetail", new String[]{"abd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ab_id", store.get("ab_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store, gstore});
	}
	@Override
	public void printARBill(int ab_id, String caller) {
		//执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[]{ab_id});
		//执行打印操作
		//TODO
		//记录操作
		baseDao.logger.print(caller, "ab_id", ab_id);
		//执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[]{ab_id});
	}
	@Override
	public void auditARBill(int ab_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_statuscode", "ab_id=" + ab_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{ab_id});
		//执行审核操作
		baseDao.audit("ARBill", "ab_id=" + ab_id, "ab_status", "ab_statuscode", "ab_auditdate", "ab_auditman");
		baseDao.audit("ARBillDetail", "abd_abid=" + ab_id, "abd_status", "abd_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "ab_id", ab_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{ab_id});
	}
	@Override
	public void resAuditARBill(int ab_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_statuscode", "ab_id=" + ab_id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		handlerService.handler(caller, "resAudit", "before", new Object[]{ab_id});
		//执行反审核操作
		baseDao.resOperate("ARBill", "ab_id=" + ab_id, "ab_status", "ab_statuscode");
		baseDao.resOperate("ARBillDetail", "abd_abid=" + ab_id, "abd_status", "abd_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ab_id", ab_id);
		handlerService.handler(caller, "resAudit", "after", new Object[]{ab_id});
	}
	@Override
	public void submitARBill(int ab_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_statuscode", "ab_id=" + ab_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		//只能选择已审核的客户!
		Object code = baseDao.getFieldDataByCondition("ARBill", "ab_custcode", "ab_id=" + ab_id);
		status = baseDao.getFieldDataByCondition("Customer", "cu_auditstatuscode", "cu_code='" + code + "'");
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("customer_onlyAudited") + 
				"<a href=\"javascript:openUrl('jsps/scm/sale/customer.jsp?formCondition=cu_codeIS" + code +  "')\">" + code + "</a>&nbsp;");
		}
		//只能选择已审核的物料!
		List<Object> codes = baseDao.getFieldDatasByCondition("ARBillDetail", "abd_prodcode", "abd_abid=" + ab_id);
		for(Object c:codes){
			status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_code='" + c + "'");
			if(!status.equals("AUDITED")){
				BaseUtil.showError(BaseUtil.getLocalMessage("product_onlyAudited") + 
						"<a href=\"javascript:openUrl('jsps/scm/product/product.jsp?formCondition=pr_codeIS" + c +  "')\">" + c + "</a>&nbsp;");
			}
		}
		//执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[]{ab_id});
		//执行提交操作
		baseDao.submit("ARBill", "ab_id=" + ab_id, "ab_status", "ab_statuscode");
		baseDao.submit("ARBillDetail", "abd_abid=" + ab_id, "abd_status", "abd_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ab_id", ab_id);
		//执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[]{ab_id});
	}
	@Override
	public void resSubmitARBill(int ab_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ARBill", "ab_statuscode", "ab_id=" + ab_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler(caller, "resCommit", "before", new Object[]{ab_id});
		//执行反提交操作
		baseDao.resOperate("ARBill", "ab_id=" + ab_id, "ab_status", "ab_statuscode");
		baseDao.resOperate("ARBillDetail", "abd_abid=" + ab_id, "abd_status", "abd_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "ab_id", ab_id);
		handlerService.handler(caller, "resCommit", "after", new Object[]{ab_id});
	}
}
