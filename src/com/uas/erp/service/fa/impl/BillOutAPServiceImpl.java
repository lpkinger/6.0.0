package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.fa.BillOutAPService;

@Service("billOutAPService")
public class BillOutAPServiceImpl implements BillOutAPService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Autowired
	private VoucherDao voucherDao;

	/**
	 * XIONGCY 单据日期是否超期
	 */
	private void checkDate(String date) {
		int yearmonth = voucherDao.getPeriodsFromDate("Month-V", date);
		int nowym = voucherDao.getNowPddetno("Month-V");// 当前期间
		if (yearmonth < nowym) {
			BaseUtil.showError("期间" + yearmonth + "已经结转,当前账期在:" + nowym + "<br>请修改日期，或反结转应付账.");
		}
	}

	/**
	 * XIONGCY
	 */
	@Override
	public void saveBillOutAP(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("bi_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BillOutAP", "bi_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存BillOutAP
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BillOutAP", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存BillOutAPDetail
		for (Map<Object, Object> m : grid) {
			m.put("ard_id", baseDao.getSeqId("BillOutAPDETAIL_SEQ"));
			m.put("ard_status", BaseUtil.getLocalMessage("ENTERING"));
			m.put("ard_statuscode", "ENTERING");
			m.put("ard_code", code);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "BillOutAPDetail");
		baseDao.execute(gridSql);
		getTotal(store.get("bi_id"));
		// 记录操作
		baseDao.logger.save(caller, "bi_id", store.get("bi_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
	}

	private void getTotal(Object bi_id) {
		baseDao.execute("update BillOutAPDetail set ard_code=(select bi_code from BillOutAP where ard_biid=bi_id) where ard_biid=" + bi_id
				+ " and not exists (select 1 from BillOutAP where ard_code=bi_code)");
		baseDao.execute("update BillOutAPDetail set ard_nowbalance=round(nvl(ard_nowqty,0)*round(nvl(ard_nowprice,0),8),2) where ard_biid="
				+ bi_id);
		baseDao.execute("update BillOutAPDetail set ard_taxamount=round(ard_nowbalance*nvl(ard_taxrate,0)/(100+nvl(ard_taxrate,0)),2) where ard_biid="
				+ bi_id);
		baseDao.execute("update BillOutAP set bi_amount=round(nvl((select sum(round(ard_nowbalance,2)) from BillOutAPDetail where ard_biid=bi_id),0),2) where bi_id="
				+ bi_id);
		baseDao.execute("update BillOutAP set bi_taxamount=round(nvl((select sum(round(ard_taxamount,2)) from BillOutAPDetail where ard_biid=bi_id),0),2) + nvl(bi_taxdiffer,0) where bi_id="
				+ bi_id);
	}

	@Override
	public void deleteBillOutAP(int bi_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BillOutAP", "bi_statuscode", "bi_id=" + bi_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, bi_id);
		String adidstr = "";
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select abd_id from BillOutAPDetail left join apbilldetail on abd_code=ard_ordercode and abd_detno=ard_orderdetno where ard_biid=?",
						bi_id);
		while (rs.next()) {
			adidstr += "," + rs.getString("abd_id");
		}
		// 删除BillOutAP
		baseDao.deleteById("BillOutAP", "bi_id", bi_id);
		// 删除BillOutAPDetail
		baseDao.deleteById("BillOutAPdetail", "ard_biid", bi_id);
		if (!adidstr.equals("")) {
			adidstr = adidstr.substring(1);
			baseDao.execute("update apbilldetail set abd_yqty=nvl((select sum(ard_nowqty) from billoutapdetail where abd_code=ard_ordercode and abd_detno=ard_orderdetno and nvl(ard_adid,0)=0 group by ard_ordercode,ard_orderdetno ),0) where abd_id in ("
					+ adidstr + ")");
		}
		// 记录操作
		baseDao.logger.delete(caller, "bi_id", bi_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bi_id);
	}

	@Override
	public void updateBillOutAPById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("bi_code").toString();
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BillOutAP", "bi_statuscode", "bi_id=" + store.get("bi_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 修改BillOutAP
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BillOutAP", "bi_id");
		baseDao.execute(formSql);
		// 修改BillOutAPDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "BillOutAPDetail", "ard_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("ard_id") == null || s.get("ard_id").equals("") || s.get("ard_id").equals("0")
					|| Integer.parseInt(s.get("ard_id").toString()) == 0) {// 新添加的数据，id不存在
				s.put("ard_status", BaseUtil.getLocalMessage("ENTERING"));
				s.put("ard_statuscode", "ENTERING");
				s.put("ard_code", code);
				s.put("ard_id", baseDao.getSeqId("BillOutAPDETAIL_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "BillOutAPDetail");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		getTotal(store.get("bi_id"));
		// 记录操作
		baseDao.logger.update(caller, "bi_id", store.get("bi_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
	}

	@Override
	public void printBillOutAP(int bi_id, String caller) {
		getTotal(bi_id);
		// 只能打印审核后的单据!
		Object status = baseDao.getFieldDataByCondition("BillOutAP", "bi_statuscode", "bi_id=" + bi_id);
		if (!status.equals("AUDITED") && !status.equals("PARTRECEIVED") && !status.equals("RECEIVED") && !status.equals("NULLIFIED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.print_onlyAudit"));
		}
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bi_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "bi_id", bi_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bi_id);
	}

	@Override
	@Transactional
	public void auditBillOutAP(int bi_id, String caller) {
		getTotal(bi_id);
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status[] = baseDao.getFieldsDataByCondition("BillOutAP", new String[] { "bi_statuscode", "bi_date" }, "bi_id=" + bi_id);
		StateAssert.auditOnlyCommited(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		// 执行审核前的其它逻辑
		handlerService.handler("BillOutAP", "audit", "before", new Object[] { bi_id });
		// 执行审核操作
		baseDao.updateByCondition("BillOutAP", "bi_statuscode='AUDITED',bi_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',bi_auditer='" + SystemSession.getUser().getEm_name() + "',bi_auditdate=sysdate", "bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutAPDetail", "ard_statuscode='AUDITED',ard_status='" + BaseUtil.getLocalMessage("AUDITED") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logger.audit(caller, "bi_id", bi_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, bi_id);
		// 审核之后自动过账
		if (baseDao.isDBSetting("BillOutAP", "autoAccount")) {
			accountedBillOutAP(bi_id, caller);
		}
	}

	@Override
	public void resAuditBillOutAP(int bi_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status[] = baseDao.getFieldsDataByCondition("BillOutAP", new String[] { "bi_statuscode", "bi_date" }, "bi_id=" + bi_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		handlerService.beforeResAudit(caller, bi_id);
		// 执行反审核操作
		baseDao.updateByCondition("BillOutAP", "bi_statuscode='ENTERING',bi_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',bi_auditer='',bi_auditdate=null", "bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutAPDetail", "ard_statuscode='ENTERING',ard_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "bi_id", bi_id);
		handlerService.afterResAudit(caller, bi_id);
	}

	void check(int bi_id) {
		baseDao.execute("update BillOutAPDetail set ARD_PRICE=round(nvl(ard_price,0),8),ARD_NOWPRICE=round(nvl(ard_nowprice,0),8) where ARD_BIID="
				+ bi_id);
		if (!baseDao.isDBSetting("BillOutAP", "allowUpdatePrice")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(ard_detno) from BillOutAPDetail where ard_biid=? and round(nvl(ard_price,0),8)<>round(nvl(ard_nowprice,0),8)",
							String.class, bi_id);
			if (dets != null) {
				BaseUtil.showError("开票单价与发票单价不一致，不允许进行当前操作！行：" + dets);
			}
		} else {
			String accountCate = baseDao.getDBSetting("BillOutAP", "accountCate");
			if (accountCate == null || accountCate.length() == 0) {
				BaseUtil.showError("调账科目设置未设置！");
			}
		}
	}

	@Override
	public void submitBillOutAP(int bi_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status[] = baseDao.getFieldsDataByCondition("BillOutAP", new String[] { "bi_statuscode", "bi_date" }, "bi_id=" + bi_id);
		StateAssert.submitOnlyEntering(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, bi_id);
		getTotal(bi_id);
		check(bi_id);
		// 执行提交操作
		baseDao.updateByCondition("BillOutAP", "bi_statuscode='COMMITED',bi_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutAPDetail", "ard_statuscode='COMMITED',ard_status='" + BaseUtil.getLocalMessage("COMMITED") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logger.submit(caller, "bi_id", bi_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, bi_id);
	}

	@Override
	public void resSubmitBillOutAP(int bi_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status[] = baseDao.getFieldsDataByCondition("BillOutAP", new String[] { "bi_statuscode", "bi_date" }, "bi_id=" + bi_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		checkDate(status[1].toString().substring(0, 10));
		handlerService.beforeResSubmit(caller, bi_id);
		// 执行反提交操作
		baseDao.updateByCondition("BillOutAP", "bi_statuscode='ENTERING',bi_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutAPDetail", "ard_statuscode='ENTERING',ard_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "bi_id", bi_id);
		handlerService.afterResSubmit(caller, bi_id);
	}

	@Override
	@Transactional
	public void accountedBillOutAP(int bi_id, String caller) {
		// 只能对状态为[未记账]的订单进行操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillOutAP", new String[] { "bi_statuscode", "bi_code" }, "bi_id=" + bi_id);
		if (status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.account_onlyCommited"));
		}
		baseDao.execute("update BillOutAP set bi_rate=(select cm_crrate from CurrencysMonth where cm_crname=bi_currency "
				+ " and CM_YEARMONTH=to_char(bi_date,'yyyymm')) where bi_id=" + bi_id + " and nvl(bi_currency,' ')<>' '");
		// 执行记账前的其它逻辑
		handlerService.handler("BillOutAP", "account", "before", new Object[] { bi_id });
		check(bi_id);
		beforeAccount(bi_id, status[1]);
		// 执行记账操作
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITEBillOutAP", new Object[] { status[1] });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError("单据[" + status[1] + "]" + res);
		}
		getTotal(bi_id);
		baseDao.updateByCondition("BillOutAP", "bi_statuscode='POSTED',bi_status='已过账',bi_postman='" + SystemSession.getUser().getEm_name()
				+ "',bi_postdate=sysdate", "bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutAPDetail", "ard_statuscode='POSTED',ard_status='" + BaseUtil.getLocalMessage("POSTED") + "'",
				"ard_biid=" + bi_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.account"), BaseUtil
				.getLocalMessage("msg.accountSuccess"), "BillOutAP|bi_id=" + bi_id));
		// 执行记账后的其它逻辑
		handlerService.handler("BillOutAP", "account", "after", new Object[] { bi_id });
		baseDao.execute("update APBillDETAIL set abd_invoamount=nvl((select round(sum(nvl(ard_nowbalance,0)),2) from BILLOUTAPDETAIL where ard_ordercode=abd_code and ard_orderdetno=abd_detno),0) where (abd_code,abd_detno) in (select distinct ard_ordercode,ard_orderdetno from BillOutAPDetail where ard_biid="
				+ bi_id + " and ard_statuscode='POSTED')");
		baseDao.execute("update APBill set ab_invoamount=nvl((select round(sum(nvl(abd_invoamount,0)),2) from APBillDetail where abd_abid=ab_id),0) where ab_code in (select distinct ard_ordercode from BillOutAPDetail where ard_biid="
				+ bi_id + ")");
	}

	void beforeAccount(int bi_id, Object bi_code) {
		// 自动产生已过账的其它应付单
		if (baseDao.isDBSetting("BillOutAP", "allowUpdatePrice")) {
			String accountCate = baseDao.getDBSetting("BillOutAP", "accountCate");
			int count = baseDao.getCount("select count(1) from BillOutAPDetail where ard_biid=" + bi_id
					+ " and nvl(ard_price,0)<>nvl(ard_nowprice,0)");
			if (count > 0) {
				int bill = baseDao.getCountByCondition("APBILL", "ab_sourceid=" + bi_id + " and ab_source='应付开票记录' and ab_class='其它应付单'");
				if (bill == 0) {
					String ab_code = baseDao.sGetMaxNumber("APBill!OTDW", 2);
					int ab_id = baseDao.getSeqId("APBILL_SEQ");
					baseDao.execute("insert into apbill (ab_id,ab_code,ab_date,ab_vendid,ab_vendcode,ab_vendname,ab_currency,ab_rate,ab_buyerid,ab_buyer,ab_paymentcode,ab_payments,ab_printstatus,ab_status,ab_auditstatus,ab_paystatus,ab_recorder,ab_recorderid,"
							+ "ab_indate,ab_class,ab_vouchercode,ab_source,ab_sourceid,ab_auditstatuscode,ab_paystatuscode,ab_statuscode,ab_printstatuscode,ab_apamount,ab_payamount,ab_kind,ab_sendstatus,ab_cop,ab_departmentcode,ab_departmentname,ab_refno,AB_SOURCECODE) "
							+ "select "
							+ ab_id
							+ ", '"
							+ ab_code
							+ "',bi_date,ve_id,bi_vendcode,bi_vendname,bi_currency,bi_rate,ve_buyerid,ve_buyername,ve_paymentcode,ve_payment,'"
							+ BaseUtil.getLocalMessage("UNPRINT")
							+ "','"
							+ BaseUtil.getLocalMessage("UNPOST")
							+ "','"
							+ BaseUtil.getLocalMessage("ENTERING")
							+ "','"
							+ BaseUtil.getLocalMessage("UNPAYMENT")
							+ "','"
							+ SystemSession.getUser().getEm_name()
							+ "',"
							+ SystemSession.getUser().getEm_id()
							+ ", sysdate, '其它应付单','UNNEED','应付开票记录',bi_id,'ENTERING','UNPAYMENT','UNPOST','UNPRINT',0,0,'其他','未上传',bi_cop,bi_departmentcode,bi_department,bi_refno,bi_code from billoutap left join vendor on bi_vendcode=ve_code where bi_id="
							+ bi_id);
					baseDao.execute("insert into apbilldetail (abd_id,abd_abid,abd_code,abd_detno,abd_catecode,abd_qty,abd_price,abd_taxrate,abd_apamount,abd_status,abd_sourcekind,abd_sourcetype,abd_sourcedetailid) "
							+ "select apbilldetail_seq.nextval,"
							+ ab_id
							+ ", '"
							+ ab_code
							+ "',rownum,'"
							+ accountCate
							+ "',1,round(ard_nowbalance-ard_orderamount,2),ard_taxrate,round(ard_nowbalance-ard_orderamount,2),'0','BILLOUTAP','应付开票记录',ard_id from billoutapdetail where ard_biid="
							+ bi_id + " and nvl(ard_price,0)<>nvl(ard_nowprice,0)");
					baseDao.execute("update APBill set ab_yearmonth=to_char(ab_date,'yyyymm') where ab_id=" + ab_id);
					baseDao.execute("UPDATE apbilldetail SET abd_noapamount=round(abd_qty*abd_price/(1+abd_taxrate/100),2) WHERE abd_abid="
							+ ab_id);
					baseDao.execute("UPDATE apbilldetail SET abd_catename=(select ca_name from category where ca_code=abd_catecode) WHERE abd_abid="
							+ ab_id + " and abd_catecode is not null");
					baseDao.execute("UPDATE apbilldetail SET abd_taxamount=round((abd_qty*abd_price*abd_taxrate/100)/(1+abd_taxrate/100),2) WHERE abd_abid="
							+ ab_id);
					baseDao.execute("update apbill set ab_taxsum=(select sum(round(((abd_price*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from apbilldetail where abd_abid=ab_id)+nvl(ab_differ,0) where ab_id="
							+ ab_id);
					baseDao.execute("update apbill set ab_apamount=round(nvl((select sum(abd_apamount) from apbilldetail where abd_abid=ab_id),0),2) where ab_id="
							+ ab_id);
					// 存储过程
					String res1 = baseDao.callProcedure("Sp_CommiteAPBill", new Object[] { ab_code, 1 });
					if (res1 != null && !res1.trim().equals("")) {
						BaseUtil.showError(res1);
					}
					baseDao.updateByCondition("APBill",
							"ab_sendstatus='待上传',ab_statuscode='POSTED',ab_status='" + BaseUtil.getLocalMessage("POSTED") + "'", "ab_id="
									+ ab_id);
					baseDao.updateByCondition("APBillDetail", "abd_status=99,abd_statuscode='POSTED'", "abd_abid=" + ab_id);
				}
			}
		}
		// 自动产生已过账的成本调整单
		if (baseDao.isDBSetting("BillOutAP", "autoCostChange")) {
			int count = baseDao
					.getCount("select count(1) from BillOutAPDetail left join apbilldetail on abd_code=ard_ordercode and abd_detno=ard_orderdetno left join apbill on abd_abid=ab_id left join prodiodetail on abd_pdid=pd_id left join batch on pd_batchid=ba_id where ard_biid="
							+ bi_id + " and nvl(ard_price,0)<>nvl(ard_nowprice,0) and nvl(ba_remain,0)>0 and ab_class<>'初始化'");
			if (count > 0) {
				count = baseDao.getCountByCondition("prodinout", "pi_sourcecode='" + bi_code + "' and PI_SOURCETYPE='应付开票记录'");
				if (count == 0) {
					String pi_inoutno = baseDao.sGetMaxNumber("ProdInOut!CostChange", 2);
					int pi_id = baseDao.getSeqId("PRODINOUT_SEQ");
					baseDao.execute("insert into PRODINOUT (pi_id,pi_inoutno,pi_date,pi_departmentcode,pi_departmentname,pi_emcode,pi_emname,pi_status,pi_invostatus,"
							+ "pi_recordman,pi_recorddate,pi_class,pi_sourcetype,pi_sourcecode,pi_invostatuscode,pi_statuscode) "
							+ "select "
							+ pi_id
							+ ", '"
							+ pi_inoutno
							+ "',bi_date,bi_departmentcode,bi_department,'"
							+ SystemSession.getUser().getEm_code()
							+ "','"
							+ SystemSession.getUser().getEm_name()
							+ "','"
							+ BaseUtil.getLocalMessage("UNPOST")
							+ "','"
							+ BaseUtil.getLocalMessage("AUDITED")
							+ "','"
							+ SystemSession.getUser().getEm_name()
							+ "', sysdate, '成本调整单','应付开票记录',bi_code,'AUDITED','UNPOST' from billoutap where bi_id=" + bi_id);
					baseDao.execute("insert into ProdIODetail (pd_id,pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_prodcode,pd_batchcode,pd_batchid,pd_orderqty,"
							+ "pd_orderprice,pd_price,pd_whcode,pd_whname,pd_prodid,pd_accountstatus,pd_status,pd_accountstatuscode,pd_orderid) "
							+ "select ProdIODetail_seq.nextval,"
							+ pi_id
							+ ", '"
							+ pi_inoutno
							+ "','成本调整单',rownum,pd_prodcode,pd_batchcode,pd_batchid,ba_remain,nvl(ba_price,0),"
							+ "ROUND(ard_nowprice*nvl(bi_rate,1)/(1+nvl(ard_taxrate,0)/100),8),pd_whcode,pd_whname,pd_prodid,'未核算',0,'UNACCOUNT',ard_id "
							+ "from BILLOUTAP left join BillOutAPDetail on bi_id=ard_biid left join apbilldetail on abd_code=ard_ordercode and abd_detno=ard_orderdetno left join apbill on abd_abid=ab_id left join prodiodetail on abd_pdid=pd_id left join batch on pd_batchid=ba_id where ard_biid="
							+ bi_id + " and nvl(ard_price,0)<>nvl(ard_nowprice,0) and nvl(ba_remain,0)>0 and ab_class<>'初始化'");
					baseDao.execute(
							"update prodiodetail set pd_total=round((nvl(pd_price,0)-nvl(pd_orderprice,0))*nvl(pd_orderqty,0),2) where pd_piclass ='成本调整单' and pd_piid=?",
							pi_id);
					baseDao.execute(
							"update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from "
									+ "(select pd_whcode,pd_whname from prodiodetail where pd_piid=? order by pd_pdno) where rownum=1 ) where pi_id=?",
							pi_id, pi_id);
					// 存储过程
					String res1 = baseDao.callProcedure("SP_PRODUCTCOSTADJUST", new Object[] { "成本调整单", pi_inoutno, "" });
					if (res1 != null && !res1.trim().equals("")) {
						BaseUtil.showError(res1);
					}
					baseDao.updateByCondition("ProdInOut", "pi_statuscode='POSTED',pi_status='" + BaseUtil.getLocalMessage("POSTED")
							+ "',pi_inoutman='" + SystemSession.getUser().getEm_name() + "',pi_date1=sysdate,pi_sendstatus='待上传'", "pi_id="
							+ pi_id);
					baseDao.updateByCondition("ProdIODetail", "pd_status=99", "pd_piid=" + pi_id);
				}
			}
		}
	}

	@Override
	@Transactional
	public void resAccountedBillOutAP(int bi_id, String caller) {
		// 只能对状态为[已记账]的订单进行反记账操作!
		Object[] status = baseDao.getFieldsDataByCondition("BillOutAP", new String[] { "bi_statuscode", "bi_code" }, "bi_id=" + bi_id);
		if (!status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAccount_onlyAccount"));
		}
		// 执行反记账前的其它逻辑
		handlerService.handler("BillOutAP", "resAccount", "before", new Object[] { bi_id });
		// 判断是否已转成本调整单
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(pi_inoutno) from prodinout where pi_sourcecode=? and pi_class='成本调整单' and pi_sourcetype='应付开票记录'",
				String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("存在关联的成本调整单[" + dets + "]，不允许进行反记账操作!");
		}
		String res = null;
		Object[] ab = baseDao.getFieldsDataByCondition("apbill",
				new String[] { "ab_code", "ab_statuscode", "ab_id", "nvl(ab_payamount,0)" }, "ab_source='应付开票记录' and ab_sourceid=" + bi_id);
		if (ab != null) {
			if (Double.parseDouble(ab[3].toString()) != 0) {
				BaseUtil.showError("关联的其它应付单[" + ab[0] + "]已付款，请先取消付款!");
			}
			if ("POSTED".equals(ab[1])) {
				res = baseDao.callProcedure("Sp_UnCommiteAPBill", new Object[] { ab[0], 1 });
				if (res != null && !res.trim().equals("")) {
					BaseUtil.showError(res);
				}
			}
			baseDao.execute("delete from apbilldetail where abd_abid=" + ab[2]);
			baseDao.execute("delete from apbill where ab_id=" + ab[2]);
		}
		// 执行反记账操作
		res = baseDao.callProcedure("SP_UNCOMMITEBillOutAP", new Object[] { status[1] });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("BillOutAP", "bi_statuscode='ENTERING',bi_status='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',bi_postdate=null,bi_postman=null", "bi_id=" + bi_id);
		baseDao.updateByCondition("BillOutAPDetail", "ard_statuscode='ENTERING',ard_status='" + BaseUtil.getLocalMessage("ENTERING") + "'",
				"ard_biid=" + bi_id);
		baseDao.execute("update ARBillDETAIL set abd_invoamount=nvl((select round(sum(nvl(ard_nowbalance,0)),2) from BILLOUTDETAIL where ard_ordercode=abd_code and ard_orderdetno=abd_detno),0) where (abd_code,abd_detno) in (select distinct ard_ordercode,ard_orderdetno from BillOutDetail where ard_biid="
				+ bi_id + " and ard_statuscode='POSTED')");
		baseDao.execute("update APBill set ab_invoamount=nvl((select round(sum(nvl(abd_invoamount,0)),2) from APBillDetail where abd_abid=ab_id),0) where ab_code in (select distinct ard_ordercode from BillOutAPDetail where ard_biid="
				+ bi_id + ")");
		/*
		 * baseDao.execute(
		 * "UPDATE APBILL SET AB_BILLDATE=(SELECT BI_POSTDATE FROM (SELECT DISTINCT BI_POSTDATE,ARD_ORDERCODE FROM (SELECT BI_POSTDATE,ARD_ORDERCODE,RANK() OVER (PARTITION BY ARD_ORDERCODE ORDER BY BI_POSTDATE DESC) I "
		 * +
		 * "FROM BILLOUTAP,BILLOUTAPDETAIL WHERE ARD_BIID=BI_ID AND BI_STATUSCODE='POSTED' AND ARD_ORDERCODE IN (SELECT ARD_ORDERCODE FROM BILLOUTAPDETAIL WHERE ARD_BIID="
		 * + bi_id +
		 * ")) WHERE I=1) WHERE ARD_ORDERCODE=AB_CODE) WHERE AB_CODE IN (SELECT DISTINCT ARD_ORDERCODE FROM BILLOUTAPDETAIL WHERE ARD_BIID="
		 * + bi_id + ") AND AB_STATUSCODE='POSTED'");
		 */
		// 执行反记账后的其它逻辑
		handlerService.handler("BillOutAP", "resAccount", "after", new Object[] { bi_id });
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.resAccount"), BaseUtil
				.getLocalMessage("msg.resAccountSuccess"), "BillOutAP|bi_id=" + bi_id));
	}

	@Override
	public String[] printBillOutAP(int bi_id, String caller, String reportName, String condition) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bi_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "bi_id", bi_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bi_id);
		return keys;
	}

	@Override
	public String[] printVoucherCodeBillOutAP(int bi_id, String caller, String reportName, String condition) {

		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bi_id);
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "bi_id", bi_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bi_id);
		return keys;
	}

	@Override
	public void updateTaxcode(String caller, int bi_id, String bi_refno, String bi_remark) {
		baseDao.updateByCondition("BillOutAP", "bi_refno='" + bi_refno + "',bi_remark='" + bi_remark + "'", "bi_id=" + bi_id);
		baseDao.logger.others("更新税票信息", "更新成功", caller, "bi_id", bi_id);
	}

	final static String INSERT_PAYPLEASEDETAILDET = "insert into PAYPLEASEDETAILDET(ppdd_id, "
			+ "ppdd_ppdid, ppdd_detno, ppdd_billcode, ppdd_currency, ppdd_paymethod,"
			+ "ppdd_billdate, ppdd_planpaydate, ppdd_account, ppdd_billamount, ppdd_thisapplyamount,"
			+ " ppdd_turnamount, ppdd_paymethodid,ppdd_ppid) " + "values (PAYPLEASEDETAILDET_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,0,?,?)";

	@Override
	public JSONObject turnPayPlease(int id, String caller) {
		Object bicode = baseDao.getFieldDataByCondition("Billoutap", "bi_code", "bi_id=" + id);
		bicode = baseDao.getFieldDataByCondition("PayPlease", "pp_code", "pp_sourcecode='" + bicode + "' and pp_sourcetype='应付开票记录'");
		if (bicode != null && !bicode.equals("")) {
			baseDao.execute("update Billoutap set BI_TURNSTATUS='已转付款申请' where bi_id=" + id);
			BaseUtil.showError("该开票记录已经转入过付款申请单[" + bicode + "]！");
		} else {
			int ppid = 0;
			int ppdid = 0;
			Employee employee = SystemSession.getUser();
			String code = null;
			int count = baseDao.getCount("select count(1) from Billoutap left join BilloutapDETAIL on BI_ID=ARD_BIID "
					+ "left join apbilldetail on ard_ordercode=abd_code and ard_orderdetno=abd_detno "
					+ "full join PRODIODETAIL on NVL(ABD_PDID,0)=PD_ID where bi_id=" + id
					+ " and nvl(PD_PICLASS,' ') not in ('不良品入库单','不良品出库单')");
			if (count > 0) {
				ppid = baseDao.getSeqId("PAYPLEASE_SEQ");
				ppdid = baseDao.getSeqId("PAYPLEASEDETAIL_SEQ");
				code = baseDao.sGetMaxNumber("PayPlease", 2);
				baseDao.execute("INSERT INTO PayPlease(pp_id, pp_code, pp_date, pp_applydept, pp_type, pp_apply, pp_status,"
						+ "pp_total, pp_paystatus, pp_statuscode, pp_paystatuscode, pp_applyid, pp_sourcecode," + "pp_sourcetype)"
						+ " select " + ppid + ",'" + code + "',sysdate,'" + employee.getEm_depart() + "','应付款','" + employee.getEm_name()
						+ "','" + BaseUtil.getLocalMessage("ENTERING") + "', bi_amount, '" + BaseUtil.getLocalMessage("UNPAYMENT")
						+ "', 'ENTERING'," + "'UNPAYMENT'," + employee.getEm_id() + ",bi_code,'应付开票记录'" + " from Billoutap where bi_id="
						+ id);
				baseDao.execute("INSERT INTO PayPleaseDetail(ppd_id, ppd_ppid, ppd_vendid, ppd_detno, ppd_vendcode, ppd_vendname, ppd_paymethod,"
						+ "ppd_bankname, ppd_bankaccount, ppd_currency, ppd_bankman, ppd_applyamount, ppd_auditamount,ppd_startdate,ppd_overdate,"
						+ "ppd_account,ppd_paymethodcode,ppd_paymethodid,PPD_BIID)"
						+ " select "
						+ ppdid
						+ ","
						+ ppid
						+ ",ve_id,1,bi_vendcode,bi_vendname,ve_payment,"
						+ "ve_bank,ve_bankaccount,bi_currency,ve_bankman,bi_amount,bi_amount,null,null,"
						+ "0,ve_paymentcode,ve_paymentid,bi_id from BillOutAP left join vendor on bi_vendcode=ve_code where bi_id=" + id);
			}
			if (ppid != 0 && ppdid != 0) {
				int detno = 1;
				List<Map<Object, Object>> params = new ArrayList<Map<Object, Object>>();
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select ard_ordercode,sum(nvl(ard_nowbalance,0)) ard_nowbalance from BillOutAPDetail where ard_biid=? group by ard_ordercode",
								id);
				while (rs.next()) {
					Map<Object, Object> map = new HashMap<Object, Object>();
					map.put("adamount", rs.getGeneralDouble("ard_nowbalance"));
					map.put("abcode", rs.getGeneralString("ard_ordercode"));
					params.add(map);
				}
				if (params != null && !"".equals(params)) {
					for (Map<Object, Object> m : params) {
						SqlRowList abs = baseDao.queryForRowSet("select * from APBILL where ab_code=?", m.get("abcode"));
						if (abs.next()) {
							baseDao.execute(
									INSERT_PAYPLEASEDETAILDET,
									new Object[] { ppdid, detno++, abs.getObject("ab_code"), abs.getObject("ab_currency"),
											abs.getObject("ab_payments"), abs.getObject("ab_date"), abs.getObject("ab_planpaydate"),
											abs.getObject("ab_payamount"), abs.getObject("ab_apamount"), m.get("adamount"),
											abs.getObject("ab_paymentid"), ppid });
						}
					}
				}
				// 生成日志
				baseDao.logger.others("转付款申请单", "转入成功", caller, "bi_id", id);
				JSONObject j = new JSONObject();
				j.put("pp_id", ppid);
				j.put("pp_code", code);
				baseDao.execute("update Billoutap set BI_TURNSTATUS='已转付款申请' where bi_id=" + id);
				return j;
			}
		}
		return null;
	}
}
