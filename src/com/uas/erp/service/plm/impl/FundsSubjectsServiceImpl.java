package com.uas.erp.service.plm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.plm.FundsSubjectsService;

@Service
public class FundsSubjectsServiceImpl implements FundsSubjectsService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFundSubject(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FundSubject", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save("FundSubject", "fs_id", store.get("fs_id"));
	}

	@Override
	public void updateFundSubject(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FundSubject", "fs_id");
		baseDao.execute(formSql);
		baseDao.logger.update("FundSubject", "fs_id", store.get("fs_id"));
	}

	@Override
	public void deleteFundSubject(int id) {
		handlerService.beforeDel("FundSubject", id);
		// 删除FundSubject
		baseDao.deleteById("FundSubject", "fs_id", id);
		// 记录操作
		baseDao.logger.delete("FundSubject", "fs_id", id);
		// 执行删除后的其它逻辑
		handlerService.afterDel("FundSubject", id);
	}

	public void auditFundSubject(int id) {
		Object status = baseDao.getFieldDataByCondition("FundSubject", "fs_statuscode", "fs_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit("FundSubject", id);
		// 执行审核操作
		baseDao.audit("FundSubject", "fs_id=" + id, "fs_status", "fs_statuscode", "fs_auditdate", "fs_auditor");
		// 记录操作
		baseDao.logger.audit("FundSubject", "fs_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit("FundSubject", id);
	}

	@Override
	public void submitFundSubject(int id) {
		Object status = baseDao.getFieldDataByCondition("FundSubject", "fs_statuscode", "fs_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit("FundSubject", id);
		// 执行反审核操作
		baseDao.submit("FundSubject", "fs_id=" + id, "fs_status", "fs_statuscode");
		// 记录操作
		baseDao.logger.submit("FundSubject", "fs_id", id);
		handlerService.afterSubmit("FundSubject", id);
	}

	@Override
	public void resSubmitFundSubject(int id) {
		Object status = baseDao.getFieldDataByCondition("FundSubject", "fs_statuscode", "fs_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("FundSubject", "fs_id=" + id, "fs_status", "fs_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("FundSubject", "fs_id", id);
	}

	@Override
	public void resAuditFundSubject(int id) {
		Object status = baseDao.getFieldDataByCondition("FundSubject", "fs_statuscode", "fs_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("FundSubject", "fs_id=" + id, "fs_status", "fs_statuscode");
		// 记录操作
		baseDao.logger.resAudit("FundSubject", "fs_id", id);
	}

}