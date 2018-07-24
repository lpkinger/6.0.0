package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.CreditReportServiceService;

@Service("CreditReportServiceService")
public class CreditReportServiceServiceImpl implements
		CreditReportServiceService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCreditReportService(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String crs_code = store.get("crs_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("CreditReportService",
				"crs_code='" + crs_code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil
				.getInsertSqlByMap(store, "CreditReportService");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "crs_id", store.get("crs_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateCreditReportService(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 执行修改操作
		String sql = SqlUtil.getUpdateSqlByFormStore(store,
				"CreditReportService", "crs_id");
		baseDao.execute(sql);
		// 记录操作
		baseDao.logger.update(caller, "crs_id", store.get("crs_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteCreditReportService(int crs_id, String caller) {
		// 只能删除[在录入]的客户资料
		Object status = baseDao.getFieldDataByCondition("CreditReportService",
				"crs_statuscode", "crs_id=" + crs_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.customer.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { crs_id });
		// 执行删除操作
		baseDao.deleteById("CreditReportService", "crs_id", crs_id);
		// 记录操作
		baseDao.logger.delete(caller, "crs_id", crs_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, crs_id);
	}

	@Override
	public void submitCreditReportService(int crs_id, String caller) {
		// 只能提交[在录入]的资料
		Object[] status = baseDao.getFieldsDataByCondition(
				"CreditReportService", new String[] { "crs_statuscode",
						"crs_custcode" }, "crs_id=" + crs_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.customer.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, crs_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"CreditReportService",
				"crs_statuscode='COMMITED', crs_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "crs_id="
						+ crs_id);
		// 记录操作
		baseDao.logger.submit("CreditReportService", "crs_id", crs_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, crs_id);
	}

	@Override
	public void resSubmitCreditReportService(int crs_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交操作
		Object status = baseDao.getFieldDataByCondition("CreditReportService",
				"crs_statuscode", "crs_id=" + crs_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.customer.ressubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, crs_id);
		baseDao.updateByCondition(
				"CreditReportService",
				"crs_statuscode='ENTERING', crs_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "crs_id="
						+ crs_id);
		// 记录操作
		baseDao.logger.resSubmit("CreditReportService", "crs_id", crs_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, crs_id);
	}

	@Override
	public void auditCreditReportService(int crs_id, String caller) {
		// 只能审核[已提交]的客户
		Object[] status = baseDao.getFieldsDataByCondition(
				"CreditReportService", new String[] { "crs_statuscode" },
				"crs_id=" + crs_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.customer.audit_uncommit"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, crs_id);
		baseDao.updateByCondition(
				"CreditReportService",
				"crs_statuscode='AUDITED', crs_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "crs_id="
						+ crs_id);
		// 记录操作
		baseDao.logger.audit("CreditReportService", "crs_id", crs_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, crs_id);
	}

	@Override
	public void resAuditCreditReportService(int crs_id, String caller) {
		// 只能反审核[已审核]的客户
		Object status = baseDao.getFieldDataByCondition("CreditReportService",
				"crs_statuscode", "crs_id=" + crs_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("scm.sale.customer.resaudit_onlyAudited"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, crs_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"CreditReportService",
				"crs_statuscode='ENTERING', crs_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "crs_id="
						+ crs_id);
		// 记录操作
		baseDao.logger.resAudit("CreditReportService", "crs_id", crs_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, crs_id);
	}

}
