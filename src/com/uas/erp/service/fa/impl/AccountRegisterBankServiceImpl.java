package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Workbook;
import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.b2c.service.buyer.PurchaseOrderService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AccountRegisterDao;
import com.uas.erp.dao.common.CategoryStrDao;
import com.uas.erp.dao.common.DetailGridDao;
import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.AccountRegisterDetailAss;
import com.uas.erp.model.Category;
import com.uas.erp.model.DetailGrid;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.ProcessService;
import com.uas.erp.service.fa.AccountRegisterBankService;

@Service("accuntRegisterBankService")
public class AccountRegisterBankServiceImpl implements AccountRegisterBankService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private AccountRegisterDao accountRegisterDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private CategoryStrDao categoryStrDao;
	@Autowired
	private EnterpriseDao enterpriseDao;
	@Autowired
	private DetailGridDao detailGridDao;
	@Autowired
	private PayPleaseDao payPleaseDao;
	@Autowired
	private VoucherDao voucherDao;
	@Autowired
	private PurchaseOrderService purchaseOrderService;
	@Autowired
	private ProcessService processService;
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	@Transactional
	public void saveAccountRegister(String formStore, String[] gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
		List<Map<Object, Object>> assgrid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("AccountRegister", "ar_code='" + store.get("ar_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		baseDao.checkCloseMonth("MONTH-B", store.get("ar_date"));
		if (!"费用".equals(store.get("ar_type")) && !"转存".equals(store.get("ar_type")) && !"应付票据付款".equals(store.get("ar_type"))
				&& !"其它付款".equals(store.get("ar_type")) && !"其它收款".equals(store.get("ar_type")) && !"应收票据收款".equals(store.get("ar_type"))
				&& !"自动转存".equals(store.get("ar_type")) && !"暂收款".equals(store.get("ar_type")) && !"保理付款".equals(store.get("ar_type"))
				&& !"保理收款".equals(store.get("ar_type"))) {
			if (store.get("ar_accountcurrency").equals(store.get("ar_arapcurrency"))) {
				if (!"1".equals(store.get("ar_araprate"))) {
					BaseUtil.showError("币别一致，冲账汇率不为1，请修改!");
				}
			}
		}
		// 应收票据收款、应付票据付款 必须有来源，不允许手工新增
		String type = String.valueOf(store.get("ar_type"));
		Employee employee = SystemSession.getUser();
		if ("应收票据收款".equals(type) || "应付票据付款".equals(type))
			BaseUtil.showError(type + " 必须有来源，不允许手工新增");
		store.put("ar_emid", employee.getEm_id());
		store.put("ar_recordman", employee.getEm_name());
		store.put("ar_status", BaseUtil.getLocalMessage("ENTERING"));
		store.put("ar_statuscode", "ENTERING");
		store.put("ar_poststatus", BaseUtil.getLocalMessage("UNPOST"));
		store.put("ar_poststatuscode", "UNPOST");
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存
		List<String> sqls = new ArrayList<String>();
		sqls.add(SqlUtil.getInsertSqlByMap(store, "AccountRegister"));
		String err_out = "";
		int count = grid.size();
		for (Map<Object, Object> map : grid) {
			String cate_code = map.get("ard_catecode").toString(); // 科目编码
			String ard_detno = map.get("ard_detno").toString();
			if (!cate_code.trim().equals("")) {
				Object o = baseDao.getFieldDataByCondition("CATEGORY", "ca_isleaf", "ca_code='" + cate_code + "'");
				String isleaf = o == null ? "0" : o.toString();
				if (isleaf.equals("0")) {
					// 科目不是子节点 报错
					err_out = err_out + ard_detno;
					if (grid.indexOf(map) == count - 1) {

					} else {
						err_out = err_out + ",";
					}
				}
			}
		}
		if (!err_out.equals("")) {
			BaseUtil.showError("第" + err_out + "条明细行科目不是末级科目,不能保存明细行!");
		}
		// 保存AccountRegisterDetail
		Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(assgrid, "ars_ardid");
		for (Map<Object, Object> map : grid) {
			int id = baseDao.getSeqId("ACCOUNTREGISTERDETAIL_SEQ");
			assgrid = list.get(map.get("ard_id"));
			if (assgrid != null) {
				for (Map<Object, Object> m : assgrid) {// AccountRegisterDetailAss
					m.put("ars_ardid", id);
					m.put("ars_type", caller);
				}
				sqls.addAll(SqlUtil.getInsertSqlbyList(assgrid, "AccountRegisterDetailAss", "ars_id"));
			}
			map.put("ard_id", id);
		}
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(grid, "AccountRegisterDetail"));
		baseDao.execute(sqls);
		String insertAssDetSql = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) values (?,?,?,?,?,?,'AccountRegister!Bank')";
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from accountregister left join accountregisterdetail on ard_arid=ar_id where ard_arid=? and nvl(ard_catecode,' ')<>' '",
						store.get("ar_id"));
		while (rs.next()) {
			Object catecode = rs.getObject("ard_catecode");
			int ardid = rs.getInt("ard_id");
			SqlRowList ass = baseDao.queryForRowSet("select ca_assname from category where ca_code=? and nvl(ca_assname,' ')<>' '",
					catecode);
			if (ass.next()) {
				String assStr = ass.getString("ca_assname");
				String[] codes = assStr.split("#");
				for (String assname : codes) {
					int i = baseDao.getCount("select count(1) from accountregisterdetailass where ars_ardid=" + ardid
							+ " and ARS_ASSTYPE='" + assname + "'");
					if (i == 0) {
						Object maxno = baseDao.getFieldDataByCondition("accountregisterdetailass", "max(nvl(ars_detno,0))", "ars_ardid="
								+ ardid);
						maxno = maxno == null ? 0 : maxno;
						int detno = Integer.parseInt(maxno.toString()) + 1;
						int arsid = baseDao.getSeqId("ACCOUNTREGISTERDETAILASS_SEQ");
						baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, null, null });
					}
					int arsid = baseDao.getFieldValue("accountregisterdetailass", "ars_id", "ars_ardid=" + ardid + " and ARS_ASSTYPE='"
							+ assname + "'", Integer.class);
					if ("部门".equals(assname) && StringUtil.hasText(rs.getObject("ar_departmentcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_departmentcode")
								+ "', ars_assname='" + rs.getObject("ar_departmentname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
					if ("项目".equals(assname) && StringUtil.hasText(rs.getObject("ar_prjcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_prjcode")
								+ "', ars_assname='" + rs.getObject("ar_prjname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
					if ("客户往来".equals(assname) && StringUtil.hasText(rs.getObject("ar_custcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_custcode")
								+ "', ars_assname='" + rs.getObject("ar_custname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '" + " and nvl(ars_asscode,' ')=' '");
					}
				}
			}
		}
		updateErrorString(Integer.parseInt((String) store.get("ar_id")));
		baseDao.logger.save(caller, "ar_id", store.get("ar_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	void checkcmrate(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(cm_yearmonth) from accountregister,currencysmonth where nvl(ar_accountcurrency,' ')<>' ' and ar_accountcurrency=cm_crname and cm_yearmonth=to_char(ar_date,'yyyymm') and ar_id=?",
						String.class, id);
		if (dets == null) {
			BaseUtil.showError("单据日期所在期间月度汇率未设置，不允许进行当前操作!");
		}
	}

	void checkVoucher(Object id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_vouchercode) from AccountRegister where ar_id=? and ar_vouchercode is not null and ar_vouchercode<>'UNNEED'",
						String.class, id);
		if (dets != null) {
			BaseUtil.showError("已有凭证，不允许进行当前操作!凭证编号：" + dets);
		}
	}

	// 还原来源付款申请已转数状态
	void rePayPlease(Object pp_code, Object ppdId, Object type) {
		if ("应付款".equals(type)) {
			payPleaseDao.updateDetailAmount(pp_code);
		} else if ("预付款".equals(type)) {
			payPleaseDao.updateDetailAmountYF(pp_code);
		}
		baseDao.execute("update PayPleaseDetail set ppd_statuscode=null,ppd_status=null where ppd_id=" + ppdId
				+ " and nvl(ppd_account,0)=0");
		baseDao.execute("update PayPleaseDetail set ppd_statuscode='PARTBR',ppd_status='部分转银行登记' where ppd_id=" + ppdId
				+ " and round(nvl(ppd_account,0),2)< round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0");
		baseDao.execute("update PayPleaseDetail set ppd_statuscode='TURNBR',ppd_status='已转银行登记' where ppd_id=" + ppdId
				+ " and round(nvl(ppd_account,0),2)= round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0");
	}

	@Override
	public void deleteAccountRegister(int ar_id, String caller) {
		checkVoucher(ar_id);
		Object[] date = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_date", "ar_source", "ar_sourcetype",
				"ar_sourceid", "ar_type", "ar_code", "ar_bankid" }, "ar_id=" + ar_id);
		SqlRowList rs = baseDao.queryForRowSet("select rb_kind,rb_code from recbalance where rb_sourceid=? and nvl(rb_source,' ')='Bank'",
				ar_id);
		if (rs.next()) {
			BaseUtil.showError("已转" + rs.getObject("rb_kind") + "[" + rs.getObject("rb_code") + "]，不允许删除银行登记！");
		}
		rs = baseDao.queryForRowSet("select pb_kind,pb_code from paybalance where pb_sourceid=? and nvl(pb_source,' ')='Bank'", ar_id);
		if (rs.next()) {
			BaseUtil.showError("已转" + rs.getObject("pb_kind") + "[" + rs.getObject("pb_code") + "]，不允许删除银行登记！");
		}
		rs = baseDao.queryForRowSet("select pr_kind,pr_code from prerec where pr_sourceid=? and nvl(pr_source,' ')='Bank'", ar_id);
		if (rs.next()) {
			BaseUtil.showError("已转" + rs.getObject("pr_kind") + "[" + rs.getObject("pr_code") + "]，不允许删除银行登记！");
		}
		rs = baseDao.queryForRowSet("select pp_type,pp_code from prepay where pp_sourceid=? and nvl(pp_source,' ')='Bank'", ar_id);
		if (rs.next()) {
			BaseUtil.showError("已转" + rs.getObject("pp_type") + "[" + rs.getObject("pp_code") + "]，不允许删除银行登记！");
		}
		if (StringUtil.hasText(date[1])) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wmsys.wm_concat(cm_yearmonth) from accountregister,currencysmonth where nvl(ar_accountcurrency,' ')<>' ' and ar_accountcurrency=cm_crname and cm_yearmonth=to_char(ar_date,'yyyymm') and ar_id=?",
							String.class, ar_id);
			if (dets == null) {
				BaseUtil.showError("银行登记单据日期所在期间月度汇率未设置，不允许进行当前操作!");
			}
		}
		if (date[4] != null && "转存".equals(date[4].toString())) {
			String res = null;
			Object[] code = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_code", "ar_statuscode" },
					"ar_type='自动转存' and ar_source='" + date[5] + "'");
			if (code != null) {
				if (code[1] != null && "POSTED".equals(code[1])) {
					res = baseDao.callProcedure("SP_UNCOMMITEREGISTER",
							new Object[] { code[0], String.valueOf(SystemSession.getUser().getEm_id()) });
					if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
						BaseUtil.showError(res);
					}
				}
				baseDao.execute("delete from AccountRegister where ar_code='" + code[0] + "'");
			}
		}
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { ar_id });
		List<String> sqls = new ArrayList<String>();
		Object ppdId = baseDao.getFieldDataByCondition("accountregister", "nvl(ar_sourceid,0)", "ar_id=" + ar_id
				+ " and ar_sourcetype ='付款申请'");
		Object sourceId = baseDao.getFieldDataByCondition("accountregister", "nvl(ar_sourceid,0)", "ar_id=" + ar_id
				+ " and ar_sourcetype ='模具付款申请'");
		Object[] fpId = baseDao.getFieldsDataByCondition("accountregister", new String[] { "nvl(ar_sourceid,0)", "ar_accountcurrency",
				"ar_sourcetype" }, "ar_id=" + ar_id + " and ar_sourcetype in ('费用报销单','总务申请单','借款申请单','还款申请单','差旅费报销单')");
		Object[] ctId = baseDao.getFieldsDataByCondition("accountregister", new String[] { "nvl(ar_sourceid,0)", "ar_accountcurrency" },
				"ar_id=" + ar_id + " and ar_sourcetype='费用比例报销单' ");
		Object rbid = baseDao.getFieldDataByCondition("accountregister", "nvl(ar_sourceid,0)", "ar_id=" + ar_id
				+ " and ar_sourcetype ='回款通知单'");
		if (rbid != null) {
			sqls.add("update recbalancenotice set rb_yamount=0 where rb_id=" + rbid);
		}

		// 删除
		sqls.add("delete from AccountRegister where ar_id=" + ar_id);
		// 删除AccountRegisterDetail
		sqls.add("delete from AccountRegisterDetail where ard_arid=" + ar_id);
		if (sourceId != null) {
			sqls.add("update MOULDFEEPLEASE set mp_payamount=(select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype ='模具付款申请' and nvl(ar_sourceid,0)="
					+ sourceId + "), mp_thispaydate=null where mp_id=" + sourceId);
			sqls.add("update MOULDFEEPLEASE set mp_paystatuscode='PARTPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT")
					+ "' where nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + sourceId);
			sqls.add("update MOULDFEEPLEASE set mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT")
					+ "' where nvl(mp_payamount,0)=0 and mp_id=" + sourceId);
		}
		if (ctId != null) {
			Object ct = baseDao.getFieldDataByCondition("CUSTOMTABLE", "ct_varchar50_1", "ct_id=" + ctId[0]);
			if (ct != null && ctId[1] != null && !ctId[1].equals(ct)) {
				Object ctrate = baseDao.getFieldDataByCondition("CurrencysMonth", "nvl(cm_crrate,0)", "cm_crname='" + ct
						+ "' and cm_yearmonth=to_char(to_date('" + date[0] + "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
				if (ctrate != null && Double.parseDouble(ctrate.toString()) != 0) {
					sqls.add("update CUSTOMTABLE set ct_number_2=nvl((select sum(round((NVL(ar_payment,0)-NVL(ar_deposit,0))*nvl(ar_accountrate,0)/"
							+ Double.parseDouble(ctrate.toString())
							+ ",2)) from accountregister where ar_sourcetype ='费用比例报销单' and nvl(ar_sourceid,0)="
							+ ctId[0]
							+ "),0)"
							+ " where ct_id=" + ctId[0]);
				}
			} else {
				sqls.add("update CUSTOMTABLE set ct_number_2=nvl((select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype ='费用比例报销单' and nvl(ar_sourceid,0)="
						+ ctId[0] + "),0)" + " where ct_id=" + ctId[0]);
			}
			sqls.add("update CUSTOMTABLE set ct_varchar50_13='部分支付' where nvl(ct_amount,0)>nvl(ct_number_2,0)+nvl(ct_number_3,0) and ct_id="
					+ ctId[0]);
			sqls.add("update CUSTOMTABLE set ct_varchar50_13='未支付' where nvl(ct_number_2,0)+nvl(ct_number_3,0)=0 and ct_id=" + ctId[0]);
		}
		if (fpId != null) {
			Object fp = baseDao.getFieldDataByCondition("FeePlease", "fp_v13", "fp_id=" + fpId[0]
					+ " and fp_kind in ('费用报销单','总务申请单','借款申请单','还款申请单','差旅费报销单')");
			double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "bap_sourcetype='总务申请单' and bap_sourceid=" + fpId[0]);// 已转应付票据金额
			double brc = baseDao.getSummaryByField("BillARChange", "brc_amount", "brc_sourcetype='总务申请单' and brc_sourceid=" + fpId[0]);// 已转应收票据异动金额
			if (fp != null && fpId[1] != null && !fpId[1].equals(fp)) {
				Object fprate = baseDao.getFieldDataByCondition("CurrencysMonth", "nvl(cm_crrate,0)", "cm_crname='" + fp
						+ "' and cm_yearmonth=to_char(to_date('" + date[0] + "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
				if (fprate != null && Double.parseDouble(fprate.toString()) != 0) {
					sqls.add("update FeePlease set fp_n1=nvl((select sum(round((NVL(ar_payment,0)-NVL(ar_deposit,0))*nvl(ar_accountrate,0)/"
							+ Double.parseDouble(fprate.toString())
							+ ",2)) from accountregister where ar_sourcetype =fp_kind and nvl(ar_sourceid,0)="
							+ fpId[0]
							+ "),0)+"
							+ (bap + brc) + " where fp_id=" + fpId[0] + " and fp_kind in ('费用报销单','总务申请单','借款申请单','差旅费报销单')");
					sqls.add("update FeePlease set fp_n1=nvl((select sum(round((NVL(ar_deposit,0)-NVL(ar_payment,0))*nvl(ar_accountrate,0)/"
							+ Double.parseDouble(fprate.toString())
							+ ",2)) from accountregister where ar_sourcetype =fp_kind and nvl(ar_sourceid,0)="
							+ fpId[0]
							+ "),0) where fp_id=" + fpId[0] + " and fp_kind = '还款申请单'");
				}
			} else {
				sqls.add("update FeePlease set fp_n1=nvl((select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype =fp_kind and nvl(ar_sourceid,0)="
						+ fpId[0]
						+ "),0)+"
						+ (bap + brc)
						+ " where fp_id="
						+ fpId[0]
						+ " and fp_kind in ('费用报销单','总务申请单','借款申请单','差旅费报销单')");
				sqls.add("update FeePlease set fp_n1=nvl((select sum(NVL(ar_deposit,0)-NVL(ar_payment,0)) from accountregister where ar_sourcetype =fp_kind and nvl(ar_sourceid,0)="
						+ fpId[0] + "),0) where fp_id=" + fpId[0] + " and fp_kind ='还款申请单'");
			}
			sqls.add("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_kind='费用报销单' and fp_id="
					+ fpId[0]);
			sqls.add("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_kind='费用报销单' and fp_id=" + fpId[0]);
			sqls.add("update FeePlease set fp_v7='部分支付' where nvl(fp_n1,0)>0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_kind in ('总务申请单','借款申请单','还款申请单','差旅费报销单') and fp_id="
					+ fpId[0]);
			sqls.add("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)=0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_kind in ('总务申请单','借款申请单','还款申请单','差旅费报销单') and fp_id="
					+ fpId[0]);
		}
		// 删除AccountRegisterDetailAss
		sqls.add("delete from AccountRegisterDetailAss where ars_ardid in (select ard_id from AccountRegisterDetail where ard_arid="
				+ ar_id + ")");
		Object[] puid = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_sourceid", "ar_source", "ar_sourcetype" },
				"ar_id =" + ar_id);
		baseDao.execute(sqls);
		if (puid[2] != null && "采购单".equals(puid[2])) {
			// 没有其他来源于该采购单的银行登记单，更新来源采购单的【转银行登记】为未生成
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(ar_code) from AccountRegister where ar_type = '转存' and nvl(ar_source,' ') ='" + puid[1] + "'",
					String.class);
			if (dets == null) {
				baseDao.updateByCondition("Purchase", "PU_TRANSFERBANK ='未生成'", "pu_id =" + puid[0]);
			}
		}
		if (ppdId != null) {
			String pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id=" + ppdId,
					String.class);
			rePayPlease(pp_code, ppdId, date[4]);
		}
		// 删除bank$task表
		if (date[6] != null) {
			baseDao.deleteByCondition("bank$task", "id_='" + date[6] + "'");
		}
		// 记录操作
		baseDao.logger.delete(caller, "ar_id", ar_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { ar_id });
	}

	@Override
	public void updateAccountRegisterById(String formStore, String[] gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore[0]);
		Object vId = store.get("ar_id").toString();
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_statuscode", "ar_date", "ar_type" },
				"ar_id=" + vId);
		if ("POSTED".equals(status[0])) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		checkVoucher(vId);
		baseDao.checkCloseMonth("MONTH-B", store.get("ar_date"));
		if (!"费用".equals(store.get("ar_type")) && !"转存".equals(store.get("ar_type")) && !"应付票据付款".equals(store.get("ar_type"))
				&& !"其它付款".equals(store.get("ar_type")) && !"其它收款".equals(store.get("ar_type")) && !"应收票据收款".equals(store.get("ar_type"))
				&& !"自动转存".equals(store.get("ar_type")) && !"暂收款".equals(store.get("ar_type")) && !"预付款".equals(store.get("ar_type"))
				&& !"保理付款".equals(store.get("ar_type")) && !"保理收款".equals(store.get("ar_type"))) {
			if (store.get("ar_accountcurrency").equals(store.get("ar_arapcurrency"))) {
				if (!"1".equals(store.get("ar_araprate"))) {
					BaseUtil.showError("币别一致，冲账汇率不为1，请修改!");
				}
			}
		}
		baseDao.execute("update accountregister set ar_accountrate=(select nvl(cm_crrate,1) from currencysmonth where ar_accountcurrency=cm_crname and to_char(ar_date,'yyyymm')=cm_yearmonth) where ar_id="
				+ vId);
		// 应收票据收款、应付票据付款 必须有来源，不允许手工新增
		String type = String.valueOf(store.get("ar_type"));
		if (("应收票据收款".equals(type) || "应付票据付款".equals(type)) && store.get("ar_source") == null)
			BaseUtil.showError(type + " 必须有来源，不允许手工新增");

		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 修改
		boolean bool1 = false;
		boolean bool2 = false;
		boolean bool3 = false;
		boolean bool4 = false;
		boolean bool5 = false;
		boolean bool6 = false;
		boolean bool7 = false;
		boolean bool8 = false;
		String amountsql = null;
		String pp_code = null;
		Object ppdId = baseDao.getFieldDataByCondition("accountregister", "nvl(ar_sourceid,0)", "ar_id=" + vId
				+ " and ar_sourcetype ='付款申请'");
		if (ppdId != null) {
			pp_code = baseDao.getFieldValue("payplease left join PaypleaseDetail on pp_id=ppd_ppid", "pp_code", "ppd_id=" + ppdId,
					String.class);
			Double applytotal = baseDao.getFieldValue("PayPleaseDetail", "nvl(ppd_applyamount,0)", "ppd_id=" + ppdId, Double.class);
			double ar = baseDao.getSummaryByField("accountregister", "ar_apamount", "ar_sourcetype ='付款申请' and ar_sourceid=" + ppdId
					+ " and ar_id<>" + vId);// 已转银行登记金额
			double bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "BAP_PAYBILLCODE='" + pp_code + "'");// 已转应付票据金额
			double brc = baseDao.getSummaryByField("BillARChange", "brc_cmamount", "brc_ppcode='" + pp_code + "'");// 已转应收票据异动金额
			double pb = baseDao.getSummaryByField("PayBalance", "pb_apamount", "pb_sourcecode='" + pp_code + "' and pb_source='付款申请'");// 已转付款类单据金额
			double pp = baseDao.getSummaryByField("PrePay", "pp_jsamount", "pp_sourcecode='" + pp_code + "' and pp_source='预付款申请'");// 已转预付款单据金额
			double ytotal = 0.0;
			if ("应付款".equals(status[2])) {
				ytotal = ar + bap + brc + pb;
			} else if ("预付款".equals(status[2])) {
				ytotal = ar + bap + brc + pp;
			}
			double thisamount = Double.parseDouble(store.get("ar_apamount").toString());
			if (NumberUtil.compare(applytotal, (ytotal + thisamount), 2) == -1) {
				BaseUtil.showError("本次冲应付款金额+已转金额超过来源付款申请金额！本次冲应付款金额[" + thisamount + "]已转金额[" + ytotal + "]申请金额[" + applytotal + "]");
			}
			if (grid.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (Map<Object, Object> s : grid) {
					if ("应付款".equals(type)) {
						if (StringUtil.hasText(s.get("ard_ordercode"))) {
							Object bill = s.get("ard_ordercode");
							double oldthisamount = 0;
							double turnamount = 0;
							double billamount = 0;
							double tmount = Double.parseDouble(s.get("ard_nowbalance").toString());
							SqlRowList rs = baseDao
									.queryForRowSet(
											"select ard_nowbalance,ppdd_turnamount,ppdd_thisapplyamount from accountregisterdetail left join PayPleasedetaildet on ard_orderid=ppdd_id where ard_id=?",
											s.get("ard_id"));
							if (rs.next()) {
								oldthisamount = rs.getGeneralDouble("ard_nowbalance");
								turnamount = rs.getGeneralDouble("ppdd_turnamount");
								billamount = rs.getGeneralDouble("ppdd_thisapplyamount");
							}
							if (Math.abs(billamount) < Math.abs(turnamount - oldthisamount + tmount)) {
								sb.append("超来源付款申请金额！发票[" + bill + "]，付款申请金额[" + billamount + "]，已转金额["
										+ (turnamount - oldthisamount + tmount) + "]<hr/>");
							}
						}
					} else if ("预付款".equals(type)) {
						if (StringUtil.hasText(s.get("ard_ordercode")) || StringUtil.hasText(s.get("ard_makecode"))) {
							Object bill = null;
							Object pclass = null;
							double oldthisamount = 0;
							double turnamount = 0;
							double billamount = 0;
							double tmount = Double.parseDouble(s.get("ard_nowbalance").toString());
							SqlRowList rs = baseDao
									.queryForRowSet(
											"select ard_nowbalance,ppdd_turnamount,ppdd_thisapplyamount from accountregisterdetail left join PayPleasedetaildet on ard_ppddid=ppdd_id where ard_id=?",
											s.get("ard_id"));
							if (rs.next()) {
								oldthisamount = rs.getGeneralDouble("ard_nowbalance");
								turnamount = rs.getGeneralDouble("ppdd_turnamount");
								billamount = rs.getGeneralDouble("ppdd_thisapplyamount");
							}
							if (StringUtil.hasText(s.get("ard_ordercode"))) {
								bill = s.get("ard_ordercode");
								pclass = "采购单";
							} else if (StringUtil.hasText(s.get("ard_makecode"))) {
								bill = s.get("ard_makecode");
								pclass = "委外单";
							}
							if (Math.abs(billamount) < Math.abs(turnamount - oldthisamount + tmount)) {
								sb.append("超来源付款申请金额！" + pclass + "[" + bill + "]，付款申请金额[" + billamount + "]，已转金额["
										+ (turnamount - oldthisamount + tmount) + "]<hr/>");
							}
						}
					}
				}
				if (sb.length() > 0) {
					BaseUtil.showError(sb.toString());
				}
			}
			bool6 = true;
		}
		Object sourceId = baseDao.getFieldDataByCondition("accountregister", "nvl(ar_sourceid,0)", "ar_id=" + vId
				+ " and ar_sourcetype ='模具付款申请'");
		if (sourceId != null) {
			Double mptotal = baseDao.getFieldValue("MOULDFEEPLEASE", "nvl(mp_total,0)", "mp_id=" + sourceId, Double.class);
			Double ytotal = baseDao.getSummaryByField("accountregister", "NVL(ar_payment,0)-NVL(ar_deposit,0)",
					"ar_sourcetype ='模具付款申请' and ar_sourceid=" + sourceId + " and ar_id<>" + vId);
			double ar_payment = Double.parseDouble(store.get("ar_payment").toString());
			double ar_deposit = Double.parseDouble(store.get("ar_payment").toString());
			if (NumberUtil.compare(mptotal, (ytotal + (ar_payment - ar_deposit)), 2) == -1) {
				BaseUtil.showError("填写支出金额超过来源模具付款申请金额！");
			}
			bool3 = true;
		}
		double bap = 0.0;
		double brc = 0.0;
		Object[] ctId = baseDao.getFieldsDataByCondition("accountregister", new String[] { "nvl(ar_sourceid,0)", "ar_accountcurrency" },
				"ar_id=" + vId + " and ar_sourcetype ='费用比例报销单'");
		int ct_id = 0;
		if (ctId != null) {
			ct_id = Integer.parseInt(ctId[0].toString());
			Object mptotal = baseDao.getFieldDataByCondition("CUSTOMTABLE", "nvl(ct_amount,0)-nvl(ct_number_3,0)", "ct_id=" + ct_id);
			double ytotal = 0.0;
			double thisamount = 0.0;
			Object ct = baseDao.getFieldDataByCondition("CUSTOMTABLE", "ct_varchar50_1", "ct_id=" + ct_id);
			if (ct != null && store.get("ar_accountcurrency") != null && !ct.equals(store.get("ar_accountcurrency"))) {
				Object ctrate = baseDao.getFieldDataByCondition("CurrencysMonth", "nvl(cm_crrate,0)", "cm_crname='" + ct
						+ "' and cm_yearmonth=to_char(to_date('" + status[1] + "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
				if (ctrate != null && Double.parseDouble(ctrate.toString()) != 0) {
					thisamount = NumberUtil
							.formatDouble(
									(Double.parseDouble(store.get("ar_payment").toString()) + Double.parseDouble(store.get("ar_deposit")
											.toString()))
											* Double.parseDouble(store.get("ar_accountrate").toString())
											/ Double.parseDouble(ctrate.toString()), 0);

					ytotal = baseDao.getSummaryByField("accountregister",
							"round((NVL(ar_payment,0)-NVL(ar_deposit,0))*nvl(ar_accountrate,0)/" + Double.parseDouble(ctrate.toString())
									+ ",2)", "ar_sourcetype ='费用比例报销单' and ar_sourceid=" + ct_id + " and ar_id<>" + vId);
					if (NumberUtil.compare(Double.parseDouble(mptotal.toString()), (ytotal + thisamount), 2) == -1) {
						BaseUtil.showError("填写支出金额超过来源费用比例报销单金额！");
					}
					bool8 = true;
					amountsql = "select sum(round((NVL(ar_payment,0)-NVL(ar_deposit,0))*"
							+ Double.parseDouble(store.get("ar_accountrate").toString()) + "/" + Double.parseDouble(ctrate.toString())
							+ ",2)) from accountregister where ar_sourcetype ='费用比例报销单' and ar_sourceid=" + ct_id;
				}
			} else {
				thisamount = NumberUtil.formatDouble(
						Double.parseDouble(store.get("ar_payment").toString()) - Double.parseDouble(store.get("ar_deposit").toString()), 2);
				ytotal = baseDao.getSummaryByField("accountregister", "round(NVL(ar_payment,0)-NVL(ar_deposit,0),2)",
						"ar_sourcetype ='费用比例报销单' and ar_sourceid=" + ct_id + " and ar_id<>" + vId);
				if (NumberUtil.compare(Double.parseDouble(mptotal.toString()), (ytotal + thisamount), 2) == -1) {
					BaseUtil.showError("填写支出金额超过来源费用报销单金额！");
				}
				bool8 = true;
				amountsql = "select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype ='费用比例报销单' and ar_sourceid="
						+ ct_id;
			}
		}
		Object[] fpId = baseDao.getFieldsDataByCondition("accountregister", new String[] { "nvl(ar_sourceid,0)", "ar_sourcetype",
				"ar_accountcurrency" }, "ar_id=" + vId + " and ar_sourcetype in ('费用报销单','总务申请单','借款申请单','还款申请单','差旅费报销单')");
		int fp_id = 0;
		if (fpId != null) {
			Object mptotal = null;
			fp_id = Integer.parseInt(fpId[0].toString());
			bap = baseDao.getSummaryByField("BillAP", "bap_topaybalance", "bap_sourcetype='总务申请单' and bap_sourceid=" + fp_id);// 已转应付票据金额
			brc = baseDao.getSummaryByField("BillARChange", "brc_amount", "brc_sourcetype='总务申请单' and brc_sourceid=" + fp_id);// 已转应收票据异动金额
			if ("费用报销单".equals(fpId[1])) {
				mptotal = baseDao.getFieldDataByCondition("FeePlease", "nvl(fp_pleaseamount,0)-nvl(fp_n6,0)", "fp_id=" + fp_id);
			} else {
				mptotal = baseDao.getFieldDataByCondition("FeePlease", "nvl(fp_pleaseamount,0)", "fp_id=" + fp_id);
			}
			double ytotal = 0.0;
			double thisamount = 0.0;
			Object fp = baseDao.getFieldDataByCondition("FeePlease", "fp_v13", "fp_id=" + fp_id
					+ " and fp_kind in ('费用报销单','总务申请单','借款申请单','还款申请单','差旅费报销单')");
			if (fp != null && store.get("ar_accountcurrency") != null && !fp.equals(store.get("ar_accountcurrency"))) {
				Object fprate = baseDao.getFieldDataByCondition("CurrencysMonth", "nvl(cm_crrate,0)", "cm_crname='" + fp
						+ "' and cm_yearmonth=to_char(to_date('" + status[1] + "','yyyy-mm-dd hh24:mi:ss'), 'yyyymm')");
				if (fprate != null && Double.parseDouble(fprate.toString()) != 0) {
					thisamount = NumberUtil
							.formatDouble(
									(Double.parseDouble(store.get("ar_payment").toString()) + Double.parseDouble(store.get("ar_deposit")
											.toString()))
											* Double.parseDouble(store.get("ar_accountrate").toString())
											/ Double.parseDouble(fprate.toString()), 0);
					if ("费用报销单".equals(fpId[1])) {
						ytotal = baseDao.getSummaryByField(
								"accountregister",
								"round((NVL(ar_payment,0)-NVL(ar_deposit,0))*nvl(ar_accountrate,0)/"
										+ Double.parseDouble(fprate.toString()) + ",2)", "ar_sourcetype ='费用报销单' and ar_sourceid=" + fp_id
										+ " and ar_id<>" + vId);
						if (NumberUtil.compare(Double.parseDouble(mptotal.toString()), (ytotal + thisamount), 2) == -1) {
							BaseUtil.showError("填写支出金额超过来源费用报销单金额！");
						}
						bool1 = true;
						amountsql = "select sum(round((NVL(ar_payment,0)-NVL(ar_deposit,0))*"
								+ Double.parseDouble(store.get("ar_accountrate").toString()) + "/" + Double.parseDouble(fprate.toString())
								+ ",2)) from accountregister where ar_sourcetype ='费用报销单' and ar_sourceid=" + fp_id;
					} else if ("总务申请单".equals(fpId[1])) {
						ytotal = baseDao.getSummaryByField(
								"accountregister",
								"round((NVL(ar_payment,0)-NVL(ar_deposit,0))*nvl(ar_accountrate,0)/"
										+ Double.parseDouble(fprate.toString()) + ",2)", "ar_sourcetype ='总务申请单' and ar_sourceid=" + fp_id
										+ " and ar_id<>" + vId);
						if (Double.parseDouble(mptotal.toString()) < ytotal + bap + brc + thisamount) {
							BaseUtil.showError("填写支出金额超过来源总务申请单金额！");
						}
						bool2 = true;
						amountsql = "select sum(round((NVL(ar_payment,0)-NVL(ar_deposit,0))*"
								+ Double.parseDouble(store.get("ar_accountrate").toString()) + "/" + Double.parseDouble(fprate.toString())
								+ ",2)) from accountregister where ar_sourcetype ='总务申请单' and ar_sourceid=" + fp_id;
					} else if ("借款申请单".equals(fpId[1])) {
						ytotal = baseDao.getSummaryByField(
								"accountregister",
								"round((NVL(ar_payment,0)-NVL(ar_deposit,0))*nvl(ar_accountrate,0)/"
										+ Double.parseDouble(fprate.toString()) + ",2)", "ar_sourcetype ='借款申请单' and ar_sourceid=" + fp_id
										+ " and ar_id<>" + vId);
						if (Double.parseDouble(mptotal.toString()) < ytotal + thisamount) {
							BaseUtil.showError("填写支出金额超过来源借款申请单金额！");
						}
						bool4 = true;
						amountsql = "select sum(round((NVL(ar_payment,0)-NVL(ar_deposit,0))*"
								+ Double.parseDouble(store.get("ar_accountrate").toString()) + "/" + Double.parseDouble(fprate.toString())
								+ ",2)) from accountregister where ar_sourcetype ='借款申请单' and ar_sourceid=" + fp_id;
					} else if ("还款申请单".equals(fpId[1])) {
						ytotal = baseDao.getSummaryByField(
								"accountregister",
								"round((NVL(ar_payment,0)-NVL(ar_deposit,0))*nvl(ar_accountrate,0)/"
										+ Double.parseDouble(fprate.toString()) + ",2)", "ar_sourcetype ='还款申请单' and ar_sourceid=" + fp_id
										+ " and ar_id<>" + vId);
						if (Double.parseDouble(mptotal.toString()) < ytotal + thisamount) {
							BaseUtil.showError("填写收入金额超过来源还款申请单金额！");
						}
						bool5 = true;
						amountsql = "select sum(round((NVL(ar_deposit,0)-NVL(ar_payment,0))*"
								+ Double.parseDouble(store.get("ar_accountrate").toString()) + "/" + Double.parseDouble(fprate.toString())
								+ ",2)) from accountregister where ar_sourcetype ='还款申请单' and ar_sourceid=" + fp_id;
					} else if ("差旅费报销单".equals(fpId[1])) {
						ytotal = baseDao.getSummaryByField(
								"accountregister",
								"round((NVL(ar_payment,0)-NVL(ar_deposit,0))*nvl(ar_accountrate,0)/"
										+ Double.parseDouble(fprate.toString()) + ",2)", "ar_sourcetype ='差旅费报销单' and ar_sourceid=" + fp_id
										+ " and ar_id<>" + vId);
						if (Double.parseDouble(mptotal.toString()) < ytotal + thisamount) {
							BaseUtil.showError("填写支出金额超过来源差旅费报销单金额！");
						}
						bool7 = true;
						amountsql = "select sum(round((NVL(ar_payment,0)-NVL(ar_deposit,0))*"
								+ Double.parseDouble(store.get("ar_accountrate").toString()) + "/" + Double.parseDouble(fprate.toString())
								+ ",2)) from accountregister where ar_sourcetype ='差旅费报销单' and ar_sourceid=" + fp_id;
					}
				}
			} else {
				thisamount = NumberUtil.formatDouble(
						Double.parseDouble(store.get("ar_payment").toString()) - Double.parseDouble(store.get("ar_deposit").toString()), 2);
				if ("费用报销单".equals(fpId[1])) {
					ytotal = baseDao.getSummaryByField("accountregister", "round(NVL(ar_payment,0)-NVL(ar_deposit,0),2)",
							"ar_sourcetype ='费用报销单' and ar_sourceid=" + fp_id + " and ar_id<>" + vId);
					if (NumberUtil.compare(Double.parseDouble(mptotal.toString()), (ytotal + thisamount), 2) == -1) {
						BaseUtil.showError("填写支出金额超过来源费用报销单金额！");
					}
					bool1 = true;
					amountsql = "select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype ='费用报销单' and ar_sourceid="
							+ fp_id;
				} else if ("总务申请单".equals(fpId[1])) {
					ytotal = baseDao.getSummaryByField("accountregister", "round(NVL(ar_payment,0)-NVL(ar_deposit,0),2)",
							"ar_sourcetype ='总务申请单' and ar_sourceid=" + fp_id + " and ar_id<>" + vId);
					if (NumberUtil.compare(Double.parseDouble(mptotal.toString()), (ytotal + bap + brc + thisamount), 2) == -1) {
						BaseUtil.showError("填写支出金额超过来源总务申请单金额！");
					}
					bool2 = true;
					amountsql = "select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype ='总务申请单' and ar_sourceid="
							+ fp_id;
				} else if ("借款申请单".equals(fpId[1])) {
					ytotal = baseDao.getSummaryByField("accountregister", "round(NVL(ar_payment,0)-NVL(ar_deposit,0),2)",
							"ar_sourcetype ='借款申请单' and ar_sourceid=" + fp_id + " and ar_id<>" + vId);
					if (NumberUtil.compare(Double.parseDouble(mptotal.toString()), (ytotal + thisamount), 2) == -1) {
						BaseUtil.showError("填写支出金额超过来源借款申请单金额！");
					}
					bool4 = true;
					amountsql = "select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype ='借款申请单' and ar_sourceid="
							+ fp_id;
				} else if ("还款申请单".equals(fpId[1])) {
					thisamount = NumberUtil
							.formatDouble(
									Double.parseDouble(store.get("ar_deposit").toString())
											- Double.parseDouble(store.get("ar_payment").toString()), 2);
					ytotal = baseDao.getSummaryByField("accountregister", "round(NVL(ar_deposit,0)-NVL(ar_payment,0),2)",
							"ar_sourcetype ='还款申请单' and ar_sourceid=" + fp_id + " and ar_id<>" + vId);
					if (NumberUtil.compare(Double.parseDouble(mptotal.toString()), (ytotal + thisamount), 2) == -1) {
						BaseUtil.showError("填写收入金额超过来源还款申请单金额！");
					}
					bool5 = true;
					amountsql = "select sum(NVL(ar_deposit,0)-NVL(ar_payment,0)) from accountregister where ar_sourcetype ='还款申请单' and ar_sourceid="
							+ fp_id;
				} else if ("差旅费报销单".equals(fpId[1])) {
					ytotal = baseDao.getSummaryByField("accountregister", "round(NVL(ar_payment,0)-NVL(ar_deposit,0),2)",
							"ar_sourcetype ='差旅费报销单' and ar_sourceid=" + fp_id + " and ar_id<>" + vId);
					if (NumberUtil.compare(Double.parseDouble(mptotal.toString()), (ytotal + thisamount), 2) == -1) {
						BaseUtil.showError("填写支出金额超过来源差旅费报销单金额！");
					}
					bool7 = true;
					amountsql = "select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype ='差旅费报销单' and ar_sourceid="
							+ fp_id;
				}
			}
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AccountRegister", "ar_id");
		baseDao.execute(formSql);
		if (bool1) {
			baseDao.execute("update FeePlease set fp_n1=nvl((" + amountsql + "),0) where fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_pleaseamount,0)=nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_pleaseamount,0)>nvl(fp_n1,0)+nvl(fp_n6,0) and fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)+nvl(fp_n6,0)=0 and fp_id=" + fp_id);
		}
		if (bool2) {
			baseDao.execute("update FeePlease set fp_n1=nvl((" + amountsql + "),0)+" + (+bap + brc) + " where fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_n1,0)>0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)=0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_id="
					+ fp_id);
		}
		if (bool4) {
			baseDao.execute("update FeePlease set fp_n1=(" + amountsql + ") where fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_n1,0)>0 and nvl(fp_pleaseamount,0)=nvl(fp_n1,0) and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_n1,0)>0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)=0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_id="
					+ fp_id);
		}
		if (bool5) {
			baseDao.execute("update FeePlease set fp_n1=(" + amountsql + ") where fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_n1,0)>0 and nvl(fp_pleaseamount,0)=nvl(fp_n1,0) and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_n1,0)>0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)=0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_id="
					+ fp_id);
		}
		if (bool3) {
			baseDao.execute("update MOULDFEEPLEASE set mp_payamount=(select sum(NVL(ar_payment,0)-NVL(ar_deposit,0)) from accountregister where ar_sourcetype ='模具付款申请' and nvl(ar_sourceid,0)="
					+ sourceId + ") where mp_id=" + sourceId);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='PAYMENTED',mp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED")
					+ "' where nvl(mp_payamount,0)=nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + sourceId);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='PARTPAYMENT',mp_paystatus='"
					+ BaseUtil.getLocalMessage("PARTPAYMENT")
					+ "' where nvl(mp_payamount,0)<nvl(mp_total,0) and nvl(mp_payamount,0)>0 and mp_id=" + sourceId);
			baseDao.execute("update MOULDFEEPLEASE set mp_paystatuscode='UNPAYMENT',mp_paystatus='" + BaseUtil.getLocalMessage("UNPAYMENT")
					+ "' where nvl(mp_payamount,0)=0 and mp_id=" + sourceId);
		}
		if (bool7) {
			baseDao.execute("update FeePlease set fp_n1=(" + amountsql + ") where fp_id=" + fp_id);
			baseDao.execute("update FeePlease set fp_v7='已支付' where nvl(fp_n1,0)>0 and nvl(fp_pleaseamount,0)=nvl(fp_n1,0) and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='部分支付' where nvl(fp_n1,0)>0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_id="
					+ fp_id);
			baseDao.execute("update FeePlease set fp_v7='未支付' where nvl(fp_n1,0)=0 and nvl(fp_pleaseamount,0)>nvl(fp_n1,0) and fp_id="
					+ fp_id);
		}
		if (bool8) {
			baseDao.execute("update CUSTOMTABLE set ct_number_2=nvl((" + amountsql + "),0) where ct_id=" + ct_id);
			baseDao.execute("update CUSTOMTABLE set ct_varchar50_13='已支付' where nvl(ct_amount,0)=nvl(ct_number_2,0)+nvl(ct_number_3,0) and ct_id="
					+ ct_id);
			baseDao.execute("update CUSTOMTABLE set ct_varchar50_13='部分支付' where nvl(ct_amount,0)>nvl(ct_number_2,0)+nvl(ct_number_3,0) and ct_id="
					+ ct_id);
			baseDao.execute("update CUSTOMTABLE set ct_varchar50_13='未支付' where nvl(ct_number_2,0)+nvl(ct_number_3,0)=0 and ct_id=" + ct_id);
		}
		// 更新冲账汇率
		baseDao.execute("update Accountregister set ar_araprate=1 where ar_id=? and ar_arapcurrency=ar_accountcurrency", vId);
		List<Map<Object, Object>> assMain = BaseUtil.parseGridStoreToMaps(gridStore[2]);
		if (assMain != null) {
			for (Map<Object, Object> am : assMain) {
				if (am.get("ass_id") == null || am.get("ass_id").equals("null") || Integer.parseInt(am.get("ass_id").toString()) == 0) {
					am.put("ass_id", baseDao.getSeqId("ACCOUNTREGISTERASS_SEQ"));
					baseDao.execute(SqlUtil.getInsertSqlByMap(am, "AccountRegisterAss"));
				} else {
					baseDao.execute(SqlUtil.getUpdateSqlByFormStore(am, "AccountRegisterAss", "ass_id"));
				}
			}
		}
		// 保存AccountRegisterDetail
		List<String> gridSql = null;
		if (grid.size() > 0) {
			gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, "AccountRegisterDetail", "ard_id");
			String err_out = "";
			int count = grid.size();
			for (Map<Object, Object> s : grid) {
				String cate_code = s.get("ard_catecode").toString(); // 科目编码
				String ard_detno = s.get("ard_detno").toString();
				if (!cate_code.trim().equals("")) {
					Object o = baseDao.getFieldDataByCondition("CATEGORY", "ca_isleaf", "ca_code='" + cate_code + "'");
					String isleaf = o == null ? "0" : o.toString();
					if (isleaf.equals("0")) {
						// 科目不是子节点 报错
						err_out = err_out + ard_detno;
						if (grid.indexOf(s) == count - 1) {
						} else {
							err_out = err_out + ",";
						}
					}
				}
			}
			if (!err_out.equals("")) {
				BaseUtil.showError("第" + err_out + "条明细行科目不是末级科目,不能更新明细行!");
			}
			gridSql.add(0, "update AccountRegisterDetail set ard_detno=-ard_detno where ard_arid=" + vId);
			List<Map<Object, Object>> assgrid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
			Map<Object, List<Map<Object, Object>>> list = BaseUtil.groupMap(assgrid, "ars_ardid");
			for (Map<Object, Object> s : grid) {
				if (s.get("ard_id") == null || s.get("ard_id").equals("") || s.get("ard_id").equals("0")
						|| Integer.parseInt(s.get("ard_id").toString()) <= 0) {
					int id = baseDao.getSeqId("ACCOUNTREGISTERDETAIL_SEQ");
					assgrid = list.get(String.valueOf(s.get("ard_id")));
					if (assgrid != null) {
						for (Map<Object, Object> m : assgrid) {// AccountRegisterDetailAss
							m.put("ars_ardid", id);
							m.put("ars_type", caller);
						}
						// 保存AccountRegisterDetailAss
						List<String> sqls = SqlUtil.getInsertSqlbyList(assgrid, "AccountRegisterDetailAss", "ars_id");
						baseDao.execute(sqls);
					}
					s.put("ard_id", id);
					gridSql.add(SqlUtil.getInsertSqlByMap(s, "AccountRegisterDetail"));
				} else {
					// 科目有修改的情况下，先删除之前科目的辅助核算
					gridSql.add("delete from AccountRegisterDetailAss where ars_ardid="
							+ s.get("ard_id")
							+ " and instr(nvl((select ca_assname from category left join AccountRegisterDetail on ca_code=ard_catecode where ard_id=ars_ardid and ca_assname is not null),' '), ars_asstype) = 0");
				}
			}
			for (Object key : list.keySet()) {
				Integer id = Integer.parseInt(String.valueOf(key));
				if (id > 0) {
					assgrid = list.get(key);
					if (assgrid != null) {
						for (Map<Object, Object> map : assgrid) {
							// 科目修改的情况下，辅助核算类型可能一样
							if (!StringUtil.hasText(map.get("ars_id")) || Integer.parseInt(String.valueOf(map.get("ars_id"))) <= 0) {
								gridSql.add("delete from AccountRegisterDetailAss where ars_ardid=" + map.get("ars_ardid")
										+ " and ars_asstype='" + map.get("ars_asstype") + "'");
							}
						}
						List<String> sqls = SqlUtil.getInsertOrUpdateSqlbyGridStore(assgrid, "AccountRegisterDetailAss", "ars_id");
						gridSql.addAll(sqls);
					}
				}
			}
			gridSql.add("update AccountRegisterDetail set ard_detno=abs(ard_detno) where ard_arid=" + vId);
			baseDao.execute(gridSql);
		} else {
			grid = BaseUtil.parseGridStoreToMaps(gridStore[1]);
			gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "AccountRegisterDetailAss", "ars_id");
			// 保存AccountRegisterDetailAss
			baseDao.execute(gridSql);
		}
		String insertAssDetSql = "insert into accountregisterdetailass(ars_id,ars_ardid,ars_detno,ars_asstype,ars_asscode,ars_assname,ars_type) values (?,?,?,?,?,?,'AccountRegister!Bank')";
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select * from accountregister left join accountregisterdetail on ard_arid=ar_id where ard_arid=? and nvl(ard_catecode,' ')<>' '",
						store.get("ar_id"));
		while (rs.next()) {
			Object catecode = rs.getObject("ard_catecode");
			int ardid = rs.getInt("ard_id");
			SqlRowList ass = baseDao.queryForRowSet("select ca_assname from category where ca_code=? and nvl(ca_assname,' ')<>' '",
					catecode);
			if (ass.next()) {
				String assStr = ass.getString("ca_assname");
				String[] codes = assStr.split("#");
				for (String assname : codes) {
					int i = baseDao.getCount("select count(1) from accountregisterdetailass where ars_ardid=" + ardid
							+ " and ARS_ASSTYPE='" + assname + "'");
					if (i == 0) {
						Object maxno = baseDao.getFieldDataByCondition("accountregisterdetailass", "max(nvl(ars_detno,0))", "ars_ardid="
								+ ardid);
						maxno = maxno == null ? 0 : maxno;
						int detno = Integer.parseInt(maxno.toString()) + 1;
						int arsid = baseDao.getSeqId("ACCOUNTREGISTERDETAILASS_SEQ");
						baseDao.execute(insertAssDetSql, new Object[] { arsid, ardid, detno, assname, null, null });
					}
					int arsid = baseDao.getFieldValue("accountregisterdetailass", "ars_id", "ars_ardid=" + ardid + " and ARS_ASSTYPE='"
							+ assname + "'", Integer.class);
					if ("部门".equals(assname) && StringUtil.hasText(rs.getObject("ar_departmentcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_departmentcode")
								+ "', ars_assname='" + rs.getObject("ar_departmentname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
					if ("项目".equals(assname) && StringUtil.hasText(rs.getObject("ar_prjcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_prjcode")
								+ "', ars_assname='" + rs.getObject("ar_prjname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
					if ("客户往来".equals(assname) && StringUtil.hasText(rs.getObject("ar_custcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_custcode")
								+ "', ars_assname='" + rs.getObject("ar_custname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
					if ("供应商往来".equals(assname) && StringUtil.hasText(rs.getObject("ar_vendcode"))) {
						baseDao.execute("update accountregisterdetailass set ars_asscode='" + rs.getObject("ar_vendcode")
								+ "', ars_assname='" + rs.getObject("ar_vendname") + "' where ars_id=" + arsid
								+ " and nvl(ars_asscode,' ')=' '");
					}
				}
			}
		}
		if (bool6 && pp_code != null) {
			rePayPlease(pp_code, ppdId, type);
		}
		updateErrorString(Integer.parseInt((String) store.get("ar_id")));
		Double sum = baseDao.getSummaryByField("AccountRegisterDetail", "ard_debit", "ard_arid=" + store.get("ar_id"));
		if ("转存".equals(type)) {
			baseDao.execute("update AccountRegister set ar_prerate=round((ar_preamount+" + sum + ")/ar_payment,15) where ar_id="
					+ store.get("ar_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "ar_id", store.get("ar_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	static final String trim_ass = "delete from accountregisterdetailass where ars_id in (select ars_id from AccountregisterDetail left join accountregisterdetailass on ars_ardid=ard_id left join category on ca_code=ard_catecode where ard_arid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ars_asstype)=0)";
	static final String del_ass = "delete from accountregisterdetailass where ars_ardid in (select ard_id from Accountregister left join AccountregisterDetail on ard_arid=ar_id left join category on ca_code=ard_catecode where ar_id=? and nvl(ca_asstype,' ')=' ')";

	private void check(int ar_id) {
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_code) from Accountregister where ar_arapcurrency=ar_accountcurrency and nvl(ar_araprate,0)<>1 and ar_id=? and ar_type not in ('费用','转存','自动转存','应收票据收款','应付票据付款','其它付款','其它收款','暂收款','预付款','保理收款','保理付款')",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("币别一致，冲账汇率不为1，不允许进行当前操作!");
		}
		String currency = baseDao.getDBSetting("defaultCurrency");
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select WM_CONCAT(ar_accountcurrency) from AccountRegister where ar_id=? and nvl(ar_accountrate,0)=1 and nvl(ar_accountcurrency,' ')<>'"
						+ currency + "'", String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("账户币别非本位币账户汇率不能为1！");
		}
		// 清除无效核算
		baseDao.execute(
				"delete from accountregisterass where ASS_ID in (select ASS_ID from accountregister left join accountregisterass on ASS_CONID=ar_id left join category on ca_code=ar_accountcode where ar_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0)",
				ar_id);
		baseDao.execute(
				"delete from accountregisterass where ASS_CONID in (select ar_id from accountregister left join category on ca_code=ar_accountcode where ar_id=? and nvl(ca_asstype,' ')=' ')",
				ar_id);
		baseDao.execute(trim_ass, ar_id);
		baseDao.execute(del_ass, ar_id);
		// 辅助核算不完善
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ar_code) from accountregister left join accountregisterass on ASS_CONID=ar_id left join category on ca_code=ar_accountcode where ar_id=? and nvl(ca_assname,' ')<>' ' and (nvl(ASS_ASSTYPE,' ')=' ' or nvl(ASS_CODEFIELD,' ')=' ' or nvl(ASS_NAMEFIELD,' ')=' ') order by ar_id",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算不完善，不允许进行当前操作!");
		}
		// 核算项重复
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ar_code) from (select count(1) c,ar_code,ASS_ASSTYPE from accountregister left join accountregisterass on ASS_CONID=ar_id where ar_id=? and nvl(ASS_ASSTYPE,' ')<>' ' group by ar_code,ASS_ASSTYPE) where c>1 order by ar_code",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("主表辅助核算核算项重复，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ard_detno) from (select ard_detno,nvl(regexp_count(ca_assname,'#'),-1)+1 expected_rows,(select count(1) from accountregisterdetailass where ars_ardid=ard_id) actual_rows from accountregisterdetail left join category on ca_code=ard_catecode where ard_arid=? and nvl(ca_assname,' ')<>' ') where expected_rows<>actual_rows",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("明细行核算项不全，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ard_detno) from accountregisterdetail left join accountregisterdetailass on ARS_ARDID=ard_id left join category on ca_code=ard_catecode where ard_arid=? and nvl(ca_assname,' ')<>' ' and (nvl(ARS_ASSTYPE,' ')=' ' or nvl(ARS_ASSNAME,' ')=' ' or nvl(ARS_ASSCODE,' ')=' ') order by ard_detno",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算不完善，不允许进行当前操作!行号：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ard_detno) from (select count(1) c,ard_detno,ARS_ASSTYPE from accountregisterdetail left join accountregisterdetailass on ARS_ARDID=ard_id where ard_arid=? and nvl(ARS_ASSTYPE,' ')<>' ' group by ard_detno,ARS_ASSTYPE) where c>1 order by ard_detno",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("明细行辅助核算核算项重复，不允许进行当前操作!行号：" + dets);
		}
		// 核算项错误
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ar_code) from accountregister left join accountregisterass on ASS_CONID=ar_id left join category on ca_code=ar_accountcode where ar_id=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ASS_ASSNAME)=0 order by ar_code",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("主表核算项错误，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wmsys.wm_concat(ard_detno) from accountregisterdetail left join accountregisterdetailass on ARS_ARDID=ard_id left join category on ca_code=ard_catecode where ard_arid=? and nvl(ca_assname,' ')<>' ' and instr(ca_assname,ARS_ASSTYPE)=0 order by ard_detno",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("明细行核算项错误，不允许进行当前操作!行号：" + dets);
		}
		// 核算项不存在
		String str = "";
		StringBuffer error = new StringBuffer();
		SqlRowList rs1 = baseDao
				.queryForRowSet(
						"select 'select '||ard_detno||',count(1) from '||ak_table||' where '||ak_asscode||'='''||ARS_ASSCODE||''' and '||AK_ASSNAME||'='''||ARS_ASSNAME||'''' from accountregisterdetailass left join asskind on ARS_ASSTYPE=ak_name left join accountregisterdetail on ARS_ARDID=ard_id where ard_arid=? order by ard_detno",
						ar_id);
		while (rs1.next()) {
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(2) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0)
			error.append("核算编号+核算名称不存在,行:").append(str).append(";");
		BaseUtil.showError(error.toString());
		rs1 = baseDao
				.queryForRowSet(
						"select 'select count(1) from '||ak_table||' where '||ak_asscode||'='''||ASS_CODEFIELD||''' and '||AK_ASSNAME||'='''||ASS_NAMEFIELD||'''' from accountregisterass left join asskind on ASS_ASSNAME=ak_name left join accountregister on ASS_CONID=ar_id where ar_id=? order by ar_code",
						ar_id);
		if (rs1.next()) {
			str = "";
			SqlRowList rd = baseDao.queryForRowSet(rs1.getString(1));
			if (rd.next() && rd.getInt(1) == 0) {
				if (StringUtil.hasText(str))
					str = str + ",";
				str += rd.getInt(1);
			}
		}
		if (str.length() > 0)
			BaseUtil.showError("主表核算编号+核算名称不存在，不允许进行当前操作!");
		// @add wuyx 2018/01/09 类型为商城采购订单时 限制
		if (baseDao.checkIf("accountregisterdetail left join purchase on ard_ordercode = pu_code",
				"upper(nvl(pu_ordertype,' ')) = 'B2C' and ard_arid = " + ar_id)) {
			// 金额与商城一致
			dets = baseDao
					.queryForObject(
							" select nvl(( select sum(b2cpu_price) pricetotal "
									+ " from b2C$purchaseorder left join purchase on B2CPU_ORDERID = pu_pocode left join ACCOUNTREGISTERDETAIL on ard_ordercode = pu_code "
									+ " where ard_arid=?),0) - nvl(ardtotal,0) from ( select sum(ard_nowbalance) ardtotal from ACCOUNTREGISTERDETAIL  where  ard_arid=?)",
							String.class, ar_id, ar_id);
			if (dets != null && Double.valueOf(dets) > 0) {
				BaseUtil.showError("当前单据付款金额小于商城采购单应付金额（或因运费未支出）! 差异额：" + dets);
			}
			// 必须一次性付清账款
			dets = baseDao.queryForObject(" select distinct sum(nvl(ard_nowbalance,0)) OVER (PARTITION BY 1) - nvl(ar_payment,0) "
					+ " from ACCOUNTREGISTERDETAIL left join AccountRegister on ard_arid = ar_id  " + " where  ard_arid=?", String.class,
					ar_id);
			if (dets != null && !dets.equals("0")) {
				BaseUtil.showError("含商城类采购单的银行登记需一次性付清账款! 差异额：" + dets);
			}
		}
	}

	@Override
	public void submitAccountRegister(int ar_id, String caller) {
		Object[] date = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_date" }, "ar_id=" + ar_id);
		baseDao.checkCloseMonth("MONTH-B", date[0]);
		checkcmrate(ar_id);
		checkVoucher(ar_id);
		check(ar_id);
		SqlRowList rs = baseDao.queryForRowSet("select * from AccountRegister where ar_id=?", ar_id);
		if (rs.next()) {
			// 只能对状态为[在录入]的订单进行提交操作!
			StateAssert.submitOnlyEntering(rs.getString("ar_statuscode"));
			// 执行提交前的其它逻辑
			handlerService.handler(caller, "commit", "before", new Object[] { ar_id });
			// 执行提交操作
			baseDao.submit("AccountRegister", "ar_id=" + ar_id, "ar_status", "ar_statuscode");
			baseDao.execute(
					"update Category set ca_nowbalance2=NVL((select sum(nvl(ar_deposit,0)-nvl(ar_payment,0)) from AccountRegister where ar_accountcode=ca_code and ar_statuscode='COMMITED'),0) where ca_code=?",
					rs.getString("ar_accountcode"));
			// 记录操作
			baseDao.logger.submit(caller, "ar_id", ar_id);
			// 执行提交后的其它逻辑
			handlerService.handler(caller, "commit", "after", new Object[] { ar_id });
		}
	}

	/**
	 * 在提交时 更新errorString 字段
	 * 
	 * @param id
	 */
	public void updateErrorString(int id) {
		// 多辅助核算项的核算明细按照科目编号中辅助核算名称顺序重排
		SqlRowList detailList = baseDao
				.queryForRowSet(
						"select ard_id,ca_assname from accountregisterdetail left join category on ard_catecode=ca_code where ard_arid=? and ca_assname is not null and instr(ca_assname,'#')>0",
						id);
		while (detailList.next()) {
			String[] assNames = detailList.getString("ca_assname").split("#");
			int index = 1;
			for (String assName : assNames) {
				baseDao.execute("update accountregisterdetailass set ars_detno=? where ars_ardid=? and ars_asstype=? and ars_detno<>?",
						index, detailList.getObject("ard_id"), assName, index);
				index++;
			}
		}
		// ar_accountrate 账户币别 ar_payment 支出金额 ar_deposit 收入金额
		Object[] ar_type_str = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_type", "ar_accountrate",
				"ar_payment", "ar_deposit" }, "ar_id=" + id);
		baseDao.updateByCondition("AccountRegister", "ar_errstring = null", "ar_id=" + id);
		// 单据类型为 费用 或者其它收款 或者其它付款
		if (ar_type_str[0] != null && (ar_type_str[0].equals("费用") || ar_type_str[0].equals("其它收款") || ar_type_str[0].equals("其它付款"))) {
			if ((Double.parseDouble((ar_type_str[2] == null ? '0' : ar_type_str[2]).toString()) != 0 && Double
					.parseDouble((ar_type_str[3] == null ? '0' : ar_type_str[3]).toString()) == 0)
					|| (Double.parseDouble((ar_type_str[2] == null ? '0' : ar_type_str[2]).toString()) == 0 && Double
							.parseDouble((ar_type_str[3] == null ? '0' : ar_type_str[3]).toString()) != 0)) {
				// ard_debit 借方
				// ard_credit 贷方
				Object[] ar_amount = baseDao.getFieldsDataByCondition("AccountRegisterDetail", new String[] { "sum(ard_debit)",
						"sum(ard_credit)" }, "ard_arid = " + id);
				double ar_accountrate = NumberUtil.formatDouble((ar_type_str[1] == null ? '0' : ar_type_str[1]).toString(), 9); // 汇率
				// 支出金额 = 借方 - 贷方
				double ar_deposit = NumberUtil.formatDouble((ar_type_str[3] == null ? '0' : ar_type_str[3]).toString(), 3); // 收入金额
				double ar_payment = NumberUtil.formatDouble((ar_type_str[2] == null ? '0' : ar_type_str[2]).toString(), 3); // 支出金额
				double sum_debit = NumberUtil.formatDouble((ar_amount[0] == null ? '0' : ar_amount[0]).toString(), 3); // 借方
				double sum_credit = NumberUtil.formatDouble((ar_amount[1] == null ? '0' : ar_amount[1]).toString(), 3); // 贷方
				double sub = NumberUtil.formatDouble(
						(NumberUtil.formatDouble(ar_deposit * ar_accountrate, 3) + sum_debit)
								- (NumberUtil.formatDouble(ar_payment * ar_accountrate, 3) + sum_credit), 2);
				double bb = NumberUtil.formatDouble((ar_deposit + ar_payment) * ar_accountrate, 2);
				// 平衡
				if (Math.abs(sub) > 0.01) {// 不平衡
					baseDao.updateByCondition("AccountRegister", "ar_errstring = '不平衡(" + Math.abs(NumberUtil.formatDouble(sub, 2))
							+ "),本币(" + bb + ")'", "ar_id=" + id);
				} else { // 不平衡
					baseDao.updateByCondition("AccountRegister", "ar_errstring = null", "ar_id=" + id);
				}
			}
		} else {
			return;
		}
	}

	@Override
	public void resSubmitAccountRegister(int ar_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_statuscode", "ar_accountcode" }, "ar_id="
				+ ar_id);
		StateAssert.resSubmitOnlyCommited(status[0]);
		checkVoucher(ar_id);
		Object[] date = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_date" }, "ar_id=" + ar_id);
		baseDao.checkCloseMonth("MONTH-B", date[0]);
		handlerService.beforeResSubmit(caller, ar_id);
		// 执行反提交操作
		baseDao.resOperate("AccountRegister", "ar_id=" + ar_id, "ar_status", "ar_statuscode");
		baseDao.execute("update Category set ca_nowbalance2=NVL((select sum(nvl(ar_deposit,0)-nvl(ar_payment,0)) from AccountRegister where ar_accountcode='"
				+ status[1] + "' and ar_statuscode='COMMITED'),0) where ca_code='" + status[1] + "'");
		// 记录操作
		baseDao.logger.resSubmit(caller, "ar_id", ar_id);
		handlerService.afterResSubmit(caller, ar_id);
	}

	/**
	 * 删除明细时，重新计算明细状态
	 */
	public String validAccountRegister(int id) {
		updateErrorString(id);
		Object str = baseDao.getFieldDataByCondition("AccountRegister", "ar_errstring", "ar_id=" + id);
		if (str == null)
			return null;
		return String.valueOf(str);
	}

	@Override
	public List<JSONTree> getJsonTrees(int parentid, String masterName) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		boolean isGroup = false;
		Master master = SystemSession.getUser().getCurrentMaster();
		/*
		 * if ("true".equals(BaseUtil.getXmlSetting("group"))) { if (master !=
		 * null && (master.getMa_type() == 0 || master.getMa_type() == 2)) {
		 * isGroup = true; } }
		 */
		if (isGroup) {
			if (parentid == 0 && masterName == null) {
				SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
				tree = enterpriseDao.getMastersTree(master.getMa_id());
			} else {
				SpObserver.putSp(masterName);
				tree = parseCategory(categoryStrDao.getCategoryBank(parentid), masterName);
			}
			SpObserver.putSp(SystemSession.getUser().getEm_master());
		} else {
			tree = parseCategory(categoryStrDao.getCategoryBank(parentid), master.getMa_name());
		}
		return tree;
	}

	private List<JSONTree> parseCategory(List<Category> list, String masterName) {
		List<JSONTree> tree = new ArrayList<JSONTree>();
		for (Category navigation : list) {
			tree.add(new JSONTree(navigation, masterName));
		}
		return tree;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void accountedAccountRegister(int ar_id, String caller) {
		baseDao.execute("update AccountRegister set ar_errstring=null where ar_id=" + ar_id + " and nvl(ar_errstring,' ')='正常'");
		baseDao.execute("update AccountRegisterDetail set ard_currency=(select ar_arapcurrency from AccountRegister where ard_arid=ar_id)"
				+ " where nvl(ard_currency,' ')=' ' and ard_arid=" + ar_id
				+ " and exists (select * from AccountRegister where ar_id=ard_arid"
				+ " and ar_sourcetype='付款申请' and ar_type in ('应付款','预付款') and ar_id=" + ar_id + ")");
		baseDao.execute("update accountregisterdetail set ard_rate=(select cm_crrate from CurrencysMonth where cm_crname=ard_currency "
				+ " and CM_YEARMONTH=to_char(sysdate,'yyyymm')) where ard_arid=" + ar_id
				+ " and nvl(ard_rate,0)=0 and nvl(ard_currency,' ')<>' '");
		baseDao.execute("update AccountRegister set ar_araprate=round(nvl(ar_aramount,0)/(nvl(ar_payment,0)+nvl(ar_deposit,0)),15) where (nvl(ar_payment,0)+nvl(ar_deposit,0))<>0 and ar_id="
				+ ar_id);
		// 只能对状态为[未记账]的订单进行操作!
		Object[] status = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_statuscode", "ar_code", "ar_type",
				"ar_source", "ar_accountcode", "to_char(ar_date,'yyyymm')", "nvl(ar_preamount,0)", "round(ar_payment*ar_prerate,2)",
				"ar_aacode" }, "ar_id=" + ar_id);
		if (!status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.account_onlyCommited"));
		}
		if (status[2] == null) {
			BaseUtil.showError("请先选择类型！");
		}
		checkcmrate(ar_id);
		checkVoucher(ar_id);
		Object[] date = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_date" }, "ar_id=" + ar_id);
		baseDao.checkCloseMonth("MONTH-B", date[0]);
		check(ar_id);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ar_code) from Accountregister where ar_arapcurrency=ar_accountcurrency and nvl(ar_araprate,0)<>1 and ar_id=? and ar_type ='预付款'",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("币别一致，冲账汇率不为1，不允许进行当前操作!");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(ard_detno) from AccountRegisterDetail left join Category on ard_catecode=ca_code where ard_arid=? and nvl(ca_currencytype,0)=1 and nvl(ard_doubledebit,0)=0 and nvl(ard_doublecredit,0)=0",
						String.class, ar_id);
		if (dets != null) {
			BaseUtil.showError("行" + dets + "的科目设置了外币核算，原币金额不能为0！");
		}
		Employee employee = SystemSession.getUser();
		// 执行记账操作
		if ("应付款".equals(status[2].toString())) {
			SqlRowList billcode = baseDao.queryForRowSet(
					"select ard_ordercode from AccountRegisterDetail where ard_arid=? and nvl(ard_ordercode,' ')<>' '", ar_id);
			while (billcode.next()) {
				baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("ard_ordercode") });
			}
		}
		// 执行记账前的其它逻辑
		handlerService.handler(caller, "account", "before", new Object[] { ar_id });
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITEREGISTER", new Object[] { status[1], String.valueOf(employee.getEm_id()) });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		if (status[2] != null && "转存".equals(status[2].toString())) {
			Object code = baseDao.getFieldDataByCondition("AccountRegister", "ar_code", "ar_type='自动转存' and ar_source='" + status[1] + "'");
			res = baseDao.callProcedure("SP_COMMITEREGISTER", new Object[] { code, String.valueOf(employee.getEm_id()) });
			if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
				BaseUtil.showError(res);
			}
		}
		baseDao.execute("update Category set ca_nowbalance2=NVL((select sum(nvl(ar_deposit,0)-nvl(ar_payment,0)) from AccountRegister where ar_accountcode='"
				+ status[4] + "' and ar_statuscode='COMMITED'),0) where ca_code='" + status[4] + "'");
		baseDao.execute("update AccountRegister set AR_AUDITMAN='" + employee.getEm_name() + "',AR_AUDITDATE=sysdate where ar_id=" + ar_id);
		// 生成单据成功之后，提示相应的单号
		dataLink(ar_id, status[2]);
		// 单据过账时如果过账成功调用一下当前单据凭证制作的方法，制作成功后弹出凭证界面。
		if (baseDao.isDBSetting(caller, "autoCreateVoucher")) {
			if (status[2] != null && !status[2].equals("应收款") && !status[2].equals("应收退款") && !status[2].equals("预收款")
					&& !status[2].equals("预收退款单") && !status[2].equals("应付款") && !status[2].equals("应付退款") && !status[2].equals("预付款")
					&& !status[2].equals("预付退款")) {
				String returnstr = baseDao.callProcedure("FA_VOUCHERCREATE",
						new Object[] { status[5], "AccountRegiste", "'" + status[1].toString() + "'", "single", status[2].toString(), "CB",
								employee.getEm_id(), employee.getEm_name() });
				if (returnstr != null && !returnstr.equals("")) {
					BaseUtil.showError(returnstr);
				}
			}
		}
		boolean bool = baseDao.checkIf("user_tab_columns", "table_name='BILLARCHEQUE'");
		if (bool) {
			if (status[2] != null && (status[2].equals("应收款") || status[2].equals("预收款"))) {
				baseDao.execute("UPDATE BILLARCHEQUE SET bar_checkno=(select ar_checkno from AccountRegister where ar_id=" + ar_id
						+ " and bar_id=ar_sourceid and bar_kind=ar_sourcetype)"
						+ " where exists (select 1 from AccountRegister where bar_id=ar_sourceid and bar_kind=ar_sourcetype)");
			}
		}
		bool = baseDao.checkIf("user_tab_columns", "table_name='BILLAPCHEQUE'");
		if (bool) {
			if (status[2] != null && (status[2].equals("应付款") || status[2].equals("预付款"))) {
				baseDao.execute("UPDATE BILLAPCHEQUE SET bar_checkno=(select ar_checkno from AccountRegister where ar_id=" + ar_id
						+ " and bar_id=ar_sourceid and bar_kind=ar_sourcetype)"
						+ " where exists (select 1 from AccountRegister where bar_id=ar_sourceid and bar_kind=ar_sourcetype)");
			}
		}
		if ("应付款".equals(status[2].toString())) {
			SqlRowList billcode = baseDao.queryForRowSet(
					"select ard_ordercode from AccountRegisterDetail where ard_arid=? and nvl(ard_ordercode,' ')<>' '", ar_id);
			while (billcode.next()) {
				baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("ard_ordercode") });
			}
		}
		if ("保理付款".equals(status[2].toString())) {
			Object cacode = baseDao.getFieldDataByCondition("AccountRegister inner join AccountApply on ar_source = aa_code", "aa_cacode",
					"ar_sourcetype='出账申请' and ar_id=" + ar_id);
			// 更新保理进度
			baseDao.updateByCondition("FINBUSINAPPLY",
					"FS_STATUS = '放款',FS_LOADDATE = " + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "FS_CQCODE = '"
							+ cacode + "' and FS_LOADDATE is null");
			baseDao.execute("update AccountApply set aa_actpaydate=(select ar_date from accountregister where ar_aacode=aa_code and ar_id="
					+ ar_id + ") where aa_code='" + status[8] + "'");
			Master master = SystemSession.getUser().getCurrentMaster();
			Master parentMaster = null;
			if (master != null && master.getMa_pid() != null && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (null != parentMaster) {
				baseDao.execute("update " + parentMaster.getMa_user()
						+ ".FINBUSINAPPLY set FS_STATUS = '放款',FS_LOADDATE=sysdate where FS_CQCODE='" + cacode
						+ "' and FS_LOADDATE is null");
			}
		}
		// 记录操作
		baseDao.logger.others("msg.account", "msg.accountSuccess", caller, "ar_id", ar_id);
		// 执行记账后的其它逻辑
		handlerService.handler(caller, "account", "after", new Object[] { ar_id });

		// 清除流程
		String flowcaller = processService.getFlowCaller(caller);
		if (flowcaller != null) {
			try {
				// 删除该单据已实例化的流程
				processService.deletePInstance(ar_id, flowcaller, "audit");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void dataLink(int ar_id, Object ar_type) {
		String log = null;
		Object id = 0;
		Object code = null;
		Object[] data = null;
		if ("转存".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_id", "ar_code" },
					"ar_type='自动转存' and ar_sourcetype='转存' and ar_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，自动转存单号:" + "<a href=\"javascript:openUrl('jsps/fa/gs/accountRegister.jsp?formCondition=ar_idIS" + id
						+ "&gridCondition=ard_adidIS" + id + "&whoami=AccountRegister!Bank')\">" + code + "</a>";
			}
		} else if ("预收款".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("PreRec", new String[] { "pr_id", "pr_code" },
					"pr_kind='预收款' and pr_source='Bank' and pr_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，预收款单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/preRec.jsp?formCondition=pr_idIS" + id
						+ "&gridCondition=prd_pridIS" + id + "&whoami=PreRec!Ars!DERE')\">" + code + "</a>";
			}
		} else if ("预收退款".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("PreRec", new String[] { "pr_id", "pr_code" },
					"pr_kind='预收退款单' and pr_source='Bank' and pr_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，预收退款单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/preRec.jsp?formCondition=pr_idIS" + id
						+ "&gridCondition=prd_pridIS" + id + "&whoami=PreRec!Ars!DEPR')\">" + code + "</a>";
			}
		} else if ("预付款".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_id", "pp_code" },
					"pp_type='预付款' and pp_source='Bank' and pp_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，预付款单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/prepay.jsp?formCondition=pp_idIS" + id
						+ "&gridCondition=ppd_ppidIS" + id + "&whoami=PrePay!Arp!PAMT')\">" + code + "</a>";
			}
		} else if ("预付退款".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("PrePay", new String[] { "pp_id", "pp_code" },
					"pp_type='预付退款单' and pp_source='Bank' and pp_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，预付退款单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/prepay.jsp?formCondition=pp_idIS" + id
						+ "&gridCondition=ppd_ppidIS" + id + "&whoami=PrePay!Arp!PAPR')\">" + code + "</a>";
			}
		} else if ("应收款".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_id", "rb_code" },
					"rb_kind='收款单' and rb_source='Bank' and rb_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，收款单单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/recBalance.jsp?formCondition=rb_idIS" + id
						+ "&gridCondition=rbd_rbidIS" + id + "&whoami=RecBalance!PBIL')\">" + code + "</a>";
			}
		} else if ("应收退款".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("RecBalance", new String[] { "rb_id", "rb_code" },
					"rb_kind='应收退款单' and rb_source='Bank' and rb_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，应收退款单单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/recBalanceTK.jsp?formCondition=rb_idIS" + id
						+ "&gridCondition=rbd_rbidIS" + id + "&whoami=RecBalance!TK')\">" + code + "</a>";
			}
		} else if ("应付款".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_id", "pb_code" },
					"pb_kind='付款单' and pb_source='Bank' and pb_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，付款单单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/paybalance.jsp?formCondition=pb_idIS" + id
						+ "&gridCondition=pbd_pbidIS" + id + "&whoami=PayBalance')\">" + code + "</a>";
			}
		} else if ("应付退款".equals(ar_type)) {
			data = baseDao.getFieldsDataByCondition("PayBalance", new String[] { "pb_id", "pb_code" },
					"pb_kind='应付退款单' and pb_source='Bank' and pb_sourceid=" + ar_id);
			if (data != null) {
				id = data[0];
				code = data[1];
				log = "转入成功，应付退款单单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/paybalanceTK.jsp?formCondition=pb_idIS" + id
						+ "&gridCondition=pbd_pbidIS" + id + "&whoami=PayBalance!TK')\">" + code + "</a>";
			}
		}
		if (log != null) {
			BaseUtil.appendError(log);
		}
	}

	@Override
	public void resAccountedAccountRegister(int ar_id, String caller) {
		// 只能对状态为[已记账]的订单进行反记账操作!
		Object[] status = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_statuscode", "ar_code", "ar_type",
				"ar_source", "ar_accountcode", "ar_aacode" }, "ar_id=" + ar_id);
		if (!status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAccount_onlyAccount"));
		}
		checkVoucher(ar_id);
		Object[] date = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_date" }, "ar_id=" + ar_id);
		baseDao.checkCloseMonth("MONTH-B", date[0]);
		if ("自动转存".equals(status[2].toString())) {
			BaseUtil.showError("不能直接反记账自动转存单据,请反记账转存单[" + status[3] + "]");
		}
		if ("暂收款".equals(status[2].toString())) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(rb_code) from recbalance where rb_sourceid=? and rb_kind='冲应收款' and rb_source='Bank'", String.class,
					ar_id);
			if (dets != null) {
				BaseUtil.showError("已转冲应收款单，不允许进反记账！冲应收款单号：" + dets);
			}
		}
		Employee employee = SystemSession.getUser();
		String res = null;
		if (status[2] != null && "转存".equals(status[2].toString())) {
			Object[] code = baseDao.getFieldsDataByCondition("AccountRegister", new String[] { "ar_code", "ar_statuscode" },
					"ar_type='自动转存' and ar_source='" + status[1] + "'");
			if (code != null) {
				if (code[1] != null && "POSTED".equals(code[1])) {
					res = baseDao.callProcedure("SP_UNCOMMITEREGISTER", new Object[] { code[0], String.valueOf(employee.getEm_id()) });
					if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
						BaseUtil.showError(res);
					}
				}
				baseDao.execute("delete from AccountRegister where ar_code='" + code[0] + "'");
			}
			res = baseDao.callProcedure("SP_UNCOMMITEREGISTER", new Object[] { status[1], String.valueOf(employee.getEm_id()) });
			if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
				BaseUtil.showError(res);
			}
		} else {
			// 执行反记账操作
			res = baseDao.callProcedure("SP_UNCOMMITEREGISTER", new Object[] { status[1], String.valueOf(employee.getEm_id()) });
			if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
				BaseUtil.showError(res);
			}
		}
		baseDao.execute("update Category set ca_nowbalance2=NVL((select sum(nvl(ar_deposit,0)-nvl(ar_payment,0)) from AccountRegister where ar_accountcode='"
				+ status[4] + "' and ar_statuscode='COMMITED'),0) where ca_code='" + status[4] + "'");
		baseDao.execute("update AccountRegister set AR_AUDITMAN=null,AR_AUDITDATE=null where ar_id=" + ar_id);
		boolean bool = baseDao.checkIf("user_tab_columns", "table_name='BILLARCHEQUE'");
		if (bool) {
			if (status[2] != null && (status[2].equals("应收款") || status[2].equals("预收款"))) {
				baseDao.execute("UPDATE BILLARCHEQUE SET bar_checkno=null where exists (select 1 from AccountRegister where bar_id=ar_sourceid and bar_kind=ar_sourcetype)");
			}
		}
		if (status[2] != null && (status[2].equals("应付款") || status[2].equals("预付款"))) {
			baseDao.execute("UPDATE BILLAPCHEQUE SET bar_checkno=null where exists (select 1 from AccountRegister where bar_id=ar_sourceid and bar_kind=ar_sourcetype)");
			if ("应付款".equals(status[2].toString())) {
				SqlRowList billcode = baseDao.queryForRowSet(
						"select ard_ordercode from AccountRegisterDetail where ard_arid=? and nvl(ard_ordercode,' ')<>' '", ar_id);
				while (billcode.next()) {
					baseDao.procedure("SP_APLOCKAMOUNT2", new Object[] { billcode.getObject("ard_ordercode") });
				}
			}
		}
		if (status[2] != null && "保理付款".equals(status[2].toString())) {
			Object cacode = baseDao.getFieldDataByCondition("AccountRegister inner join AccountApply on ar_source = aa_code", "aa_cacode",
					"ar_sourcetype='出账申请' and ar_id=" + ar_id);
			baseDao.execute("update AccountApply set aa_actpaydate=null where aa_code='" + status[5]
					+ "' and not exists (select 1 from REIMBURSEMENTPLAN where rp_aacode=aa_code)");
			// 更新保理进度
			baseDao.updateByCondition("FINBUSINAPPLY", "FS_STATUS = '放款',FS_LOADDATE = null", "FS_CQCODE = '" + cacode + "'");
			Master master = SystemSession.getUser().getCurrentMaster();
			Master parentMaster = null;
			if (master != null && master.getMa_pid() != null && master.getMa_pid() > 0) {
				parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
			}
			if (null != parentMaster) {
				baseDao.execute("update " + parentMaster.getMa_user()
						+ ".FINBUSINAPPLY set FS_STATUS = '放款',FS_LOADDATE=null where FS_CQCODE='" + cacode + "'");
			}
		}
		// 记录操作
		baseDao.logger.others("msg.resAccount", "msg.resAccountSuccess", caller, "ar_id", ar_id);
	}

	@Override
	public int turnPayBalance(int ar_id, String caller) {
		int pbid = 0;
		// 判断该供单是否已经转入过付款单
		Object code = baseDao.getFieldDataByCondition("AccountRegister", "ar_code", "ar_id=" + ar_id);
		code = baseDao.getFieldDataByCondition("PayBalance", "pb_code", "pb_source='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("fa.gs.billap.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/fa/arp/paybalance.jsp?formCondition=pb_codeIS" + code + "&whoami=PayBalance')\">"
					+ code + "</a>&nbsp;");
		} else {
			// 转付款单
			pbid = accountRegisterDao.turnPayBalance(ar_id);
			// 记录操作
			baseDao.logger.turn("msg.turnPayBalance", caller, "ar_id", ar_id);
		}
		return pbid;
	}

	@Override
	public int turnRecBalance(int ar_id, String caller) {
		int rbid = 0;
		// 转收款单
		rbid = accountRegisterDao.turnRecBalance(ar_id);
		// 记录操作
		baseDao.logger.turn("msg.turnRecBalance", caller, "ar_id", ar_id);
		return rbid;
	}

	@Override
	public void updateRemark(int id, String remark, String caller) {
		baseDao.updateByCondition("AccountRegister", "ar_memo='" + remark + "'", "ar_id=" + id);
		// 记录操作
		baseDao.logger.others("更新备注", "msg.updateSuccess", caller, "ar_id", id);
	}

	/**
	 * 复制银行登记
	 */
	public JSONObject copyAccountRegister(int id, String caller) {
		Map<String, Object> dif = new HashMap<String, Object>();
		// Copy 银行登记
		int nId = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
		String code = baseDao.sGetMaxNumber("AccountRegister", 2);
		dif.put("ar_id", nId);
		dif.put("ar_code", "'" + code + "'");
		dif.put("ar_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
		dif.put("ar_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
		dif.put("ar_statuscode", "'ENTERING'");
		dif.put("ar_recorddate", "sysdate");
		dif.put("ar_source", "null");
		dif.put("ar_sourceid", 0);
		dif.put("ar_voucherid", 0);
		dif.put("ar_vouchercode", "null");
		dif.put("ar_custvendcode", "null");
		dif.put("ar_sourcetype", "null");
		dif.put("ar_checkno", "null");
		baseDao.copyRecord("AccountRegister", "AccountRegister", "ar_id=" + id, dif);
		// Copy 银行登记明细
		SqlRowList list = baseDao.queryForRowSet("SELECT ard_id FROM AccountRegisterDetail WHERE ard_arid=?", id);
		SqlRowList ass = null;
		Integer dId = null;
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dId = baseDao.getSeqId("ACCOUNTREGISTERDETAIL_SEQ");
			dif.put("ard_id", dId);
			dif.put("ard_arid", nId);
			baseDao.copyRecord("AccountRegisterDetail", "AccountRegisterDetail", "ard_id=" + list.getInt("ard_id"), dif);
			// Copy 明细辅助核算
			ass = baseDao.queryForRowSet("SELECT ars_id FROM AccountRegisterDetailAss WHERE ars_ardid=?", list.getInt("ard_id"));
			while (ass.next()) {
				dif = new HashMap<String, Object>();
				dif.put("ars_id", baseDao.getSeqId("ACCOUNTREGISTERDETAILASS_SEQ"));
				dif.put("ars_ardid", dId);
				baseDao.copyRecord("AccountRegisterDetailAss", "AccountRegisterDetailAss", "ars_id=" + ass.getInt("ars_id"), dif);
			}
		}
		// Copy 主表辅助核算
		list = baseDao.queryForRowSet("SELECT ass_id FROM AccountRegisterAss WHERE ASS_CONID=?", id);
		while (list.next()) {
			dif = new HashMap<String, Object>();
			dif.put("ass_id", baseDao.getSeqId("ACCOUNTREGISTERASS_SEQ"));
			dif.put("ASS_CONID", nId);
			baseDao.copyRecord("AccountRegisterAss", "AccountRegisterAss", "ass_id=" + list.getInt("ass_id"), dif);
		}
		JSONObject obj = new JSONObject();
		obj.put("ar_id", nId);
		obj.put("ar_code", code);
		return obj;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean ImportExcel(int id, Workbook wbs, String substring) {
		int sheetnum = wbs.getNumberOfSheets();
		Object textValue = "";
		/**
		 * 清除所有 该凭证的辅助核算
		 * */
		baseDao.deleteByCondition("AccountRegisterDetailAss", "ars_ardid in (select ard_id from  AccountRegisterDetail   where  ard_arid="
				+ id + ")");
		List<Map<String, Object>> maplist = new ArrayList<Map<String, Object>>();
		List<DetailGrid> details = detailGridDao.getDetailGridsByCaller("AccountRegisterDetailAss!Export", SpObserver.getSp());
		String detnofield = details.get(0).getDg_field();
		Object groupdetno = null;
		Map<String, Object> modelMap = null;
		DetailGrid detail = null;
		Map<String, List<Map<String, Object>>> AccountRegisterDetails = new HashMap<String, List<Map<String, Object>>>();
		List<Map<String, Object>> maps = null;
		if (sheetnum > 0) {
			HSSFSheet sheet = (HSSFSheet) wbs.getSheetAt(0);
			// 再遍历行 从第2行开始
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row != null) {
					modelMap = new HashMap<String, Object>();
					for (int j = 0; j < row.getLastCellNum(); j++) {
						textValue = "";
						HSSFCell cell = row.getCell(j);
						if (cell != null) {
							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_NUMERIC: {
								if (HSSFDateUtil.isCellDateFormatted(cell)) {
									textValue = DateUtil.parseDateToOracleString(Constant.YMD, cell.getDateCellValue());
								} else {
									textValue = cell.getNumericCellValue();
								}
								break;
							}
							case HSSFCell.CELL_TYPE_STRING:
								textValue = cell.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_BOOLEAN:
								textValue = cell.getBooleanCellValue();
								break;
							case HSSFCell.CELL_TYPE_FORMULA:
								textValue = cell.getCellFormula() + "";
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								textValue = "";
								break;
							case HSSFCell.CELL_TYPE_ERROR:
								textValue = "";
								break;
							default:
								textValue = "";
								break;
							}
							detail = details.get(j);
							if (textValue.toString().indexOf(".") > 0) {
								textValue = textValue.toString().substring(0, textValue.toString().indexOf("."));
							}
							if (j == 1) {
								if ("".equals(textValue)) {
									BaseUtil.showError("提示第" + (i + 1) + "行 没有设置" + detail.getDg_field() + "序号");
								} else {
									modelMap.put(detail.getDg_field(), textValue.toString().trim());
								}
							} else {
								if ("".equals(textValue)) {
									BaseUtil.showError("提示第" + (i + 1) + "行 没有设置" + detail.getDg_caption());
								} else
									modelMap.put(detail.getDg_field(), textValue.toString().trim());
							}
						}
					}
					modelMap.put("detno", i + 1);// 为每条数据生成行号
					maplist.add(modelMap);
				}
			}
		}
		for (Map<String, Object> map : maplist) {
			groupdetno = map.get(detnofield);
			if (groupdetno != null) {
				if (AccountRegisterDetails.containsKey(groupdetno.toString())) {
					AccountRegisterDetails.get(groupdetno).add(map);
				} else {
					maps = new ArrayList<Map<String, Object>>();
					maps.add(map);
					AccountRegisterDetails.put(groupdetno.toString(), maps);
				}
			}
		}
		List<Object[]> lists = baseDao.getFieldsDatasByCondition("AccountRegisterDetail", new String[] { "ard_id", "ard_detno",
				"ard_catecode" }, "ard_arid=" + id);
		List<String> sqls = new ArrayList<String>();
		Object vddetno = null;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = AccountRegisterDetails.keySet().iterator(); iterator.hasNext();) {
			vddetno = iterator.next().toString();
			for (Object[] obj : lists) {
				if (obj[1].toString().equals(vddetno)) {
					maps = AccountRegisterDetails.get(vddetno);
					int basedetno = 1;
					for (Map<String, Object> map : maps) {
						boolean bool = baseDao.checkByCondition("category",
								"ca_code='" + obj[2] + "' and instr(ca_assname,'" + map.get("ars_asstype") + "')>0");
						if (bool) {
							BaseUtil.showError("表格行" + map.get("detno") + "科目[" + obj[2] + "]不存在[" + map.get("ars_asstype") + "]辅助核算,请检查！");
						}
						map.put("ars_ardid", obj[0]);
						map.remove("ard_detno");
						map.remove("detno");
						map.put("ars_detno", basedetno);
						map.put("ars_type", "AccountRegister!Bank");
						map.put("ars_id", baseDao.getSeqId("ACCOUNTREGISTERDETAILASS_SEQ"));
						sqls.add(SqlUtil.getInsertSqlByMap(map, "AccountRegisterDetailAss"));
						basedetno++;
					}
				}
			}
		}
		baseDao.execute(sqls);
		// 校验
		validAccountRegister(id);
		return true;
	}

	@Override
	public void updateType(String custcode, String custname, String sellercode, String sellername, String arapcurrency, String araprate,
			String aramount, String vendcode, String vendname, String category, String description, String precurrency, String prerate,
			String preamount, String payment, String deposit, String id, String type, String caller) {
		// 应收票据收款、应付票据付款 必须有来源，不允许手工新增
		if ("应收票据收款".equals(type) || "应付票据付款".equals(type)) {
			BaseUtil.showError(type + " 必须有来源，不允许手工新增或通过改变单据类型来添加");
		}
		baseDao.updateByCondition("AccountRegister", "ar_custcode='" + custcode + "', ar_custname='" + custname + "',ar_sellercode='"
				+ sellercode + "'," + "ar_sellername='" + sellername + "', ar_arapcurrency='" + arapcurrency + "',ar_araprate='" + araprate
				+ "'," + "ar_aramount='" + aramount + "', ar_vendcode='" + vendcode + "', ar_vendname='" + vendname + "', ar_category='"
				+ category + "',ar_catedesc='" + description + "',ar_precurrency='" + precurrency + "', ar_prerate='" + prerate + "',"
				+ "ar_preamount='" + preamount + "',ar_payment='" + payment + "',ar_deposit='" + deposit + "' , ar_type='" + type
				+ "', ar_errstring=null", "ar_id='" + id + "'");
		// 记录操作
		baseDao.logger.others("更新类型", "msg.updateSuccess", caller, "ar_id", id);
	}

	@Override
	public void refreshQuery(String condition) {
		int premonth = Integer.parseInt(voucherDao.getJustPeriods("MONTH-B").get("PreYearmonth").toString());
		baseDao.execute(
				"update category set ca_nowbalance=nvl((select am_nowbalance from ALMonth where ca_id=am_accountcode and am_yearmonth=?),0)+nvl((select sum(nvl(ar_deposit,0))-sum(nvl(ar_payment,0)) from accountregister where to_char(ar_date,'yyyymm')>? and ar_statuscode='POSTED' and ar_accountcode=ca_code),0) "
						+ " where (abs(nvl(ca_isbank,0))=1 or abs(nvl(ca_iscash,0))=1) and exists (select 1 from almonth where ca_id=am_accountcode)",
				premonth, premonth);
	}

	@Override
	public int turnRecBalanceIMRE(int ar_id, String custcode, String thisamount) {
		int rbid = 0;
		// 转冲应收款单
		rbid = accountRegisterDao.turnRecBalanceIMRE(ar_id, custcode, thisamount);
		// 记录操作
		baseDao.logger.turn("转冲应收款单", "AccountRegister!Bank", "ar_id", ar_id);
		return rbid;
	}

	@Override
	public String[] printAccountRegister(int ar_id, String caller, String reportName, String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { ar_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.print("AccountRegister", "ar_id=" + ar_id, "ar_printstatus", "ar_printstatuscode");
		// 记录操作
		baseDao.logger.print(caller, "ar_id", ar_id);
		// 记录打印次数
		// baseDao.updateByCondition("AccountRegister",
		// "ar_count=nvl(ar_count,0)+1", "pu_id=" + ar_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { ar_id });
		return keys;
	}

	@Override
	public void endRecAmount(int ar_id, String caller) {
		baseDao.updateByCondition("AccountRegister", "ar_recamount=ar_deposit", "ar_id=" + ar_id);
		// 记录操作
		baseDao.logger.others("暂收款结案", "结案成功", caller, "ar_id", ar_id);
	}

	@Override
	public List<AccountRegisterDetailAss> findAss(int ar_id) {
		return accountRegisterDao.getAssByAccountRegisterId(ar_id);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@SuppressWarnings("rawtypes")
	public boolean ImportExcel(int id, Workbook wbs, String substring, String caller) {

		int sheetnum = wbs.getNumberOfSheets();
		Object textValue = "";
		/**
		 * 清除所有 该凭证的辅助核算
		 * */
		baseDao.deleteByCondition("AccountRegisterDetailAss", "ars_ardid in (select ard_id from  AccountRegisterDetail  where  ard_arid="
				+ id + ")");
		baseDao.deleteByCondition("AccountRegisterDetail", " ard_arid=" + id);
		List<Map<String, Object>> maplist = new ArrayList<Map<String, Object>>();
		List<DetailGrid> details = detailGridDao.getDetailGridsByCaller(caller, SpObserver.getSp());
		Object groupdetno = null;
		Map<String, Object> modelMap = null;
		DetailGrid detail = null;
		Map<String, List<Map<String, Object>>> AccountRegisterDetails = new TreeMap<String, List<Map<String, Object>>>(
				new Comparator<String>() {
					public int compare(String obj1, String obj2) {
						return Integer.valueOf(obj1) - Integer.valueOf(obj2);
					}
				});
		List<Map<String, Object>> maps = null;
		if (sheetnum > 0) {
			HSSFSheet sheet = (HSSFSheet) wbs.getSheetAt(0);
			DataFormatter fmt = new DataFormatter();

			// 再遍历行 从第2行开始
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row != null) {
					modelMap = new HashMap<String, Object>();
					String str1 = "";
					for (int j = 0; j < row.getLastCellNum(); j++) {
						textValue = "";
						HSSFCell cell = row.getCell(j);
						textValue = fmt.formatCellValue(cell);
						detail = details.get(j);
						if ((detail.getDg_field().equals("ard_explanation") || detail.getDg_field().equals("ard_catecode"))
								&& ("".equals(textValue))) {
							BaseUtil.showError("表格第" + (i + 1) + "行 没有设置" + detail.getDg_caption());
						} else
							modelMap.put(detail.getDg_field(), textValue.toString().trim());

						if (detail.getDg_field().equals("ard_explanation") || detail.getDg_field().equals("ard_catecode")
								|| detail.getDg_field().equals("ard_debit") || detail.getDg_field().equals("ard_credit")) {
							// 要比较的字段
							str1 += "#";
							str1 += textValue.toString().trim();
						}
					}
					modelMap.put("detno", i + 1);// 为每条数据生成行号
					modelMap.put("iscombine", 0);
					modelMap.put("str1", str1);
					if (!modelMap.isEmpty())
						maplist.add(modelMap);

				}
			}
		}

		for (Map<String, Object> map1 : maplist) {
			Object str1 = map1.get("str1");
			Object ass1 = map1.get("ars_asstype");
			Object detno = map1.get("detno");
			Object ard_detno = map1.get("ard_detno");
			boolean bool = baseDao.checkByCondition("category", "ca_code='" + map1.get("ard_catecode") + "' and instr(ca_assname,'" + ass1
					+ "')>0");
			if (bool) {
				BaseUtil.showError("表格行" + map1.get("detno") + "科目[" + map1.get("ard_catecode") + "]不存在[" + ass1 + "]辅助核算,请检查！");
			}
			Object asscount = baseDao.getFieldDataByCondition("category", "length(ca_assname)-length(replace(ca_assname,'#',''))+1",
					"ca_code='" + map1.get("ard_catecode") + "'");
			// 科目设了多辅助核算（辅助核算大于1）或不设辅助核算时才合并
			if (map1.get("iscombine").equals(0) && ass1 != null && !StringUtils.isEmpty(ass1.toString())
					&& ((asscount != null && !asscount.toString().equals("1")) || asscount == null)) {
				if (asscount != null && !asscount.toString().equals("1") && (ard_detno == null || ard_detno.equals("")))
					BaseUtil.showError("表格行" + map1.get("detno") + "科目" + map1.get("ard_catecode") + "设置了多辅助核算，需填写序号");
				for (Map<String, Object> map2 : maplist) {
					// 科目中填了多辅助核算
					if (asscount != null && !asscount.toString().equals("1") && ard_detno != null && !ard_detno.equals("")
							&& ard_detno.equals(map2.get("ard_detno")) && !str1.equals(map2.get("str1"))) {
						BaseUtil.showError("表格行" + detno + "、行" + map2.get("detno") + "序号一致，摘要+科目编号+借、贷方金额不一致！");
					}
					if (((asscount != null && !asscount.toString().equals("1") && ard_detno != null && !ard_detno.equals("") && ard_detno
							.equals(map2.get("ard_detno"))) || asscount == null)
							&& str1.equals(map2.get("str1"))
							&& !detno.equals(map2.get("detno"))
							&& (map2.get("ars_asstype") != null && !map2.get("ars_asstype").equals(ass1))) {
						map2.put("detno", map1.get("detno"));
						map2.put("iscombine", 1);
					}
				}
			}
		}

		for (Map<String, Object> map : maplist) {
			groupdetno = map.get("detno");
			if (groupdetno != null) {
				if (AccountRegisterDetails.containsKey(groupdetno.toString())) {
					AccountRegisterDetails.get(groupdetno.toString()).add(map);
				} else {
					maps = new ArrayList<Map<String, Object>>();
					maps.add(map);
					AccountRegisterDetails.put(groupdetno.toString(), maps);
				}
			}
		}

		List<String> sqls = new ArrayList<String>();
		Object vddetno = null;
		int formdetno = 1;
		for (Iterator iterator = AccountRegisterDetails.keySet().iterator(); iterator.hasNext();) {
			vddetno = iterator.next().toString();
			maps = AccountRegisterDetails.get(vddetno);
			Map<String, Object> map1 = new HashMap<String, Object>();
			int ard_id = baseDao.getSeqId("AccountRegisterDetail_SEQ");
			if (maps.get(0).get("ard_catecode") != null && !maps.get(0).get("ard_catecode").equals("")) {
				Object asscount = baseDao.getFieldDataByCondition("category", "length(ca_assname)-length(replace(ca_assname,'#',''))+1",
						"ca_code='" + maps.get(0).get("ard_catecode") + "'");
				if (asscount != null && !asscount.toString().equals("1") && !asscount.toString().equals(maps.size() + "")) {
					BaseUtil.showError("表格行" + maps.get(0).get("detno") + "记录数" + maps.size() + "与科目辅助核算数" + asscount + "不一致！");
				}
			}
			if (maps.get(0).get("ard_catecode") != null && !maps.get(0).get("ard_catecode").equals("")) {
				map1.put("ard_id", ard_id);
				map1.put("ard_arid", id);
				map1.put("ard_detno", formdetno);
				map1.put("ard_explanation", maps.get(0).get("ard_explanation"));
				map1.put("ard_catecode", maps.get(0).get("ard_catecode"));
				Object catedesc = baseDao.getFieldDataByCondition("Category", "ca_description",
						"ca_code = '" + maps.get(0).get("ard_catecode") + "'");
				map1.put("ard_catedesc", catedesc);
				map1.put("ard_debit", StringUtil.nvl(maps.get(0).get("ard_debit"), "0"));
				map1.put("ard_credit", StringUtil.nvl(maps.get(0).get("ard_credit"), "0"));
				sqls.add(SqlUtil.getInsertSqlByMap(map1, "AccountRegisterDetail"));
				formdetno++;
			}
			int basedetno = 1;
			for (Map<String, Object> map : maps) {
				if (map.get("ars_asstype") != null && !map.get("ars_asstype").equals("")) {
					if (map.get("ars_assname") == null || map.get("ars_assname").equals("")) {
						map.remove("ars_assname");
						Object[] objs = baseDao.getFieldsDataByCondition("ASSKIND", new String[] { "AK_TABLE", "AK_ASSCODE", "AK_ASSNAME",
								"AK_ID" }, "ak_name='" + map.get("ars_asstype") + "'");
						if (objs != null && objs[0] != null && objs[1] != null && objs[2] != null) {
							if (objs[0].equals("AssKindDetail"))
								map.put("ars_assname",
										baseDao.getFieldDataByCondition("AssKindDetail", "akd_assname",
												"akd_asscode='" + map.get("ars_asscode") + "' and akd_akid =" + objs[3]));
							else
								map.put("ars_assname",
										baseDao.getFieldDataByCondition(objs[0].toString(), objs[2].toString(),
												objs[1] + "='" + map.get("ars_asscode") + "'"));
						}
					} else if (map.get("ars_assname") != null && map.get("ars_asscode") != null) {

						Object ars_assname = null;
						Object[] objs = baseDao.getFieldsDataByCondition("ASSKIND", new String[] { "AK_TABLE", "AK_ASSCODE", "AK_ASSNAME",
								"AK_ID" }, "ak_name='" + map.get("ars_asstype") + "'");
						if (objs != null && objs[0] != null && objs[1] != null && objs[2] != null) {
							if (objs[0].equals("AssKindDetail"))
								ars_assname = baseDao.getFieldDataByCondition("AssKindDetail", "akd_assname",
										"akd_asscode='" + map.get("ars_asscode") + "' and akd_akid =" + objs[3]);
							else
								ars_assname = baseDao.getFieldDataByCondition(objs[0].toString(), objs[2].toString(),
										objs[1] + "='" + map.get("ars_asscode") + "'");
						}
						if (!map.get("ars_assname").equals(ars_assname)) {
							BaseUtil.showError("辅助核算编号:" + map.get("ars_asscode") + "与辅助核算名称:" + map.get("ars_assname") + "不匹配");

						}

					}
					map.put("ars_ardid", ard_id);
					map.remove("ard_explanation");
					map.remove("ard_catecode");
					map.remove("ard_catedesc");
					map.remove("ard_debit");
					map.remove("ard_credit");
					map.remove("str1");
					map.remove("detno");
					map.remove("ard_detno");
					map.remove("iscombine");
					map.put("ars_detno", basedetno);
					map.put("ars_type", "AccountRegister!Bank");
					map.put("ars_id", baseDao.getSeqId("AccountRegisterDetailASS_SEQ"));
					sqls.add(SqlUtil.getInsertSqlByMap(map, "AccountRegisterDetailASS"));
					basedetno++;
				}
			}
		}
		baseDao.execute(sqls);
		// 校验
		validAccountRegister(id);
		return true;
	}
}
