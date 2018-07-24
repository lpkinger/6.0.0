package com.uas.erp.service.crm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.CustomerImpDistrApplyService;

@Service(value = "customerImpDistrApplyService")
public class CustomerImpDistrApplyServiceImpl implements
		CustomerImpDistrApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerImpDistrApply(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		for (Map<Object, Object> m : gstore) {
			m.put("cad_id", baseDao.getSeqId("CustomerImpDistrApplyDet_SEQ"));
			m.put("cad_cuid", store.get("ca_cuid"));
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"CustomerImpDistrApply", new String[] {}, new String[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore,
				"CustomerImpDistrApplyDet");
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCustomerImpDistrApply(int ca_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ca_id);
		// 删除purchase
		baseDao.deleteById("CustomerImpDistrApplyDet", "cad_caid", ca_id);
		baseDao.deleteById("CustomerImpDistrApply", "ca_id", ca_id);
		// 记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ca_id);
	}

	@Override
	public void updateCustomerImpDistrApply(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"CustomerImpDistrApply", "ca_id");
		// 修改MProjectPlanDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"CustomerImpDistrApplyDet", "cad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cad_id") == null || s.get("cad_id").equals("")
					|| s.get("cad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CustomerImpDistrApplyDet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s,
						"CustomerImpDistrApplyDet", new String[] { "cad_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		// 如果主记录改变了客户，反应到明细上
		baseDao.updateByCondition("CustomerImpDistrApplyDet", "cad_cuid="
				+ store.get("ca_cuid"), "cad_caid=" + store.get("ca_id"));
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitCustomerImpDistrApply(int ca_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition(
				"CustomerImpDistrApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ca_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"CustomerImpDistrApply",
				"ca_statuscode='COMMITED',ca_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logger.submit(caller, "ca_id", ca_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ca_id);
	}

	@Override
	public void resSubmitCustomerImpDistrApply(int ca_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		handlerService.handler("CustomerImpDistrApply", "resCommit", "after",
				new Object[] { ca_id });
		Object status = baseDao.getFieldDataByCondition(
				"CustomerImpDistrApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.updateByCondition(
				"CustomerImpDistrApply",
				"ca_statuscode='ENTERING',ca_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ca_id="
						+ ca_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		handlerService.afterResSubmit(caller, ca_id);
	}

	@Override
	public void auditCustomerImpDistrApply(int ca_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"CustomerImpDistrApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ca_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"CustomerImpDistrApply",
				"ca_statuscode='AUDITED',ca_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',ca_auditor='"
						+ SystemSession.getUser().getEm_name()
						+ "',ca_auditdate="
						+ DateUtil.parseDateToOracleString(null, new Date()),
				"ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ca_id);
	}

	@Override
	public void resAuditCustomerImpDistrApply(int ca_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition(
				"CustomerImpDistrApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, ca_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"CustomerImpDistrApply",
				"ca_statuscode='ENTERING',ca_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',ca_auditor='',ca_auditdate=null", "ca_id=" + ca_id);
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
		handlerService.afterResAudit(caller, ca_id);
	}

}
