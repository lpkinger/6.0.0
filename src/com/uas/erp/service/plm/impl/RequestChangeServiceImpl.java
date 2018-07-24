package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.RequestChangeService;


@Service
public class RequestChangeServiceImpl implements RequestChangeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveRequestChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PRJREQUESTCHANGE", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "prc_id", store.get("prc_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateRequestChangeById(String formStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PRJREQUESTCHANGE", "prc_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "prc_id", store.get("prc_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteRequestChange(int prc_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{prc_id});
		//删除主表内容
		baseDao.deleteById("PRJREQUESTCHANGE", "prc_id", prc_id);
		baseDao.logger.delete(caller, "prc_id", prc_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{prc_id});
	}

	@Override
	public void auditRequestChange(int prc_id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("PRJREQUESTCHANGE", "prc_statuscode", "prc_id=" + prc_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{prc_id});//maz 反审核字段错误导致报错已改正
		baseDao.audit("PRJREQUESTCHANGE", "prc_id=" + prc_id, "prc_status", "prc_statuscode", "prc_auditdate", "prc_auditor");
		//记录操作
		baseDao.logger.audit(caller, "prc_id", prc_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{prc_id});
	}
	@Override
	public void submitRequestChange(int prc_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PRJREQUESTCHANGE", "prc_statuscode", "prc_id=" + prc_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { prc_id });
		// 执行提交操作
		baseDao.submit("PRJREQUESTCHANGE", "prc_id=" + prc_id, "prc_status", "prc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "prc_id", prc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { prc_id });
	}
	@Override
	public void resSubmitRequestChange(int prc_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PRJREQUESTCHANGE", "prc_statuscode", "prc_id=" + prc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { prc_id });
		// 执行反提交操作
		baseDao.resOperate("PRJREQUESTCHANGE", "prc_id=" + prc_id, "prc_status", "prc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "prc_id", prc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { prc_id });
	}
	@Override
	public void resAuditRequestChange(int prc_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PRJREQUESTCHANGE", "prc_statuscode", "prc_id=" + prc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("PRJREQUESTCHANGE", "prc_id=" + prc_id, "prc_status", "prc_statuscode", "prc_auditdate", "prc_auditor");
		baseDao.resOperate("PRJREQUESTCHANGE", "prc_id=" + prc_id, "prc_status", "prc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "prc_id", prc_id);
	}
}
