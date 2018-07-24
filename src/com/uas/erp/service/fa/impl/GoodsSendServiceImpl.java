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
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.GoodsSendService;

@Service("goodsSendService")
public class GoodsSendServiceImpl implements GoodsSendService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private VoucherDao voucherDao;

	/**
	 * 单据日期是否超期
	 */
	private void checkDate(String date) {
		int yearmonth = voucherDao.getPeriodsFromDate("Month-C", date);
		int nowym = voucherDao.getNowPddetno("Month-C");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>不能删除请,修改日期，或反结转应收账.");
		}
	}

	@Override
	public void turnGoodsSend() {
		int nowym = voucherDao.getNowPddetno("MONTH-C");// 当前期间
		// 判断当前账期是否有未过账的发票和发出商品 全部过账后才可以继续
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ab_code) from ARBill where ab_class = '应收发票' and ab_statuscode = 'UNPOST' and to_char(ab_date,'yyyymm')="
						+ nowym, String.class);
		if (dets != null) {
			BaseUtil.showError("存在没有过账的应收发票，不能转发出商品！发票号：" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(gs_code) from goodssend where gs_statuscode = 'UNPOST' and to_char(gs_date,'yyyymm')=" + nowym,
				String.class);
		if (dets != null) {
			BaseUtil.showError("存在没有过账的发出商品，不能转发出商品！发出商品单号：" + dets);
		}
		String res = baseDao.callProcedure("FA_TURNGOODSSEND", new Object[] { SystemSession.getUser().getEm_id(),
				SystemSession.getUser().getEm_name() });
		if (res != null && res.length() > 0) {
			// 清除setting表记录
			baseDao.execute("DELETE FROM setting where se_what='FA_TURNGOODSSEND' AND se_value='Y'");
			BaseUtil.showError(res);
		}
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(gs_vouchercode) from GoodsSend where gs_id=? and nvl(gs_vouchercode,' ') <>' ' and gs_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	@Override
	public void saveGoodsSend(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		if (store.containsKey("cu_name")) {
			store.remove("cu_name");
		}
		store.put("gs_statuscode", "UNPOST");
		store.put("gs_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("gs_auditstatuscode", "ENTERING");
		store.put("gs_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
		store.put("gs_invostatuscode", "PARTAR");
		store.put("gs_invostatus", BaseUtil.getLocalMessage("PARTAR"));
		// 保存ARBill
		String formSql = SqlUtil.getInsertSqlByMap(store, "GoodsSend");
		baseDao.execute(formSql);
		// 保存ARBillDetail
		for (Map<Object, Object> m : gstore) {
			m.put("gsd_id", baseDao.getSeqId("GoodsSendDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "GoodsSendDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "gs_id", store.get("gs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateGoodsSend(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		checkVoucher(store.get("gs_id"));
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("GoodsSend", "gs_auditstatuscode", "gs_id=" + store.get("gs_id"));
		StateAssert.updateOnlyEntering(status);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		if (store.containsKey("cu_name")) {
			store.remove("cu_name");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "GoodsSend", "gs_id");
		baseDao.execute(formSql);
		// 修改ARBillDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "GoodsSendDetail", "gsd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("gsd_id") != null && !"0".equals(s.get("gsd_id")) && Integer.parseInt(s.get("gsd_id").toString()) != 0) {// 新添加的数据，id不存在
				Double oldqty = baseDao.getFieldValue("GoodsSendDetail", "gsd_qty", "gsd_id=" + s.get("gsd_id"), Double.class);
				Object[] values = baseDao.getFieldsDataByCondition("PRODIODETAIL", new String[] { "nvl(pd_inqty,0)", "nvl(pd_outqty,0)",
						"nvl(pd_showinvoqty,0)", "nvl(pd_turngsqty,0)" }, "pd_id=" + s.get("gsd_pdid"));
				if (Math.abs(Double.parseDouble(String.valueOf(s.get("gsd_qty"))))
						+ Math.abs(Double.parseDouble(String.valueOf(values[2]))) + Math.abs(Double.parseDouble(String.valueOf(values[3])))
						- Math.abs(oldqty) > Math.abs(Double.parseDouble(String.valueOf(values[0])))
						+ Math.abs(Double.parseDouble(String.valueOf(values[1])))) {
					BaseUtil.showError("第" + s.get("gsd_detno") + "行 超出了最大可转发出商品数!");
				} else
					baseDao.updateByCondition("ProdIOdetail",
							"pd_turngsqty=pd_turngsqty+" + (Double.parseDouble(String.valueOf(s.get("gsd_qty"))) - oldqty),
							"pd_id=" + s.get("gsd_pdid"));
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "gs_id", store.get("gs_id"));
		// 更新上次采购价格、供应商
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public void deleteGoodsSend(String caller, int gs_id) {
		/*
		 * Object status = baseDao.getFieldDataByCondition("GoodsSend",
		 * "gs_auditstatuscode", "gs_id=" + gs_id);
		 * StateAssert.delOnlyEntering(status); checkVoucher(gs_id);
		 */

		Object[] args = baseDao.getFieldsDataByCondition("GoodsSend", "gs_auditstatuscode,gs_date", "gs_id=" + gs_id);
		StateAssert.delOnlyEntering(args[0]);
		checkDate(args[1].toString().substring(0, 10));

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, gs_id);
		String[] sqls = new String[3];
		sqls[0] = "update prodiodetail set pd_turngsqty=nvl(pd_turngsqty,0)-nvl((select gsd_qty from goodssenddetail where gsd_gsid="
				+ gs_id
				+ " and gsd_pdid=pd_id),0) where pd_piclass in ('出货单','销售退货单') and pd_id in (select gsd_pdid from goodssenddetail where gsd_gsid="
				+ gs_id + ")";
		sqls[1] = "delete from GoodsSendDetail where gsd_gsid=" + gs_id;
		sqls[2] = "delete from GoodsSend where gs_id=" + gs_id;
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.delete(caller, "gs_id", gs_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, gs_id);
	}

	@Override
	public void printGoodsSend(String caller, int gs_id) {
		// 只能对状态为[未打印]的订单进行打印操作! 已打印的 也可继续打印
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, gs_id);
		// 执行审核操作
		baseDao.updateByCondition("GoodsSend", "gs_printstatuscode='PRINTED',gs_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"gs_id=" + gs_id);
		// 记录操作
		baseDao.logger.print(caller, "gs_id", gs_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, gs_id);
	}

	@Override
	public void auditGoodsSend(String caller, int gs_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("GoodsSend", "gs_auditstatuscode", "gs_id=" + gs_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, gs_id);
		// 执行审核操作
		baseDao.updateByCondition("GoodsSend", "gs_auditstatuscode='AUDITED',gs_auditstatus='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',gs_auditer='" + SystemSession.getUser().getEm_name() + "',gs_auditdate=sysdate", "gs_id=" + gs_id);
		// 记录操作
		baseDao.logger.audit(caller, "gs_id", gs_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, gs_id);
	}

	@Override
	public void resAuditGoodsSend(String caller, int gs_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("GoodsSend", new String[] { "gs_auditstatuscode", "gs_statuscode" }, "gs_id="
				+ gs_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		handlerService.beforeResAudit(caller, gs_id);
		// 执行反审核操作
		baseDao.updateByCondition("GoodsSend", "gs_auditstatuscode='ENTERING',gs_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',gs_auditer='',gs_auditdate=null", "gs_id=" + gs_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "gs_id", gs_id);
		handlerService.afterResAudit(caller, gs_id);
	}

	@Override
	public void submitGoodsSend(String caller, int gs_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("GoodsSend", "gs_auditstatuscode", "gs_id=" + gs_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, gs_id);
		// 执行提交操作
		baseDao.updateByCondition("GoodsSend", "gs_auditstatuscode='COMMITED',gs_auditstatus='" + BaseUtil.getLocalMessage("COMMITED")
				+ "'", "gs_id=" + gs_id);
		// 记录操作
		baseDao.logger.submit(caller, "gs_id", gs_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, gs_id);
	}

	@Override
	public void resSubmitGoodsSend(String caller, int gs_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("GoodsSend", "gs_auditstatuscode", "gs_id=" + gs_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, gs_id);
		// 执行反提交操作
		baseDao.updateByCondition("GoodsSend", "gs_auditstatuscode='ENTERING',gs_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "'", "gs_id=" + gs_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "gs_id", gs_id);
		handlerService.afterResSubmit(caller, gs_id);
	}

	@Override
	public void postGoodsSend(String caller, int gs_id) {
		Object[] status = baseDao.getFieldsDataByCondition("GoodsSend", new String[] { "gs_statuscode" }, "gs_id=" + gs_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}

		// 过账前的其它逻辑
		handlerService.beforePost(caller, gs_id);
		// 执行过账操作
		// Object obj = baseDao.getFieldDataByCondition("GoodsSend", "gs_code",
		// "gs_id=" + gs_id);
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommitSendsGoods", new Object[] { gs_id, SystemSession.getUser().getEm_id() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("GoodsSend", "gs_statuscode='POSTED',gs_status='" + BaseUtil.getLocalMessage("POSTED") + "'", "gs_id="
				+ gs_id);
		baseDao.updateByCondition("GoodsSendDetail", "gsd_status=99", "gsd_gsid=" + gs_id);
		// 记录操作
		baseDao.logger.post(caller, "gs_id", gs_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, gs_id);
	}

	@Override
	public void resPostGoodsSend(String caller, int gs_id) {
		// 只能对状态为[已过账]的单据进行反过账操作!
		Object status = baseDao.getFieldDataByCondition("GoodsSend", "gs_statuscode", "gs_id=" + gs_id);
		StateAssert.resPostOnlyPosted(status);
		checkVoucher(gs_id);
		// 明细行已开票的单据不能反过账
		String checkGsSql = "select count(*) from goodssenddetail where nvl(gsd_showinvoqty,0)<>0 and gsd_gsid = '" + gs_id + "' ";
		int checkGsSqlRs = baseDao.getCount(checkGsSql);
		if (checkGsSqlRs != 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.ars.arbill.resPostGsCheckMsg"));
		}
		handlerService.beforeResPost(caller, gs_id);
		// 执行反过账操作
		// 存储过程
		String res = baseDao.callProcedure("Sp_UnCommitSendsGoods", new Object[] { gs_id, SystemSession.getUser().getEm_id() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("GoodsSend",
				"gs_auditstatuscode='ENTERING',gs_statuscode='UNPOST',gs_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
						+ "',gs_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "gs_id=" + gs_id);
		baseDao.updateByCondition("GoodsSendDetail", "gsd_status=0", "gsd_gsid=" + gs_id);
		// 记录操作
		baseDao.logger.resPost(caller, "gs_id", gs_id);
		handlerService.afterResPost(caller, gs_id);
	}

}
