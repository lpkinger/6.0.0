package com.uas.erp.service.fa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.PaymentsDetailArpService;

@Service("paymentsDetailArpService")
public class PaymentsDetailArpServiceImpl implements PaymentsDetailArpService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePayments(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Payments",
				"pa_code='" + store.get("pa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		// 保存Payments
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Payments",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存PaymentsDetail
		Object[] pad_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pad_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				pad_id[i] = baseDao.getSeqId("PAYMENTSDETAIL_SEQ");
			}
		} else {
			pad_id[0] = baseDao.getSeqId("PAYMENTSDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"PaymentsDetail", "pad_id", pad_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 多账期付款方式的定义中, 保存时, 如果比率加起来不等于100, 则提示, 但给保存,不给审核
		int count = 0;
		for (Map<Object, Object> map : gstore) {
			String rate = map.get("pad_rate").toString();
			if (rate != null) {
				count += Integer.parseInt(rate.replace("%", ""));
			}
		}
		if (count != 100) {
			BaseUtil.showError("AFTERSUCCESS"
					+ BaseUtil.getLocalMessage("purcPaymentsDetail_rate"));
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void deletePayments(String caller, int pa_id) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, pa_id);
		// 删除Payments
		baseDao.deleteById("Payments", "pa_id", pa_id);
		// 删除PaymentsDetail
		baseDao.deleteById("paymentsdetail", "pad_paid", pa_id);
		// 记录操作
		baseDao.logger.delete(caller, "pa_id", pa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, pa_id);
	}

	@Override
	public void updatePaymentsById(String caller, String formStore,
			String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);

		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改Payments
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Payments",
				"pa_id");
		baseDao.execute(formSql);
		// 修改PaymentsDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"PaymentsDetail", "pad_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("pad_id") == null || s.get("pad_id").equals("")
					|| s.get("pad_id").equals("0")
					|| Integer.parseInt(s.get("pad_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("PAYMENTSDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PaymentsDetail",
						new String[] { "pad_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 多账期付款方式的定义中, 保存时, 如果比率加起来不等于100, 则提示, 但给保存,不给审核
		int count = 0;
		for (Map<Object, Object> map : gstore) {
			String rate = map.get("pad_rate").toString();
			if (rate != null) {
				count += Integer.parseInt(rate.replace("%", ""));
			}
		}
		if (count != 100) {
			BaseUtil.showError("AFTERSUCCESS"
					+ BaseUtil.getLocalMessage("purcPaymentsDetail_rate"));
		}
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void auditPayments(String caller, int pa_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Payments",
				"pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, pa_id);
		// 执行审核操作
		baseDao.updateByCondition("Payments", "pa_auditstatuscode='AUDITED'",
				"pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.audit(caller, "pa_id", pa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, pa_id);
	}

	@Override
	public void resAuditPayments(String caller, int pa_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Payments",
				"pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, pa_id);
		// 执行反审核操作
		baseDao.updateByCondition("Payments", "pa_auditstatuscode='ENTERING'",
				"pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pa_id", pa_id);
		handlerService.afterResAudit(caller, pa_id);
	}

	@Override
	public void submitPayments(String caller, int pa_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Payments",
				"pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.submitOnlyEntering(status);
		// 多账期付款方式的定义中, 保存时, 如果比率加起来不等于100, 则提示, 但给保存,不给审核
		List<Object> objs = baseDao.getFieldDatasByCondition("PaymentsDetail",
				"pad_rate", "pad_paid=" + pa_id);
		int count = 0;
		for (Object obj : objs) {
			String rate = obj.toString();
			if (rate != null) {
				count += Integer.parseInt(rate.replace("%", ""));
			}
		}
		if (count != 100) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("purcPaymentsDetail_rate"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, pa_id);
		// 执行提交操作
		baseDao.updateByCondition("Payments", "pa_auditstatuscode='COMMITED'",
				"pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.submit(caller, "pa_id", pa_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, pa_id);
	}

	@Override
	public void resSubmitPayments(String caller, int pa_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Payments",
				"pa_auditstatuscode", "pa_id=" + pa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, pa_id);
		// 执行反提交操作
		baseDao.updateByCondition("Payments", "pa_auditstatuscode='ENTERING'",
				"pa_id=" + pa_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "pa_id", pa_id);
		handlerService.afterResSubmit(caller, pa_id);
	}

	@Override
	public void bannedPayments(String caller, int pa_id) {
		// 执行禁用操作
		baseDao.updateByCondition("Payments", "pa_auditstatuscode='DISABLE'",
				"pa_id=" + pa_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.banned"), BaseUtil
						.getLocalMessage("msg.bannedSuccess"), "pa_id=" + pa_id));
	}

	@Override
	public void resBannedPayments(String caller, int pa_id) {
		// 执行反禁用操作
		baseDao.updateByCondition("Payments", "pa_auditstatuscode='ENTERING'",
				"pa_id=" + pa_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.unbanned"), BaseUtil
						.getLocalMessage("msg.unbannedSuccess"), "pa_id="
						+ pa_id));
	}
}
