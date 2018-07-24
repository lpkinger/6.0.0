package com.uas.erp.service.fs.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.ContractApplyService;

@Service
public class ContractApplyServiceImpl implements ContractApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveContractApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ContractApply"));
		baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateContractApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ContractApply", "ca_id"));
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteContractApply(int ca_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ca_id });
		// 删除主表内容
		baseDao.deleteById("ContractApply", "ca_id", ca_id);
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ca_id });
	}

	@Override
	public void submitContractApply(int ca_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ContractApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { ca_id });
		// 执行提交操作
		baseDao.submit("ContractApply", "ca_id=" + ca_id, "ca_status", "ca_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "ca_id", ca_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { ca_id });
	}

	@Override
	public void resSubmitContractApply(int ca_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ContractApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { ca_id });
		// 执行反提交操作
		baseDao.resOperate("ContractApply", "ca_id=" + ca_id, "ca_status", "ca_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { ca_id });
	}

	@Override
	public void auditContractApply(int ca_id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("ContractApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { ca_id });
		baseDao.audit("ContractApply", "ca_id=" + ca_id, "ca_status", "ca_statuscode", "ca_auditdate", "ca_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { ca_id });
	}

	@Override
	public void resAuditContractApply(int ca_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("ContractApply", new String[] { "ca_statuscode", "ca_code" }, "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.resAuditCheck("ContractApply", ca_id);
		String dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(aa_code) from AccountApply where aa_cacode=?",
				String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("已存在出账申请[" + dets + "]，不允许反审核！");
		}
		dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(in_invoicecode) from FsInvoice where in_cacode=?", String.class,
				status[1]);
		if (dets != null) {
			BaseUtil.showError("已存在发票[" + dets + "]，不允许反审核！");
		}
		handlerService.beforeResAudit(caller, new Object[] { ca_id });
		// 执行反审核操作
		baseDao.resAudit("ContractApply", "ca_id=" + ca_id, "ca_status", "ca_statuscode", "ca_auditman", "ca_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
		handlerService.afterResAudit(caller, new Object[] { ca_id });
	}

}
