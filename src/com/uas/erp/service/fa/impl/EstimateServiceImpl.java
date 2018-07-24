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
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.EstimateService;

@Service("estimateService")
public class EstimateServiceImpl implements EstimateService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public void turnEstimate() {
		int nowym = voucherDao.getNowPddetno("Month-V");// 当前期间
		// 判断是否有未过账的发票和发出商品 全部过账后才可以继续结转
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ab_code) from APBill where ab_class = '应付发票' and ab_statuscode = 'UNPOST' and to_char(ab_date,'yyyymm')="
						+ nowym, String.class);
		if (dets != null) {
			BaseUtil.showError("存在没有过账的应付发票,不能转应付暂估！发票号：" + dets);
		}
		dets = baseDao.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(es_code) from estimate where es_statuscode='UNPOST' and to_char(es_date,'yyyymm')=" + nowym,
						String.class);
		if (dets != null) {
			BaseUtil.showError("存在没有过账的应付暂估,不能转应付暂估！暂估单号：" + dets);
		}
		Employee employee = SystemSession.getUser();
		String res = baseDao.callProcedure("FA_TURNESTIMATE", new Object[] { employee.getEm_id(), employee.getEm_name() });
		if (res != null && res.length() > 0) {
			// 清除setting表记录
			baseDao.execute("DELETE FROM setting where se_what='FA_TURNESTIMATE' AND se_value='Y'");
			BaseUtil.showError(res);
		}
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(es_vouchercode) from Estimate where es_id=? and nvl(es_vouchercode,' ') <>' ' and es_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	@Override
	public void saveEstimate(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		if (store.containsKey("ve_name")) {
			store.remove("ve_name");
		}
		store.put("es_statuscode", "UNPOST");
		store.put("es_status", BaseUtil.getLocalMessage("UNPOST"));
		store.put("es_auditstatuscode", "ENTERING");
		store.put("es_auditstatus", BaseUtil.getLocalMessage("ENTERING"));
		store.put("es_invostatuscode", "PARTAR");
		store.put("es_invostatus", BaseUtil.getLocalMessage("PARTAR"));
		// 保存ARBill
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Estimate", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存ARBillDetail
		for (Map<Object, Object> g : gstore) {
			int esdid = baseDao.getSeqId("EstimateDetail_SEQ");
			g.put("esd_id", esdid);
			g.put("esd_pdid", esdid);
			g.put("esd_code", store.get("es_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gstore, "EstimateDetail");
		baseDao.execute(gridSql);
		baseDao.execute(
				"update EstimateDetail set esd_taxrate=(select ve_taxrate from vendor where ve_code=? and ve_auditstatuscode='AUDITED' and nvl(ve_taxrate,0)<>0) where esd_esid=? and nvl(esd_taxrate,0)=0",
				store.get("es_vendcode"), store.get("es_id"));
		baseDao.execute("update Estimate set es_amount=(select round(sum(esd_amount),2) from EstimateDetail where esd_esid = es_id) where es_id="
				+ store.get("es_id"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "es_id", store.get("es_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store, gstore });
	}

	@Override
	public void updateEstimate(String caller, String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		checkVoucher(store.get("es_id"));
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Estimate", "es_auditstatuscode", "es_id=" + store.get("es_id"));
		StateAssert.updateOnlyEntering(status);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		if (store.containsKey("ve_name")) {
			store.remove("ve_name");
		}
		// 保存arbilldetail前 先判断本次开票数量是否发生改变 还原应收批量开票的数据 逻辑开始
		// 更新采购计划下达数\本次下达数\状态
		// 执行修改前的其它逻辑
		// 修改ARBill
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Estimate", "es_id");
		baseDao.execute(formSql);
		// 修改ARBillDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "EstimateDetail", "esd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("esd_id") != null && !"0".equals(s.get("esd_id")) && Integer.parseInt(s.get("esd_id").toString()) != 0) {// 新添加的数据，id不存在
				Double oldqty = baseDao.getFieldValue("ESTIMATEDETAIL", "esd_qty", "esd_id=" + s.get("esd_id"), Double.class);
				Object[] values = baseDao.getFieldsDataByCondition("PRODIODETAIL", new String[] { "nvl(pd_inqty,0)", "nvl(pd_outqty,0)",
						"nvl(pd_showinvoqty,0)", "nvl(pd_turnesqty,0)" }, "pd_id=" + s.get("esd_pdid"));
				if (Math.abs(Double.parseDouble(String.valueOf(s.get("esd_qty"))))
						+ Math.abs(Double.parseDouble(String.valueOf(values[2]))) + Math.abs(Double.parseDouble(String.valueOf(values[3])))
						- Math.abs(oldqty) > Math.abs(Double.parseDouble(String.valueOf(values[0])))
						+ Math.abs(Double.parseDouble(String.valueOf(values[1])))) {
					BaseUtil.showError("第" + s.get("esd_detno") + "行 超出了最大可转暂估数!");
				} else
					baseDao.updateByCondition("ProdIOdetail",
							"pd_turnesqty=pd_turnesqty+" + (Double.parseDouble(String.valueOf(s.get("esd_qty"))) - oldqty),
							"pd_id=" + s.get("esd_pdid"));
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute(
				"update EstimateDetail set esd_taxrate=(select ve_taxrate from vendor where ve_code=? and ve_auditstatuscode='AUDITED' and nvl(ve_taxrate,0)<>0) where esd_esid=? and nvl(esd_taxrate,0)=0",
				store.get("es_vendcode"), store.get("es_id"));
		baseDao.execute("update Estimate set es_amount=(select round(sum(esd_amount),2) from EstimateDetail where esd_esid = es_id) where es_id="
				+ store.get("es_id"));
		// 记录操作
		baseDao.logger.update(caller, "es_id", store.get("es_id"));
		// 更新上次采购价格、供应商
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });

	}

	@Override
	public void deleteEstimate(String caller, int es_id) {
		Object status = baseDao.getFieldDataByCondition("Estimate", "es_auditstatuscode", "es_id=" + es_id);
		StateAssert.delOnlyEntering(status);
		checkVoucher(es_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, es_id);
		String[] sqls = new String[3];
		sqls[0] = "update prodiodetail set pd_turnesqty=nvl(pd_turnesqty,0)-nvl((select esd_qty from EstimateDetail where esd_esid="
				+ es_id
				+ " and esd_pdid=pd_id),0) where pd_piclass in ('采购验收单','采购验退单','委外验收单','委外验退单') and pd_id in (select esd_pdid from EstimateDetail where esd_esid="
				+ es_id + ")";
		sqls[1] = "delete from EstimateDetail where esd_esid=" + es_id;
		sqls[2] = "delete from Estimate where es_id=" + es_id;
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.delete(caller, "es_id", es_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, es_id);
	}

	@Override
	public void printEstimate(String caller, int es_id) {
		// 只能对状态为[未打印]的订单进行打印操作! 已打印的 也可继续打印
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, es_id);
		// 执行审核操作
		baseDao.updateByCondition("Estimate", "es_printstatuscode='PRINTED',es_printstatus='" + BaseUtil.getLocalMessage("PRINTED") + "'",
				"es_id=" + es_id);
		// 记录操作
		baseDao.logger.print(caller, "es_id", es_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, es_id);
	}

	@Override
	public void auditEstimate(String caller, int es_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Estimate", "es_auditstatuscode", "es_id=" + es_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, es_id);
		// 执行审核操作
		baseDao.updateByCondition("Estimate", "es_auditstatuscode='AUDITED',es_auditstatus='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',es_auditer='" + SystemSession.getUser().getEm_name() + "',es_auditdate=sysdate", "es_id=" + es_id);
		// 记录操作
		baseDao.logger.audit(caller, "es_id", es_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, es_id);
	}

	@Override
	public void resAuditEstimate(String caller, int es_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("Estimate", new String[] { "es_auditstatuscode", "es_statuscode" }, "es_id="
				+ es_id);
		if (!objs[0].equals("AUDITED") || objs[1].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		handlerService.beforeResAudit(caller, es_id);
		// 执行反审核操作
		baseDao.updateByCondition("Estimate", "es_auditstatuscode='ENTERING',es_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',es_auditer='',es_auditdate=null", "es_id=" + es_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "es_id", es_id);
		handlerService.afterResAudit(caller, es_id);
	}

	@Override
	public void submitEstimate(String caller, int es_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Estimate", "es_auditstatuscode", "es_id=" + es_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, es_id);
		// 执行提交操作
		baseDao.updateByCondition("Estimate",
				"es_auditstatuscode='COMMITED',es_auditstatus='" + BaseUtil.getLocalMessage("COMMITED") + "'", "es_id=" + es_id);
		// 记录操作
		baseDao.logger.submit(caller, "es_id", es_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, es_id);
	}

	@Override
	public void resSubmitEstimate(String caller, int es_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Estimate", "es_auditstatuscode", "es_id=" + es_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, es_id);
		// 执行反提交操作
		baseDao.updateByCondition("Estimate",
				"es_auditstatuscode='ENTERING',es_auditstatus='" + BaseUtil.getLocalMessage("ENTERING") + "'", "es_id=" + es_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "es_id", es_id);
		handlerService.afterResSubmit(caller, es_id);
	}

	@Override
	public void postEstimate(String caller, int es_id) {
		Object[] status = baseDao.getFieldsDataByCondition("Estimate", new String[] { "es_statuscode" }, "es_id=" + es_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}

		// 过账前的其它逻辑
		handlerService.beforePost(caller, es_id);
		// 过账前校验明细物料号是否和出入库单据一致
		Object contactdetno = baseDao.getFieldDataByCondition("EstimateDetail", "wmsys.wm_concat(esd_detno)",
				"Esd_Prodcode <>(select pd_prodcode  from prodiodetail where pd_id=esd_pdid) and esd_esid=" + es_id);
		if (contactdetno != null) {
			BaseUtil.showError("明细行:" + contactdetno + "  与对应的出入库单不一致无法过账！");
		}
		// 执行过账操作
		// 存储过程
		String res = baseDao.callProcedure("Sp_CommitEstimate", new Object[] { es_id, SystemSession.getUser().getEm_id() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("Estimate", "es_statuscode='POSTED',es_status='" + BaseUtil.getLocalMessage("POSTED") + "'", "es_id="
				+ es_id);
		baseDao.updateByCondition("EstimateDetail", "esd_status=99", "esd_esid=" + es_id);
		// 记录操作
		baseDao.logger.post(caller, "es_id", es_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, es_id);
	}

	@Override
	public void resPostEstimate(String caller, int es_id) {
		// 只能对状态为[已过账]的单据进行反过账操作!
		Object status = baseDao.getFieldDataByCondition("Estimate", "es_statuscode", "es_id=" + es_id);
		StateAssert.resPostOnlyPosted(status);
		handlerService.beforeResPost(caller, es_id);
		checkVoucher(es_id);
		// 明细行已开票的单据不能反过账
		String checkEsSql = "select count(*) from estimatedetail where nvl(esd_showinvoqty,0)<>0 and esd_esid = '" + es_id + "' ";
		int checkEsSqlRs = baseDao.getCount(checkEsSql);
		if (checkEsSqlRs != 0) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.arp.apbill.resPostEsCheckMsg"));
		}

		// 执行反过账操作
		// 存储过程
		String res = baseDao.callProcedure("Sp_UnCommitEstimate", new Object[] { es_id, SystemSession.getUser().getEm_id() });
		if (res != null && !res.trim().equals("")) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("Estimate",
				"es_auditstatuscode='ENTERING',es_statuscode='UNPOST',es_auditstatus='" + BaseUtil.getLocalMessage("ENTERING")
						+ "',es_status='" + BaseUtil.getLocalMessage("UNPOST") + "'", "es_id=" + es_id);
		baseDao.updateByCondition("EstimateDetail", "esd_status=0", "esd_esid=" + es_id);
		// 记录操作
		baseDao.logger.resPost(caller, "es_id", es_id);
		handlerService.afterResPost(caller, es_id);
	}

}
