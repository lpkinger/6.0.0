package com.uas.erp.service.drp.impl;

import com.sun.tools.javac.comp.Flow;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;

import com.uas.erp.model.Employee;
import com.uas.erp.service.drp.RepairWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RepairWorkServiceImpl implements RepairWorkService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveRepairWork(String formStore, String gridStore, String caller) {
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store, gstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"RepairWork", new String[] {}, new Object[] {});
		baseDao.execute(formSql);

		Object[] rwd_id = new Object[1];
		if (gridStore.contains("},")) {// 明细行有多行数据哦
			String[] datas = gridStore.split("},");
			rwd_id = new Object[datas.length];
			for (int i = 0; i < datas.length; i++) {
				rwd_id[i] = baseDao.getSeqId("REPAIRWORKDETAIL_SEQ");
			}
		} else {
			rwd_id[0] = baseDao.getSeqId("REPAIRWORKDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore,
				"RepairWorkDetail", "rwd_id", rwd_id);
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "rw_id", store.get("rw_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, gstore });

	}

	@Override
	public void updateRepairWorkById(String formStore, String gridStore,
			String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });

		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"RepairWork", "rw_id");
		baseDao.execute(formSql);

		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"RepairWorkDetail", "rwd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("rwd_id") == null || s.get("rwd_id").equals("")
					|| s.get("rwd_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("REPAIRWORKDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "RepairWorkDetail",
						new String[] { "rwd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "rw_id", store.get("rw_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void deleteRepairWork(int rw_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, rw_id);
		// 删除purchase
		baseDao.deleteById("RepairWork", "rw_id", rw_id);
		// 删除purchaseDetail
		baseDao.deleteById("RepairWorkDetail", "rwd_rwid", rw_id);
		// 记录操作
		baseDao.logger.delete(caller, "rw_id", rw_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, rw_id);

	}

	@Override
	public void auditRepairWork(int rw_id, String caller) {
		Employee employee = SystemSession.getUser();
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("RepairWork",
				"rw_statuscode", "rw_id=" + rw_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, rw_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"RepairWork",
				"rw_statuscode='AUDITED',rw_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',rw_auditer='"
						+ SystemSession.getUser().getEm_name()
						+ "',rw_auditdate=sysdate", "rw_id=" + rw_id);
		// 记录操作
		baseDao.logger.audit(caller, "rw_id", rw_id);

		// 生成维修结算单
		SqlRowList repairWork = baseDao
				.queryForRowSet("SELECT * FROM REPAIRWORK WHERE RW_ID=" + rw_id);
		SqlRowList repairWorkDetails = baseDao
				.queryForRowSet("SELECT * FROM REPAIRWORKDETAIL WHERE RWD_RWID="
						+ rw_id);

		Float totalServicefee = 0f;
		if (repairWorkDetails != null
				&& repairWorkDetails.getResultList() != null) {
			for (Map detail : repairWorkDetails.getResultList()) {
				Float servicefee = (Float) baseDao.getFieldDataByCondition(
						"Product", "PR_SERVICEFEE",
						"PR_CODE='" + detail.get("RWD_PRODCODE") + "'");
				BigDecimal qty = (BigDecimal) detail.get("RWD_QTY");
				if (servicefee != null && qty != null) {
					totalServicefee += servicefee * qty.intValue();
				}
			}
		}

		int ra_id = baseDao.getSeqId("REPAIRACCOUNT_SEQ");
		Map rw = repairWork.getResultList().get(0);
		String sql = String
				.format("INSERT INTO REPAIRACCOUNT(RA_ID,RA_RWCODE,RA_PRCODE,RA_PRDETAIL,RA_PRSPEC,RA_PRUNIT,RA_SUMREPAIRFEE,RA_ACCOUNTDATE,RA_EMUU,RA_EMNAME) "
						+ "VALUES(%d,'%s','%s','%s','%s','%s',%f,%s,%d,'%s')",
						ra_id,
						rw.get("RW_CODE"),
						rw.get("RW_PRODCODE"),
						rw.get("RW_PRODNAME"),
						rw.get("RW_SPEC"),
						rw.get("RW_UNIT"),
						totalServicefee,
						"to_date('"
								+ new SimpleDateFormat("yyyy-MM-dd")
										.format(new Date()) + "','yyyy-MM-dd')",
						employee.getEm_uu(), employee.getEm_name());
		// 维修结算单
		baseDao.execute(sql);

		int i = 0, rad_id;
		// 维修结算单明细
		if (repairWorkDetails != null
				&& repairWorkDetails.getResultList() != null) {
			for (Map detail : repairWorkDetails.getResultList()) {
				rad_id = baseDao.getSeqId("REPAIRACCOUNTDETAIL_SEQ");
				sql = String
						.format("INSERT INTO REPAIRACCOUNTDETAIL(RAD_ID,RAD_RAID,RAD_DETNO,RAD_PRCODE,RAD_PRDETAIL,RAD_SPEC,RAD_UNIT,RAD_ISOK,RAD_PRICE,RAD_QTY,RAD_REMARK,RAD_SERVICEFEE) "
								+ "VALUES(%d,%d,%d,'%s','%s','%s','%s','%s',%f,%d,'%s',%f)",
								rad_id, ra_id, ++i, detail.get("RWD_PRODCODE"),
								detail.get("RWD_PRODNAME"), detail
										.get("RWD_SPEC"), detail
										.get("RWD_UNIT"), detail
										.get("RWD_ISOK"), detail
										.get("RWD_PRICE"), ((BigDecimal) detail
										.get("RWD_QTY")).intValue(), detail
										.get("RWD_REMARK"),
								baseDao.getFieldDataByCondition(
										"Product",
										"PR_SERVICEFEE",
										"PR_CODE='"
												+ detail.get("RWD_PRODCODE")
												+ "'"));
				baseDao.execute(sql);
			}
		}
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, rw_id);
	}

	@Override
	public void resAuditRepairWork(int rw_id, String caller) {

		Object status = baseDao.getFieldDataByCondition("RepairWork",
				"rw_statuscode", "rw_id=" + rw_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, rw_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"RepairWork",
				"rw_statuscode='ENTERING',rw_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',rw_auditer='',rw_auditdate=null", "rw_id=" + rw_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "rw_id", rw_id);
		handlerService.afterResAudit(caller, rw_id);

	}

	@Override
	public void submitRepairWork(int rw_id, String caller) {

		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("RepairWork",
				"rw_statuscode", "rw_id=" + rw_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, rw_id);
		// 执行提交操作
		baseDao.updateByCondition(
				"RepairWork",
				"rw_statuscode='COMMITED',rw_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "rw_id="
						+ rw_id);
		/*baseDao.updateByCondition("RepairWorkDetail", "rwd_isturn='未转其它单据'",
				"rwd_rwid=" + rw_id);*/
		// 记录操作
		baseDao.logger.submit(caller, "rw_id", rw_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, rw_id);

	}

	@Override
	public void resSubmitRepairWork(int rw_id, String caller) {

		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("RepairWork",
				"rw_statuscode", "rw_id=" + rw_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, rw_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"RepairWork",
				"rw_statuscode='ENTERING',rw_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "rw_id="
						+ rw_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "rw_id", rw_id);
		handlerService.afterResSubmit(caller, rw_id);

	}

	@Override
	@Transactional
	public String batchTurnStockScrap(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		int id = baseDao.getSeqId("ProdInOut_SEQ");
		String code = baseDao.sGetMaxNumber("ProdInOut!StockScrap", 2);
		int detno = 1;
		String insertSql = "insert into ProdInOut(pi_id,pi_inoutno,pi_invostatus,pi_invostatuscode,pi_recordman,pi_recorddate,pi_class,"
				+ "pi_status,pi_statuscode,pi_updateman,pi_updatedate,pi_printstatuscode,pi_printstatus,pi_date) values (?,?,?,?,?,sysdate,?,?,?,?,sysdate,?,?,sysdate)";
		String insertDetSql = "insert into ProdIODetail(pd_pdno,pd_prodcode,pd_outqty,pd_price,pd_total,pd_id,pd_piid,"
				+ "pd_auditstatus,pd_inoutno,pd_accountstatuscode,pd_accountstatus,pd_status,pd_piclass) values (?,?,?,?,?,ProdIODetail_seq.nextval,?,?,?,?,?,?,?)";
		for (Map<Object, Object> map : maps) {
			double total = Double.parseDouble(map.get("rwd_price").toString())
					* Double.parseDouble(map.get("rwd_qty").toString());
			/*baseDao.updateByCondition("RepairWorkDetail", "rwd_isturn='已转报废单'",
					"rwd_id=" + map.get("rwd_id"));*/
			baseDao.execute(
					insertDetSql,
					new Object[] { detno++, map.get("rwd_prodcode"),
							map.get("rwd_qty"), map.get("rwd_price"), total,
							id, "ENTERING", code, "UNACCOUNT",
							BaseUtil.getLocalMessage("UNACCOUNT"), 0, "报废单" });
		}
		baseDao.execute(
				insertSql,
				new Object[] { id, code, BaseUtil.getLocalMessage("ENTERING"),
						"ENTERING", employee.getEm_name(), "报废单",
						BaseUtil.getLocalMessage("UNPOST"), "UNPOST",
						employee.getEm_name(),
						BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT" });
		return "转入成功,报废单号:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!StockScrap&formCondition=pi_idIS"
				+ id
				+ "&gridCondition=pd_piidIS"
				+ id
				+ "')\">"
				+ code
				+ "</a>&nbsp;";
	}

	@Override
	@Transactional
	public String batchTurnAppropriationOut(String data, String caller) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(data);
		Employee employee = SystemSession.getUser();
		int id = baseDao.getSeqId("ProdInOut_SEQ");
		String code = baseDao.sGetMaxNumber("ProdInOut!AppropriationOut", 2);
		int detno = 1;
		String insertSql = "insert into ProdInOut(pi_id,pi_inoutno,pi_invostatus,pi_invostatuscode,pi_recordman,pi_recorddate,pi_class,"
				+ "pi_status,pi_statuscode,pi_updateman,pi_updatedate,pi_printstatuscode,pi_printstatus,pi_date) values (?,?,?,?,?,sysdate,?,?,?,?,sysdate,?,?,sysdate)";
		String insertDetSql = "insert into ProdIODetail(pd_pdno,pd_prodcode,pd_outqty,pd_price,pd_total,pd_id,pd_piid,"
				+ "pd_auditstatus,pd_inoutno,pd_accountstatuscode,pd_accountstatus,pd_status,pd_piclass) values (?,?,?,?,?,ProdIODetail_seq.nextval,?,?,?,?,?,?,?)";
		for (Map<Object, Object> map : maps) {
			double total = Double.parseDouble(map.get("rwd_price").toString())
					* Double.parseDouble(map.get("rwd_qty").toString());
			/*baseDao.updateByCondition("RepairWorkDetail",
					"rwd_isturn='已转售后出库单'", "rwd_id=" + map.get("rwd_id"));*/
			baseDao.execute(
					insertDetSql,
					new Object[] { detno++, map.get("rwd_prodcode"),
							map.get("rwd_qty"), map.get("rwd_price"), total,
							id, "ENTERING", code, "UNACCOUNT",
							BaseUtil.getLocalMessage("UNACCOUNT"), 0, "售后出库单" });
		}
		baseDao.execute(
				insertSql,
				new Object[] { id, code, BaseUtil.getLocalMessage("ENTERING"),
						"ENTERING", employee.getEm_name(), "拨出单",
						BaseUtil.getLocalMessage("UNPOST"), "UNPOST",
						employee.getEm_name(),
						BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT" });
		return "转入成功,售后出库单号:<a href=\"javascript:openUrl('jsps/scm/reserve/prodInOut.jsp?whoami=ProdInOut!SHOut&formCondition=pi_idIS"
				+ id
				+ "&gridCondition=pd_piidIS"
				+ id
				+ "')\">"
				+ code
				+ "</a>&nbsp;";
	}

	@Override
	@Transactional
	public String TurnARBill(String caller, int id) {
		Object rw_code = baseDao.getFieldDataByCondition("RepairWork",
				"rw_code", "rw_id=" + id);
		Employee employee = SystemSession.getUser();
		List<Object[]> datas = baseDao.getFieldsDatasByCondition(
				"RepairWorkDetail", new String[] { "rwd_prodcode",
						"rwd_prodname", "rwd_spec", "rwd_aborfee",
						"rwd_materialfee", "rwd_otherfee" },
				"rwd_isok='是' and rwd_rwid=" + id);
		if (datas.size() == 0) {
			BaseUtil.showError("没有要转成其它应收的费用!");
		}
		baseDao.updateByCondition("RepairWork", "rw_isTurnARBill='已转其它应收'",
				"rw_id=" + id);
		int ab_id = baseDao.getSeqId("ARBill_SEQ");
		String code = baseDao.sGetMaxNumber("ARBill!OTRS", 2);
		int detno = 1;
		String insertSql = "insert into ARBill (ab_id,ab_code,ab_date,ab_yearmonth,ab_auditstatus,ab_refno,ab_paystatus,ab_status,ab_inputname,ab_indate,"
				+ "ab_printstatus,ab_auditstatuscode,ab_statuscode,ab_printstatuscode,ab_class) values (?,?,sysdate,?,?,?,?,?,?,sysdate,?,?,?,?,?)";
		String insertDetSql = "insert into ARBillDetail (abd_detno,abd_prodcode,abd_prodname,abd_prodspec,abd_qty,abd_price,abd_aramount,abd_id,abd_abid) values (?,?,?,?,?,?,?,ARBillDetail_seq.nextval,?)";
		for (Object[] data : datas) {
			double price = Double.parseDouble(data[3].toString())
					+ Double.parseDouble(data[4].toString())
					+ Double.parseDouble(data[5].toString());
			baseDao.execute(insertDetSql, new Object[] { detno++, data[0],
					data[1], data[2], 1, price, price, ab_id });
		}
		Calendar calendar = Calendar.getInstance();
		String yearmonth = calendar.get(Calendar.YEAR) + ""
				+ calendar.get(Calendar.MONTH);
		baseDao.execute(
				insertSql,
				new Object[] { ab_id, code, yearmonth,
						BaseUtil.getLocalMessage("ENTERING"), rw_code,
						BaseUtil.getLocalMessage("UNCOLLECT"),
						BaseUtil.getLocalMessage("UNPOST"),
						employee.getEm_name(),
						BaseUtil.getLocalMessage("UNPRINT"), "ENTERING",
						"UNPOST", "UNPRINT", "其它应收单" });
		return "转入成功,其它应收单号:<a href=\"javascript:openUrl('jsps/fa/ars/arbill.jsp?whoami=ARBill!OTRS&formCondition=ab_idIS"
				+ ab_id
				+ "&gridCondition=abd_abidIS"
				+ ab_id
				+ "')\">"
				+ code
				+ "</a>&nbsp;";
	}

}
