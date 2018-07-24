package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.FormulaService;

@Service
public class FormulaServiceServiceImpl implements FormulaService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFormula(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Formula",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "fo_id", store.get("fo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateFormulaById(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Formula",
				"fo_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "fo_id", store.get("fo_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteFormula(int fo_id, String caller) {

		handlerService.beforeDel(caller, fo_id);
		// 删除
		baseDao.deleteById("Formula", "fo_id", fo_id);
		// 记录操作
		baseDao.logger.delete(caller, "fo_id", fo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, fo_id);
	}

	@Override
	public void auditFormula(int fo_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Formula",
				"fo_statuscode", "fo_id=" + fo_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, fo_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"Formula",
				"fo_statuscode='AUDITED',fo_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "fo_id="
						+ fo_id);
		// 记录操作
		baseDao.logger.audit(caller, "fo_id", fo_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, fo_id);
	}

	@Override
	public void resAuditFormula(int fo_id, String caller) {

		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Formula",
				"fo_statuscode", "fo_id=" + fo_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, fo_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"Formula",
				"fo_statuscode='ENTERING',fo_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "fo_id="
						+ fo_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "fo_id", fo_id);
		handlerService.afterResAudit(caller, fo_id);
	}

	@Override
	public void submitFormula(int fo_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Formula",
				"fo_statuscode", "fo_id=" + fo_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, fo_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"Formula",
				"fo_statuscode='COMMITED',fo_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "fo_id="
						+ fo_id);
		// 记录操作
		baseDao.logger.submit(caller, "fo_id", fo_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, fo_id);
	}

	@Override
	public void resSubmitFormula(int fo_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Formula",
				"fo_statuscode", "fo_id=" + fo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, fo_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"Formula",
				"fo_statuscode='ENTERING',fo_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "fo_id="
						+ fo_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "fo_id", fo_id);
		handlerService.afterResSubmit(caller, fo_id);
	}

}
