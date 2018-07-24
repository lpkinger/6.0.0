package com.uas.erp.service.fa.impl;

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

import com.uas.erp.service.fa.AccountRegisterService;

@Service
public class AccountRegisterServiceImpl implements AccountRegisterService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAccountRegister(String caller, String formStore,
			String gridStore) {
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存AccountRegister
		String formSql = SqlUtil.getInsertSqlByMap(store, "AccountRegister");
		baseDao.execute(formSql);
		// 保存AccountRegisterDetail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(grid,
				"AccountRegisterDetail", "ard_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "ar_id", store.get("ar_id"));
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	@Override
	public void updateAccountRegisterById(String caller, String formStore,
			String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改AccountRegister
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"AccountRegister", "ar_id");
		baseDao.execute(formSql);
		// 修改AccountRegisterDetail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSql(gstore,
				"AccountRegisterDetail", "ard_id");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "ar_id", store.get("ar_id"));
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteAccountRegister(String caller, int ar_id) {
		handlerService.beforeDel(caller, ar_id);
		// 删除AccountRegister
		baseDao.deleteById("AccountRegister", "ar_id", ar_id);
		// 删除AccountRegisterDetail
		baseDao.deleteById("AccountRegisterDetail", "ard_arid", ar_id);
		// 记录操作
		baseDao.logger.delete(caller, "ar_id", ar_id);
		handlerService.afterDel(caller, ar_id);
	}

	@Override
	public void printAccountRegister(String caller, int ar_id) {

	}

	@Override
	public void auditAccountRegister(String caller, int ar_id) {
		Object status = baseDao.getFieldDataByCondition("AccountRegister",
				"ab_statuscode", "ar_id=" + ar_id);
		StateAssert.auditOnlyCommited(status);
		handlerService.beforeAudit(caller, ar_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"AccountRegister",
				"ar_statuscode='AUDITED',ar_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',AR_AUDITMAN='"
						+ SystemSession.getUser().getEm_name()
						+ "',AR_AUDITdate=sysdate", "ar_id=" + ar_id);
		// 记录操作
		baseDao.logger.audit(caller, "ar_id", ar_id);
		handlerService.afterAudit(caller, ar_id);
	}

	@Override
	public void resAuditAccountRegister(String caller, int ar_id) {
		Object status = baseDao.getFieldDataByCondition("AccountRegister",
				"ab_statuscode", "ar_id=" + ar_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ar_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"AccountRegister",
				"ar_statuscode='ENTERING',ar_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',AR_AUDITMAN='',AR_AUDITdate=null", "ar_id="
						+ ar_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ar_id", ar_id);
		handlerService.afterResAudit(caller, ar_id);
	}

	@Override
	public void submitAccountRegister(String caller, int ar_id) {
		Object status = baseDao.getFieldDataByCondition("AccountRegister",
				"ab_statuscode", "ar_id=" + ar_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ar_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"AccountRegister",
				"ar_statuscode='COMMITED',ar_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ar_id="
						+ ar_id);
		// 记录操作
		baseDao.logger.submit(caller, "ar_id", ar_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ar_id);
	}

	@Override
	public void resSubmitAccountRegister(String caller, int ar_id) {
		Object status = baseDao.getFieldDataByCondition("AccountRegister",
				"ab_statuscode", "ar_id=" + ar_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		handlerService.beforeResSubmit(caller, ar_id);
		baseDao.updateByCondition(
				"AccountRegister",
				"ar_statuscode='ENTERING',ar_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ar_id="
						+ ar_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ar_id", ar_id);
		handlerService.afterResSubmit(caller, ar_id);
	}

}
