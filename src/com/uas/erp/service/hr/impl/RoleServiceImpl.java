package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.RoleService;


@Service
public class RoleServiceImpl implements RoleService{
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveRole(String caller,String formStore,String param){
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);

		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ROLE", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ro_id", store.get("ro_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}
	
	@Override
	public void updateRole(String caller,String formStore,String param){
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ROLE", "ro_id");
		baseDao.execute(formSql);

		// 记录操作
		baseDao.logger.delete(caller, "ro_id", store.get("ro_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
	
	@Override
	public void deleteRole(String  caller,int ro_id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { ro_id});
		// 删除purchase
		baseDao.deleteById("ROLE", "ro_id", ro_id);
		// 记录操作
		baseDao.logger.delete(caller, "ro_id", ro_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { ro_id});
	}
	
	@Override
	public void auditRole(String  caller,int ro_id) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] { ro_id });
		// 执行审核操作
		baseDao.audit("ROLE", "ro_id=" + ro_id, "ro_status", "ro_statuscode", "ro_auditdate","ro_auditor");
		// 记录操作
		baseDao.logger.audit(caller, "ro_id", ro_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { ro_id });
	}
	
	@Override
	public void resAuditRole(String  caller,int ro_id) {
		handlerService.beforeResAudit(caller, new Object[]{ro_id});
		// 执行反审核操作
		baseDao.resAudit("ROLE", "ro_id=" + ro_id, "ro_status", "ro_statuscode", "ro_auditdate","ro_auditor");
		// 记录操作
		baseDao.logger.resAudit(caller, "ro_id", ro_id);
		handlerService.afterResAudit(caller,new Object[] { ro_id });
	}

	@Override
	public void submitRole(String  caller,int ro_id) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { ro_id });
		// 执行提交操作
		baseDao.submit("ROLE", "ro_id=" + ro_id, "ro_status", "ro_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ro_id", ro_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { ro_id });
	}

	@Override
	public void resSubmitRole(String  caller,int ro_id) {
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, ro_id);
		// 执行反提交操作
		baseDao.resOperate("ROLE", "ro_id=" + ro_id, "ro_status", "ro_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ro_id", ro_id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, ro_id);
	}

	@Override
	public void bannedRole(String caller, int ro_id) {
		Object statuscode = baseDao.getFieldDataByCondition("Role", "ro_statuscode", "ro_id=" + ro_id);
		if ("DELETED".equals(statuscode)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.banned_onlyCanuse"));
		}
		if (!"AUDITED".equals(statuscode)) {
			BaseUtil.showError("只能禁用已审核的单据");
		}
		// 执行禁用前的其它逻辑
		handlerService.handler(caller, "banned", "before", new Object[] { ro_id });
		//修改单据为已禁用
		baseDao.banned("Role", "ro_id=" + ro_id, "ro_status", "ro_statuscode");
		// 记录操作
		baseDao.logger.banned(caller, "ro_id", ro_id);
		// 执行禁用后的其它逻辑
		handlerService.handler(caller, "banned", "after", new Object[] { ro_id });
	}

	@Override
	public void resBannedRole(String caller, int ro_id) {
		Object statuscode = baseDao.getFieldDataByCondition("Role", "ro_statuscode", "ro_id=" + ro_id);
		if (!"DISABLE".equals(statuscode)) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resBanned_onlyBanned"));
		}
		// 执行反禁用前的其它逻辑
		handlerService.handler(caller, "resBanned", "before", new Object[] { ro_id });
		// 反禁用(修改单据为已审核)
		baseDao.audit("Role", "ro_id=" + ro_id, "ro_status", "ro_statuscode");
		// 记录操作
		baseDao.logger.resBanned(caller, "ro_id", ro_id);
		// 执行反禁用后的其它逻辑
		handlerService.handler(caller, "resBanned", "after", new Object[] { ro_id });
	}
}
