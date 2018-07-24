package com.uas.erp.service.drp.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.drp.TerminalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TerminalServiceImpl implements TerminalService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTerminal(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Terminal",
				"te_code='" + store.get("te_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		handlerService.beforeSave(caller, new Object[] { store });

		String formSql = SqlUtil.getInsertSqlByFormStore(store, "terminal",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "te_id", store.get("te_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void updateTerminalById(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "terminal",
				"te_id");
		baseDao.execute(formSql);

		// 记录操作
		baseDao.logger.update(caller, "te_id", store.get("te_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteTerminal(int te_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, te_id);
		// 删除purchase
		baseDao.deleteById("terminal", "te_id", te_id);
		// 记录操作
		baseDao.logger.delete(caller, "te_id", te_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, te_id);

	}

	@Override
	public void auditTerminal(int te_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("terminal",
				"te_statuscode", "te_id=" + te_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, te_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"terminal",
				"te_statuscode='AUDITED',te_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',te_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',te_auditdate=sysdate", "te_id=" + te_id);
		// 记录操作
		baseDao.logger.audit(caller, "te_id", te_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, te_id);

	}

	@Override
	public void resAuditTerminal(int te_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("terminal",
				"te_statuscode", "te_id=" + te_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, te_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"terminal",
				"te_statuscode='ENTERING',te_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',te_auditer='',te_auditdate=null", "te_id=" + te_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "te_id", te_id);
		handlerService.afterResAudit(caller, te_id);

	}

	@Override
	public void submitTerminal(int te_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("terminal",
				"te_statuscode", "te_id=" + te_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, te_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"terminal",
				"te_statuscode='COMMITED',te_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "te_id="
						+ te_id);
		// 记录操作
		baseDao.logger.submit(caller, "te_id", te_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, te_id);

	}

	@Override
	public void resSubmitTerminal(int te_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("terminal",
				"te_statuscode", "te_id=" + te_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, te_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"terminal",
				"te_statuscode='ENTERING',te_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "te_id="
						+ te_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "te_id", te_id);
		handlerService.afterResSubmit(caller, te_id);

	}

}
