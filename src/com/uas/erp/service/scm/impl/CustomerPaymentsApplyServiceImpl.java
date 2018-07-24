package com.uas.erp.service.scm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.CustomerPaymentsApplyService;

@Service
public class CustomerPaymentsApplyServiceImpl implements CustomerPaymentsApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerPaymentsApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		StringBuffer sb = new StringBuffer();
		String err = null;
		Object cuid = store.get("ca_cuid");
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		for (Map<Object, Object> m : gstore) {
			if (m.get("cad_operation") != null && !"增加".equals(m.get("cad_operation"))) {
				if (m.get("cad_oldpaymentcode") != null) {
					err = baseDao.getJdbcTemplate().queryForObject(
							"select max(cp_paymentcode) from CustomerPayments where cp_paymentcode=? and cp_cuid=?", String.class,
							m.get("cad_oldpaymentcode"), cuid);
					if (err == null) {
						sb.append("明细行：").append(m.get("cad_detno")).append("的原收款方式在客户多收款方式中不存在！<br>");
					}
				}
				// 需要考虑账期变更的情况
				/*
				 * if(m.get("cad_paymentcode") != null){ err =
				 * baseDao.getJdbcTemplate().queryForObject(
				 * "select max(cp_paymentcode) from CustomerPayments where cp_paymentcode=? and cp_cuid=?"
				 * , String.class, m.get("cad_paymentcode"), cuid); if (err !=
				 * null) { sb.append("明细行：").append(m.get("cad_detno")).append(
				 * "的新收款方式在客户多收款方式中已存在！<br>"); } }
				 */
			}
			m.put("cad_id", baseDao.getSeqId("CustomerPaymentsApplyDet_SEQ"));
			m.put("cad_cuid", store.get("ca_cuid"));
		}
		if (sb.length() > 0)
			BaseUtil.showError(sb.toString());
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustomerPaymentsApply", new String[] {}, new String[] {});
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "CustomerPaymentsApplyDet");
		baseDao.execute(gridSql);
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "ca_id", store.get("ca_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteCustomerPaymentsApply(int ca_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CustomerPaymentsApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, ca_id);
		// 删除purchase
		baseDao.deleteById("CustomerPaymentsApplyDet", "cad_caid", ca_id);
		baseDao.deleteById("CustomerPaymentsApply", "ca_id", ca_id);
		// 记录操作
		baseDao.logger.delete(caller, "ca_id", ca_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, ca_id);
	}

	@Override
	public void updateCustomerPaymentsApply(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object status = baseDao.getFieldDataByCondition("CustomerPaymentsApply", "ca_statuscode", "ca_id=" + store.get("ca_id"));
		StateAssert.updateOnlyEntering(status);
		StringBuffer sb = new StringBuffer();
		String err = null;
		Object cuid = store.get("ca_cuid");
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustomerPaymentsApply", "ca_id");
		// 修改MProjectPlanDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "CustomerPaymentsApplyDet", "cad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("cad_id") == null || s.get("cad_id").equals("") || s.get("cad_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("CustomerPaymentsApplyDet_SEQ");
				s.put("cad_cuid", store.get("ca_cuid"));
				String sql = SqlUtil.getInsertSqlByMap(s, "CustomerPaymentsApplyDet", new String[] { "cad_id" }, new Object[] { id });
				gridSql.add(sql);
			} else {
				if (s.get("cad_operation") != null && !"增加".equals(s.get("cad_operation"))) {
					if (s.get("cad_oldpaymentcode") != null && !"".equals(s.get("cad_oldpaymentcode"))) {
						err = baseDao.getJdbcTemplate().queryForObject(
								"select max(cp_paymentcode) from CustomerPayments where cp_paymentcode=? and cp_cuid=?", String.class,
								s.get("cad_oldpaymentcode"), cuid);
						if (err == null) {
							sb.append("明细行：").append(s.get("cad_detno")).append("的原收款方式在客户多收款方式中不存在！<br>");
						}
					}
				}
			}
		}
		if (sb.length() > 0)
			BaseUtil.showError(sb.toString());
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		// 如果主记录改变了客户，反应到明细上
		baseDao.updateByCondition("CustomerPaymentsApplyDet", "cad_cuid=" + store.get("ca_cuid"), "cad_caid=" + store.get("ca_id"));
		// 记录操作
		baseDao.logger.update(caller, "ca_id", store.get("ca_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void submitCustomerPaymentsApply(int ca_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("CustomerPaymentsApply", new String[] { "ca_statuscode", "ca_cuid" }, "ca_id="
				+ ca_id);
		StateAssert.submitOnlyEntering(status[0]);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(cad_detno) from CustomerPaymentsApplyDet where nvl(cad_oldpaymentcode,' ')<>' ' and cad_oldpaymentcode not in (select cp_paymentcode from CustomerPayments where cp_cuid=?) and cad_caid=?",
						String.class, status[1], ca_id);
		if (dets != null) {
			BaseUtil.showError("明细行原收款方式在客户多收款方式中不存在，不允许提交!行号：" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ca_id);
		// 执行提交操作
		baseDao.updateByCondition("CustomerPaymentsApply", "ca_statuscode='COMMITED',ca_status='" + BaseUtil.getLocalMessage("COMMITED")
				+ "'", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.submit(caller, "ca_id", ca_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ca_id);
	}

	@Override
	public void resSubmitCustomerPaymentsApply(int ca_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerPaymentsApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, ca_id);
		// 执行反提交操作
		baseDao.updateByCondition("CustomerPaymentsApply", "ca_statuscode='ENTERING',ca_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ca_id", ca_id);
		handlerService.afterResSubmit(caller, ca_id);
	}

	@Override
	@Transactional
	public void auditCustomerPaymentsApply(int ca_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("CustomerPaymentsApply", new String[] { "ca_statuscode", "ca_cucode" }, "ca_id="
				+ ca_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ca_id);
		// 反应到客户分配表中
		List<Object[]> gridData = baseDao.getFieldsDatasByCondition("CustomerPaymentsApplyDet", new String[] { "cad_operation",
				"cad_detno", "cad_cuid", "cad_oldpaymentcode", "cad_paymentcode", "cad_payment", "cad_isdefault", "cad_newduedays" },
				"cad_caid=" + ca_id);
		boolean hasDefault = false;
		Object DefaultCuid = null;
		Object DefaultPaymentcode = null;
		Object DefaultDueDays = 0;
		for (Object[] o : gridData) {
			String sql = null;
			if ("是".equals(o[6] + "")) {
				hasDefault = true;
				DefaultCuid = o[2];
				DefaultPaymentcode = o[4];
				DefaultDueDays = o[7];
			}
			if ("增加".equals(o[0])) {
				Object cu_code = baseDao.getFieldDataByCondition("customer", "cu_code", "cu_id=" + o[2]);
				if (baseDao.getCount("select count(1) from CustomerPayments where cp_paymentcode='" + o[4] + "' and cp_cuid=" + o[2]) == 0) {
					int id = baseDao.getSeqId("CustomerPayments_SEQ");// 这张表多数帐套没序列
					sql = "INSERT INTO CustomerPayments (cp_id,cp_cuid,cp_detno,"
							+ "cp_paymentcode,cp_payment,cp_isdefault,cp_cucode) VALUES(" + id + "," + o[2] + "," + o[1] + ",'" + o[4]
							+ "','" + o[5] + "','" + o[6] + "','" + cu_code + "')";
				}
			} else if ("修改".equals(o[0])) {
				sql = "UPDATE CustomerPayments SET cp_paymentcode='" + o[4] + "',cp_payment='" + o[5] + "',cp_isdefault='" + o[6]
						+ "' WHERE cp_cuid=" + o[2] + " AND cp_paymentcode='" + o[3] + "'";
			} else if ("删除".equals(o[0])) {
				hasDefault = false;
				int count = baseDao.getCount("select count(*) from CustomerPayments where cp_cuid=" + o[2]);
				Object pa = baseDao.getFieldDataByCondition("CustomerPayments", "cp_isdefault", "cp_cuid=" + o[2] + " and cp_paymentcode='"
						+ o[3] + "'");
				baseDao.execute("DELETE FROM CustomerPayments WHERE cp_cuid=" + o[2] + " and cp_paymentcode='" + o[3] + "'");
				if (count == 1 || (pa != null && "是".equals(pa))) {
					BaseUtil.appendError("当前客户的默认收款方式被删掉，请设置新的默认收款方式！");
					baseDao.execute("update Customer set cu_paymentscode=null, cu_payments=null where cu_id=" + o[2]
							+ " and cu_paymentscode='" + o[3] + "'");
				}
			}
			if (sql != null) {
				baseDao.execute(sql);
			}
			if (hasDefault) {
				if (DefaultPaymentcode != null && DefaultCuid != null) {
					baseDao.updateByCondition("CustomerPayments", "cp_isdefault='否'", "cp_cuid=" + DefaultCuid);
					baseDao.updateByCondition("CustomerPayments", "cp_isdefault='是'", "cp_cuid=" + DefaultCuid + " AND cp_paymentcode='"
							+ DefaultPaymentcode + "'");
					// 修改客户资料中的付款方式Customer
					Object[] d = baseDao.getFieldsDataByCondition("Payments", new String[] { "pa_id", "pa_name" }, "pa_code='"
							+ DefaultPaymentcode + "' and pa_class='收款方式'");
					if (d == null) {
						BaseUtil.showError("付款方式不正确，请核对后重试！");
					}
					String update = "cu_paymentid=" + d[0] + ",cu_payments='" + d[1] + "',cu_paymentscode='" + DefaultPaymentcode + "'";
					if (DefaultDueDays != null) {
						update += ",cu_duedays=" + DefaultDueDays;
					}
					baseDao.updateByCondition("Customer", update, "cu_id=" + DefaultCuid);
					if (baseDao.checkIf("Payments", "pa_code='" + DefaultPaymentcode + "' and pa_creditcontrol='是' ")) {
						baseDao.updateByCondition("customer", "cu_enablecredit='是' ", "cu_id=" + DefaultCuid);
					}
				}
			}
		}
		if (baseDao.isDBSetting("Customer!Base", "creditControl")) {
			baseDao.execute("update customer set cu_enablecredit=(select pa_creditcontrol from payments where pa_code=cu_paymentscode and pa_class='收款方式') where cu_code='"
					+ status[1] + "' and nvl(cu_paymentscode,' ')<>' '");
		}
		// 执行审核操作
		baseDao.updateByCondition("CustomerPaymentsApply",
				"ca_statuscode='AUDITED',ca_status='" + BaseUtil.getLocalMessage("AUDITED") + "',ca_auditor='"
						+ SystemSession.getUser().getEm_name() + "',ca_auditdate=" + DateUtil.parseDateToOracleString(null, new Date()),
				"ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.audit(caller, "ca_id", ca_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ca_id);
	}

	@Override
	public void resAuditCustomerPaymentsApply(int ca_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("CustomerPaymentsApply", "ca_statuscode", "ca_id=" + ca_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.updateByCondition("CustomerPaymentsApply", "ca_statuscode='ENTERING',ca_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',ca_auditor='',ca_auditdate=null", "ca_id=" + ca_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ca_id", ca_id);
	}

}
