package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.ReceivablePlanService;

@Service("ReceivablePlanService")
public class ReceivablePlanServiceImpl implements ReceivablePlanService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveReceivablePlan(String formStore, String gridStore, String caller ) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object,Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		//save
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DebitContractRegister", new String[]{}, new Object[]{});	
		baseDao.execute(formSql);
		//save detail
		List<String> gridSql= SqlUtil.getInsertSqlbyGridStore(grid, "DebitContractRegisterDet");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "dcr_id", store.get("dcr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
	@Override
	public void deleteReceivablePlan(int dcr_id,String caller ) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { dcr_id });
		//delete 
		baseDao.deleteById("DebitContractRegister", "dcr_id", dcr_id);
		//delete detail
		baseDao.deleteById("DebitContractRegisterDet","dcrd_dcrid",dcr_id);
		
		//记录操作
		baseDao.logger.delete(caller, "dcr_id", dcr_id); 
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, dcr_id);
	}
	
	@Override
	public void updateReceivablePlanById(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
    	//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("DebitContractRegister", "dcr_statuscode", "dcr_id=" + store.get("dcr_id"));
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}		
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		//update
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DebitContractRegister", "dcr_id");
		baseDao.execute(formSql);	
		//update detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "DebitContractRegisterDet", "dcrd_id");
		for(Map<Object,Object> s:grid){
			if(s.get("dcrd_id") == null || s.get("dcrd_id").equals("") || s.get("dcrd_id").equals("0")
					|| Integer.parseInt(s.get("dcrd_id").toString()) == 0){
				int id = baseDao.getSeqId("DebitContractRegisterDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "DebitContractRegisterDet", new String[]{"dcrd_id"},new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "dcr_id", store.get("dcr_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{ store });
	}

	@Override
	public void auditReceivablePlan(int dcr_id,String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, dcr_id);		
		//执行审核操作
		baseDao.updateByCondition("DebitContractRegister", "dcr_statuscode='AUDITED',dcr_status='" + 
				BaseUtil.getLocalMessage("AUDITED") + "'" , "dcr_id=" + dcr_id);
		//记录操作
		baseDao.logger.audit(caller, "dcr_id", dcr_id);   
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{ dcr_id });
	}
	@Override
	public void resAuditReceivablePlan(int dcr_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if(!status.equals("AUDITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, dcr_id);
		//执行反审核操作
		baseDao.updateByCondition("DebitContractRegister", "dcr_statuscode='ENTERING',dcr_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "dcr_id=" + dcr_id);
		//记录操作
		baseDao.logger.resAudit(caller, "dcr_id", dcr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, dcr_id);
	}
	@Override
	public void submitReceivablePlan(int dcr_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if(!status.equals("ENTERING")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, dcr_id);
		//执行提交操作
		baseDao.updateByCondition("DebitContractRegister", "dcr_statuscode='COMMITED',dcr_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "'", "dcr_id=" + dcr_id);
		//记录操作
		baseDao.logger.submit(caller, "dcr_id", dcr_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, dcr_id);
	}
	@Override
	public void resSubmitReceivablePlan(int dcr_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DebitContractRegister", "dcr_statuscode", "dcr_id=" + dcr_id);
		if(!status.equals("COMMITED")){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, dcr_id);
		//执行反提交操作
		baseDao.updateByCondition("DebitContractRegister", "dcr_statuscode='ENTERING',dcr_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "dcr_id=" + dcr_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "dcr_id", dcr_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, dcr_id);
	}
}
