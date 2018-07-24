package com.uas.erp.service.fs.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.ReimbursementApplyService;

@Service
public class ReimbursementApplyServiceImpl implements ReimbursementApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveReimbursementApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ReimbursementApply"));
		baseDao.logger.save(caller, "ra_id", store.get("ra_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateReimbursementApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ReimbursementApply", "ra_id"));
		// 记录操作
		baseDao.logger.update(caller, "ra_id", store.get("ra_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteReimbursementApply(int ra_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ra_id });
		// 删除主表内容
		baseDao.deleteById("ReimbursementApply", "ra_id", ra_id);
		baseDao.logger.delete(caller, "ra_id", ra_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ra_id });
	}

	@Override
	public void submitReimbursementApply(int ra_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ReimbursementApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ra_id });
		// 执行提交操作
		baseDao.submit("ReimbursementApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ra_id", ra_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ra_id });
	}

	@Override
	public void resSubmitReimbursementApply(int ra_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ReimbursementApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ra_id });
		// 执行反提交操作
		baseDao.resOperate("ReimbursementApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ra_id", ra_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ra_id });
	}

	@Override
	public void auditReimbursementApply(int ra_id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("ReimbursementApply", "ra_statuscode", "ra_id=" + ra_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { ra_id });
		baseDao.audit("ReimbursementApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode", "ra_auditdate", "ra_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ra_id", ra_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { ra_id });
	}

	@Override
	public void resAuditReimbursementApply(int ra_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ReimbursementApply", new String[] { "ra_statuscode", "ra_code" }, "ra_id="
				+ ra_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		String dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ar_code) from AccountRegister where ar_sourcetype in ('还款单','逾期还款单') and ar_sourceid=?",
				String.class, ra_id);
		if (dets != null) {
			BaseUtil.showError("已存在银行登记[" + dets + "]，不允许反审核！");
		}
		baseDao.resAuditCheck("ReimbursementApply", ra_id);
		handlerService.beforeResAudit(caller, new Object[] { ra_id });
		// 执行反审核操作
		baseDao.resAudit("ReimbursementApply", "ra_id=" + ra_id, "ra_status", "ra_statuscode", "ra_auditman", "ra_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "ra_id", ra_id);
		handlerService.afterResAudit(caller, new Object[] { ra_id });
	}

}
