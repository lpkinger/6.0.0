package com.uas.erp.service.hr.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.AccountTransferService;

@Service
public class AccountTransferServiceImpl implements AccountTransferService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAccountTransfer(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[] { store });
		// 保存AccountTransfer
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"AccountTransfer", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller,"at_id", store.get("at_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[] { store });
	}

	@Override
	public void deleteAccountTransfer(int at_id, String caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("AccountTransfer",
				"at_statuscode", "at_id=" + at_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {at_id });
		// 删除AccountTransfer
		baseDao.deleteById("AccountTransfer", "at_id", at_id);
		// 记录操作
		baseDao.logger.delete(caller, "at_id", at_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {at_id });
	}

	@Override
	public void updateAccountTransferById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("AccountTransfer",
				"at_statuscode", "at_id=" + store.get("at_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改AccountTransfer
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"AccountTransfer", "at_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "at_id", store.get("at_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void submitAccountTransfer(int at_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("AccountTransfer",
				"at_statuscode", "at_id=" + at_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { at_id});
		// 执行提交操作
		baseDao.updateByCondition(
				"AccountTransfer",
				"at_statuscode='COMMITED',at_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "at_id="
						+ at_id);
		// 记录操作
		baseDao.logger.submit(caller, "at_id", at_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { at_id});
	}

	@Override
	public void resSubmitAccountTransfer(int at_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AccountTransfer",
				"at_statuscode", "at_id=" + at_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[] { at_id});
		// 执行反提交操作
		baseDao.updateByCondition(
				"AccountTransfer",
				"at_statuscode='ENTERING',at_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "at_id="
						+ at_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "at_id", at_id);
		handlerService.afterResSubmit(caller, new Object[] { at_id});
	}

	@Override
	public void auditAccountTransfer(int at_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("AccountTransfer",
				"at_statuscode", "at_id=" + at_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { at_id });
		// 执行审核操作
		baseDao.audit("AccountTransfer", "at_id=" + at_id, "at_status", "at_statuscode", "at_auditdate", "at_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "at_id", at_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { at_id });
	}

	@Override
	public void resAuditAccountTransfer(int at_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("AccountTransfer",
				"at_statuscode", "at_id=" + at_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.updateByCondition(
				"AccountTransfer",
				"at_statuscode='ENTERING',at_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',at_auditer='',at_auditdate=null", "at_id=" + at_id);
		// 记录操作
		baseDao.logger.audit(caller, "at_id", at_id);
	}

}
