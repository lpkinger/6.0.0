package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.WageitemService;

@Service
public class WageitemServiceImpl implements WageitemService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveWageItem(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WageItem",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "wi_id", store.get("wi_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateWageItemById(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WageItem",
				"qu_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "wi_id", store.get("wi_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteWageItem(int wi_id, String caller) {

		handlerService.beforeDel(caller, wi_id);
		// 删除
		baseDao.deleteById("WageItem", "wi_id", wi_id);
		// 记录操作
		baseDao.logger.delete(caller, "wi_id", wi_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, wi_id);
	}

	@Override
	public void auditWageItem(int wi_id, String caller) {

		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("WageItem",
				"wi_statuscode", "wi_id=" + wi_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, wi_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"WageItem",
				"wi_statuscode='AUDITED',wi_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',wi_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',wi_auditdate=sysdate", "wi_id=" + wi_id);
		// 记录操作
		baseDao.logger.audit(caller, "wi_id", wi_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wi_id);
	}

	@Override
	public void resAuditWageItem(int wi_id, String caller) {

		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WageItem",
				"wi_statuscode", "wi_id=" + wi_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, wi_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"WageItem",
				"wi_statuscode='ENTERING',wi_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',wi_auditer='',wi_auditdate=null", "wi_id=" + wi_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "wi_id", wi_id);
		handlerService.afterResAudit(caller, wi_id);
	}

	@Override
	public void submitWageItem(int wi_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WageItem",
				"wi_statuscode", "wi_id=" + wi_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, wi_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"WageItem",
				"wi_statuscode='COMMITED',wi_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "wi_id="
						+ wi_id);
		// 记录操作
		baseDao.logger.submit(caller, "wi_id", wi_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, wi_id);
	}

	@Override
	public void resSubmitWageItem(int wi_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WageItem",
				"wi_statuscode", "wi_id=" + wi_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, wi_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"WageItem",
				"wi_statuscode='ENTERING',wi_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "wi_id="
						+ wi_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "wi_id", wi_id);
		handlerService.afterResSubmit(caller, wi_id);
	}

}
