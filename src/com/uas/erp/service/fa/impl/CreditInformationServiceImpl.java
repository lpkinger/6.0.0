package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.CreditInformationService;

@Service("CreditInformationService")
public class CreditInformationServiceImpl implements CreditInformationService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCreditInformation(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String ci_no = store.get("ci_no").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("CreditInformation", "ci_no='"
				+ ci_no + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService
				.handler(caller, "save", "before", new Object[] { store });
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"CreditInformation", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ci_id", store.get("ci_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteCreditInformation(int ci_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("CreditInformation",
				"ci_statuscode", "ci_id=" + ci_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { ci_id });
		// 删除CreditInformation
		baseDao.deleteById("CreditInformation", "ci_id", ci_id);
		// 记录操作
		baseDao.logger.delete(caller, "ci_id", ci_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ci_id);
	}

	@Override
	public void updateCreditInformationById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("CreditInformation",
				"ci_statuscode", "ci_id=" + store.get("ci_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改SalePrice
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"CreditInformation", "ci_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "ci_id", store.get("ci_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditCreditInformation(int ci_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("CreditInformation",
				"ci_statuscode", "ci_id=" + ci_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ci_id);
		// 执行审核操作
		baseDao.updateByCondition(caller, "ci_statuscode='AUDITED',ci_status='"
				+ BaseUtil.getLocalMessage("AUDITED") + "'", "ci_id=" + ci_id);
		// 记录操作
		baseDao.logger.audit(caller, "ci_id", ci_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ci_id);
	}

	@Override
	public void resAuditCreditInformation(int ci_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CreditInformation",
				"ci_statuscode", "ci_id=" + ci_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ci_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				caller,
				"ci_statuscode='ENTERING',ci_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ci_id="
						+ ci_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ci_id", ci_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ci_id);
	}

	@Override
	public void submitCreditInformation(int ci_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CreditInformation",
				"ci_statuscode", "ci_id=" + ci_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ci_id);
		// 执行提交操作
		baseDao.updateByCondition(
				caller,
				"ci_statuscode='COMMITED',ci_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ci_id="
						+ ci_id);
		// 记录操作
		baseDao.logger.submit(caller, "ci_id", ci_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ci_id);
	}

	@Override
	public void resSubmitCreditInformation(int ci_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CreditInformation",
				"ci_statuscode", "ci_id=" + ci_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.beforeResSubmit(caller, ci_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				caller,
				"ci_statuscode='ENTERING',ci_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ci_id="
						+ ci_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, ci_id);
	}
}
