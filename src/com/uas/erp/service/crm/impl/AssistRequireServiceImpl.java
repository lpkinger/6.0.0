package com.uas.erp.service.crm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;


import com.uas.erp.service.crm.AssistRequireService;
@Service("AssistRequireService")
public class AssistRequireServiceImpl implements AssistRequireService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveAssistRequire(String formStore, String gridStore,
			 String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,grid});
		//保存AssistRequire
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AssistRequire", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存Contact
		for(Map<Object, Object> s:grid){
			s.put("ard_id", baseDao.getSeqId("AssistRequiredetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "AssistRequiredetail");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ar_id", store.get("ar_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,grid});
	}

	@Override
	public void deleteAssistRequire(int ar_id, 
			String caller) {
		//只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("AssistRequire", "ar_statuscode", "ar_id=" + ar_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, ar_id);
		//删除AssistRequire
		baseDao.deleteById("AssistRequire", "ar_id", ar_id);
		//删除Contact
		baseDao.deleteById("AssistRequiredetail", "ard_arid", ar_id);
		//记录操作
		baseDao.logger.delete(caller, "ar_id", ar_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, ar_id);
	}

	@Override
	public void updateAssistRequireById(String formStore, String gridStore,
			 String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid=BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AssistRequire", "ar_statuscode", "ar_id=" + store.get("ar_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store,grid});
		//修改AssistRequire
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AssistRequire", "ar_id");
		baseDao.execute(formSql);
		if(gridStore != null && !gridStore.equals("")){
			//修改Contact
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "AssistRequiredetail", "ard_id");
			baseDao.execute(gridSql);
		}
		//记录操作
		baseDao.logger.update(caller, "ar_id", store.get("ar_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,grid});
	}

	@Override
	public void submitAssistRequire(int ar_id, 
			String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AssistRequire", "ar_statuscode", "ar_id=" + ar_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ar_id);
		//执行提交操作
		baseDao.updateByCondition("AssistRequire", "ar_statuscode='COMMITED',ar_status='" + 
				BaseUtil.getLocalMessage("COMMITED") + "'", "ar_id=" + ar_id);
		//记录操作
		baseDao.logger.submit(caller, "ar_id", ar_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ar_id);
	}

	@Override
	public void resSubmitAssistRequire(int ar_id, 
			String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AssistRequire", "ar_statuscode", "ar_id=" + ar_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, ar_id);
		//执行反提交操作
		baseDao.updateByCondition("AssistRequire", "ar_statuscode='ENTERING',ar_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ar_id=" + ar_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ar_id", ar_id);
		handlerService.afterResSubmit(caller,ar_id);
	}

	@Override
	public void auditAssistRequire(int ar_id,  String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AssistRequire", "ar_statuscode", "ar_id=" + ar_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ar_id);
		//执行审核操作
		baseDao.updateByCondition("AssistRequire", "ar_statuscode='AUDITED',ar_status='" + 
				BaseUtil.getLocalMessage("AUDITED") + "' ,ar_auditer='"+SystemSession.getUser().getEm_name()+"',ar_auditdate=sysdate", "ar_id=" + ar_id);
		//记录操作
		baseDao.logger.audit(caller, "ar_id", ar_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, ar_id);
	}

	@Override
	public void resAuditAssistRequire(int ar_id, 
			String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("AssistRequire", "ar_statuscode", "ar_id=" + ar_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ar_id);
		//执行反审核操作
		baseDao.updateByCondition("AssistRequire", "ar_statuscode='ENTERING',ar_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "',ar_auditer='',ar_auditdate=null", "ar_id=" + ar_id);
		//记录操作
		baseDao.logger.resAudit(caller, "ar_id", ar_id);
		handlerService.afterResAudit(caller, ar_id);
	}

}
