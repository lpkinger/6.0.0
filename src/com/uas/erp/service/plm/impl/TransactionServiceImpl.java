package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {
	@Autowired
	private BaseDao baseDao;
	private HandlerService handlerService;

	@Override
	public void saveTransaction(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "TeammemberTran", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save("TeammemberTran", "tt_id", store.get("tt_id"));
	}

	@Override
	public void updateTransaction(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TeammemberTran", "tt_id");
		baseDao.execute(formSql);
		baseDao.logger.update("TeammemberTran", "tt_id", store.get("tt_id"));
	}

	@Override
	public void deleteTransaction(int id) {
		handlerService.handler("TeammemberTran", "delete", "before", new Object[] { id });
		// 删除Transaction
		baseDao.deleteById("TeammemberTran", "tt_id", id);
		// 记录操作
		baseDao.logger.delete("TeammemberTran", "tt_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler("Transaction", "delete", "after", new Object[] { id });
	}

	public void auditTransaction(int id) {
		Object status = baseDao.getFieldDataByCondition("TeammemberTran", "tt_statuscode", "tt_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler("TeammemberTran", "audit", "before", new Object[] { id });
		// 执行审核操作
		baseDao.audit("TeammemberTran", "tt_id=" + id, "tt_status", "tt_statuscode");
		// 记录操作
		baseDao.logger.audit("TeammemberTran", "tt_id", id);
		// 执行审核后的其它逻辑
		handlerService.handler("TeammemberTran", "audit", "after", new Object[] { id });

	}

	@Override
	public void submitTransaction(int id) {
		Object status = baseDao.getFieldDataByCondition("TeammemberTran", "tt_statuscode", "tt_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit("TeammemberTran", id);
		// 执行反审核操作
		baseDao.submit("TeammemberTran", "tt_id=" + id, "tt_status", "tt_statuscode");
		// 记录操作
		baseDao.logger.submit("TeammemberTran", "tt_id", id);
		handlerService.afterSubmit("TeammemberTran", id);
	}

	@Override
	public void resSubmitTransaction(int id) {
		Object status = baseDao.getFieldDataByCondition("TeammemberTran", "tt_statuscode", "fs_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("TeammemberTran", "tt_id=" + id, "tt_status", "tt_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("TeammemberTran", "tt_id", id);
	}

	@Override
	public void resAuditTransaction(int id) {
		Object status = baseDao.getFieldDataByCondition("TeammemberTran", "tt_statuscode", "tt_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("TeammemberTran", "tt_id=" + id, "tt_status", "tt_statuscode");
		// 记录操作
		baseDao.logger.resAudit("TeammemberTran", "tt_id", id);
	}
}
