package com.uas.erp.service.salary.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.salary.SalaryRequestService;

@Service("salaryRequestService")
public class SalaryRequestServiceImpl implements SalaryRequestService {

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private BaseDao baseDao;
	
	public void saveRequire(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		int i = baseDao.getCountByCondition("salarypassword", "sp_emcode='"+store.get("sp_emcode")+"'");
		if(i>0){
			BaseUtil.showError("该人员已存在,请误重复申请!");
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "salarypassword", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "sp_id", store.get("sp_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateRequireById(String formStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "salarypassword", "sp_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "sp_id", store.get("sp_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteRequire(int sp_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{sp_id});
		//删除主表内容
		baseDao.deleteById("salarypassword", "sp_id", sp_id);
		baseDao.logger.delete(caller, "sp_id", sp_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{sp_id});
	}

	@Override
	public void auditRequire(int sp_id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("salarypassword", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{sp_id});
		baseDao.audit("salarypassword", "sp_id=" + sp_id, "sp_status", "sp_statuscode", "sp_auditdate", "sp_auditor");
		//记录操作
		baseDao.logger.audit(caller, "sp_id", sp_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{sp_id});
	}
	@Override
	public void submitRequire(int sp_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("salarypassword", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { sp_id });
		// 执行提交操作
		baseDao.submit("salarypassword", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sp_id", sp_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { sp_id });
	}
	@Override
	public void resSubmitRequire(int sp_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("salarypassword", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { sp_id });
		// 执行反提交操作
		baseDao.resOperate("salarypassword", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sp_id", sp_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sp_id });
	}
	@Override
	public void resAuditRequire(int sp_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("salarypassword", "sp_statuscode", "sp_id=" + sp_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("salarypassword", "sp_id=" + sp_id, "sp_status", "sp_statuscode", "sp_auditdate", "sp_auditor");
		baseDao.resOperate("salarypassword", "sp_id=" + sp_id, "sp_status", "sp_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sp_id", sp_id);
	}

}
