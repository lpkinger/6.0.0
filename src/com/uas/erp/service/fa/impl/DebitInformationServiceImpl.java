package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fa.DebitInformationService;

@Service("DebitInformationService")
public class DebitInformationServiceImpl implements DebitInformationService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDebitInformation(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String di_no = store.get("di_no").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DebitInformation", "di_no='"
				+ di_no + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"DebitInformation", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "di_id", store.get("di_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void deleteDebitInformation(int di_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("DebitInformation",
				"di_statuscode", "di_id=" + di_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { di_id });
		// 删除DebitInformation
		baseDao.deleteById("DebitInformation", "di_id", di_id);
		// 记录操作
		baseDao.logger.delete(caller, "di_id", di_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, di_id);
	}

	@Override
	public void updateDebitInformationById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("DebitInformation",
				"di_statuscode", "di_id=" + store.get("di_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改DebitInformation
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"DebitInformation", "di_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "di_id", store.get("di_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditDebitInformation(int di_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("DebitInformation",
				"di_statuscode", "di_id=" + di_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, di_id);
		// 执行审核操作
		baseDao.updateByCondition(caller, "di_statuscode='AUDITED',di_status='"
				+ BaseUtil.getLocalMessage("AUDITED") + "'", "di_id=" + di_id);
		// 记录操作
		baseDao.logger.audit(caller, "di_id", di_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, di_id);
	}

	@Override
	public void resAuditDebitInformation(int di_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("DebitInformation",
				"di_statuscode", "di_id=" + di_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, di_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				caller,
				"di_statuscode='ENTERING',di_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "di_id="
						+ di_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "di_id", di_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, di_id);
	}

	@Override
	public void submitDebitInformation(int di_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("DebitInformation",
				"di_statuscode", "di_id=" + di_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, di_id);
		// 执行提交操作
		baseDao.updateByCondition(
				caller,
				"di_statuscode='COMMITED',di_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "di_id="
						+ di_id);
		// 记录操作
		baseDao.logger.submit(caller, "di_id", di_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, di_id);
	}

	@Override
	public void resSubmitDebitInformation(int di_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DebitInformation",
				"di_statuscode", "di_id=" + di_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, di_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				caller,
				"di_statuscode='ENTERING',di_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "di_id="
						+ di_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "di_id", di_id);
		// 执行反提交后的其它逻辑
		handlerService.afterSubmit(caller, di_id);
	}
}
