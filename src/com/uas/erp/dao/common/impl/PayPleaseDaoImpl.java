package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PayPleaseDao;
import com.uas.erp.model.Employee;

@Repository
public class PayPleaseDaoImpl extends BaseDao implements PayPleaseDao {
	static final String FINDPAYPLEASEDETAILDET = "select * from paypleasedetaildet where ppdd_ppdid=?";
	static final String TURNPAYPLEASEDETAIL = "SELECT * from PayPlease left join PayPleaseDetail on pp_id=ppd_ppid WHERE ppd_id=?";
	static final String INSERTBILLAP = "INSERT INTO BillAP(bap_id,bap_code,bap_date,bap_currency,bap_rate"
			+ ",bap_balance,bap_vendcode,bap_vendname,bap_remark,bap_status,bap_statuscode,bap_operator,bap_recorder"
			+ ",bap_indate,bap_getstatus,bap_sendstatus,bap_doublebalance,bap_topaybalance,bap_cmcurrency,bap_paybillcode,"
			+ "bap_billkind,bap_ppdid,bap_settleamount,bap_leftamount,bap_cmrate,bap_othercatecode,bap_nowstatus)"
			+ " values(?,?,sysdate,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,1,?,?)";
	static final String INSERTBILLARCHANGE = "INSERT INTO BILLARCHANGE(brc_id,brc_code,brc_date,brc_kind,brc_billkind2"
			+ ",brc_status,brc_amount,brc_vendcode,brc_vendname,brc_currency,brc_rate"
			+ ",brc_cmcurrency,brc_cmrate,brc_cmamount,brc_recorder,brc_indate,brc_statuscode,BRC_PPCODE,BRC_PPDID,brc_explain)"
			+ " values(?,?,sysdate,'背书转让',?,?,?,?,?,?,?,?,?,?,?,sysdate,'ENTERING',?,?,?)";

	static final String INSERTPREPAY = "insert into prepay ("
			+ "pp_id,pp_code,pp_date,pp_type,pp_indate,pp_recorder,pp_recorderid,pp_statuscode,pp_status,pp_auditstatuscode,pp_auditstatus,pp_printstatuscode,pp_printstatus,"
			+ "pp_vendid,pp_vendcode,pp_vendname,pp_buyer,pp_buyerid,pp_currency,pp_vmcurrency,pp_vmrate,pp_vmamount,pp_jsamount,pp_amount,"
			+ "pp_sourceid,pp_source,pp_sourcecode,pp_pleasecode,pp_bankname,pp_bankno,pp_paycontent,pp_remark,pp_accountcode,pp_accountname) values("
			+ "?,?,?,'预付款',sysdate,?,?,'UNPOST',?,'ENTERING',?,'UNPRINT',?," + "?,?,?,?,?,?,?,?,?,?,?," + "?,?,?,?,?,?,?,?,?,?)";

	static final String INSERTPAYBALANCE = "insert into paybalance (pb_id,pb_code,pb_kind,pb_date,pb_statuscode,pb_auditstatuscode,"
			+ "pb_vendcode,pb_vendname,pb_methodid,pb_method,pb_currency,pb_cateid,pb_catecode,pb_catename,pb_havebalance,pb_amount,pb_havepay,"
			+ "pb_source,pb_sourcecode,pb_status,pb_auditstatus,pb_sourceid,pb_vmamount,pb_vmcurrency,pb_vmrate,pb_apamount,"
			+ "pb_recorder,pb_recorderid,pb_ppcode,pb_bankname,pb_bankno,pb_paycontent,pb_remark,pb_ppdid) "
			+ "values(?,?,?,sysdate,'UNPOST','ENTERING',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	static final String INSERTPAYBALANCEDETAIL = "insert into paybalancedetail (pbd_detno,pbd_ordercode,pbd_currency,"
			+ "pbd_apamount,pbd_havepay,pbd_nowbalance,pbd_id,pbd_code,pbd_pbid,pbd_catecode,pbd_ppddid) "
			+ "values (?,?,?,?,?,?,PAYBALANCEDETAIL_SEQ.NEXTVAL,?,?,?,?)";
	static final String INSERTPREPAYDETAIL = "insert into prepaydetail (ppd_id,ppd_ppid,ppd_code,ppd_detno,ppd_currency,ppd_nowbalance,"
			+ "ppd_ordertype,ppd_ordercode,ppd_makecode,ppd_date,ppd_orderamount,ppd_haveprebalance,ppd_ppddid,ppd_orderdetno,ppd_pdid,ppd_prodcode) "
			+ "values(PrePayDetail_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	static final String INSERTACCOUNTREGISTER = "insert into AccountRegister ("
			+ "ar_id,ar_vendcode,ar_vendname,ar_recorddate,ar_date,ar_payment,ar_type,"
			+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
			+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_recbankaccount,ar_recbank,ar_memo,ar_cateid,ar_checktitle,ar_arapcurrency) values("
			+ "?,?,?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTACCOUNTREGISTERDETAIL = "insert into AccountRegisterDetail ("
			+ "ard_detno,ard_currency,ard_debit,ard_nowbalance,ard_ordertype,ard_orderid,ard_ordercode,ard_orderdetno,ard_orderamount,"
			+ "ard_payments,ard_haveprepay,ard_id,ard_arid,ard_catecode,ard_makecode,ard_ppddid,ard_prodcode,ard_pdid) values (?,?,?,?,?,?,?,?,?,"
			+ "?,?,ACCOUNTREGISTERDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?)";

	@Override
	public JSONObject turnPayBalance(int ppd_id, String sourcecode, Object amount) {
		Employee employee = SystemSession.getUser();
		// 根据ppd_id 查找PayPleaseDetail 表中数据
		SqlRowList rsPPD = queryForRowSet(TURNPAYPLEASEDETAIL, ppd_id);
		// 根据ppdd_ppdid 查找PayPleaseDetailDet 表中数据
		SqlRowList rsPPDD = queryForRowSet(FINDPAYPLEASEDETAILDET, ppd_id);
		if (rsPPD.next()) {
			int pb_id = getSeqId("PAYBALANCE_SEQ");
			String code = sGetMaxNumber("PayBalance", 2);
			boolean bool = execute(
					INSERTPAYBALANCE,
					new Object[] { pb_id, code, "付款单", rsPPD.getObject("ppd_vendcode"), rsPPD.getObject("ppd_vendname"),
							rsPPD.getObject("ppd_paymethodid"), rsPPD.getObject("ppd_paymethod"), rsPPD.getObject("ppd_currency"), 0,
							rsPPD.getObject("pp_paymentcode"), rsPPD.getObject("pp_payment"), rsPPD.getGeneralDouble("ppd_applyamount"),
							rsPPD.getGeneralDouble("ppd_applyamount"), 0, "付款申请", rsPPD.getObject("pp_code"),
							BaseUtil.getLocalMessage("UNPOST"), BaseUtil.getLocalMessage("ENTERING"), rsPPD.getObject("pp_id"),
							rsPPD.getGeneralDouble("ppd_applyamount"), rsPPD.getObject("ppd_currency"), 1,
							rsPPD.getGeneralDouble("ppd_applyamount"), employee.getEm_name(), employee.getEm_id(),
							rsPPD.getObject("pp_code"), rsPPD.getObject("ppd_bankname"), rsPPD.getObject("ppd_bankaccount"),
							rsPPD.getObject("pp_payremark"), rsPPD.getObject("pp_remark"), rsPPD.getObject("ppd_id") });
			if (bool) {
				int detno = 1;
				while (rsPPDD.next()) {
					execute(INSERTPAYBALANCEDETAIL,
							new Object[] { detno++, rsPPDD.getObject("ppdd_billcode"), rsPPDD.getObject("ppdd_currency"),
									rsPPDD.getGeneralDouble("ppdd_billamount"), rsPPDD.getGeneralDouble("ab_payamount"),
									rsPPDD.getGeneralDouble("ppdd_thisapplyamount"), code, pb_id, rsPPDD.getObject("ppdd_catecode"),
									rsPPDD.getObject("ppdd_id") });
				}
				execute("update paybalancedetail set (pbd_orderdate,pbd_planpaydate,pbd_duedate,pbd_ordertype,pbd_buyerid,pbd_buyer)=(select ab_date,ab_planpaydate,ab_paydate,ab_class,ab_buyerid,ab_buyer from apbill where ab_code=pbd_ordercode) where nvl(pbd_ordercode,' ')<>' ' and pbd_pbid="
						+ pb_id);
				updateByCondition("paybalancedetail", "pbd_cateid=(select ca_id from category where ca_code=pbd_catecode)", "pbd_pbid="
						+ pb_id + " and nvl(pbd_catecode,' ')<>' '");
				updateByCondition("PayPleaseDetail", "ppd_statuscode='TURNPB',ppd_status='" + BaseUtil.getLocalMessage("TURNPB") + "'",
						"ppd_id=" + ppd_id);
				JSONObject j = new JSONObject();
				j.put("pb_id", pb_id);
				j.put("pb_code", code);
				return j;
			}
		}
		return null;
	}

	@Override
	public JSONObject turnPrePay(int ppd_id, String sourcecode, Object amount) {
		Employee employee = SystemSession.getUser();
		// 根据ppd_id 查找PayPleaseDetail 表中数据
		SqlRowList rsPPD = queryForRowSet(TURNPAYPLEASEDETAIL, ppd_id);
		// 根据ppdd_ppdid 查找PayPleaseDetailDet 表中数据
		SqlRowList rsPPDD = queryForRowSet(FINDPAYPLEASEDETAILDET, ppd_id);
		Double thisamount = Double.parseDouble(amount.toString());
		if (rsPPD.next()) {
			if (Math.abs(thisamount)
					- (Math.abs(rsPPD.getGeneralDouble("ppd_applyamount")) - Math.abs(rsPPD.getGeneralDouble("ppd_account"))) > 0.01) {
				BaseUtil.showError("本次付款金额大于剩余未付金额!");
			}
			int pp_id = getSeqId("PREPAY_SEQ");
			String code = sGetMaxNumber("PrePay", 2);
			Object[] vend = getFieldsDataByCondition("Vendor", new String[] { "ve_id", "ve_buyerid", "ve_buyername" },
					"ve_code='" + rsPPD.getObject("ppd_vendcode") + "'");
			boolean bool = execute(
					INSERTPREPAY,
					new Object[] { pp_id, code, rsPPD.getObject("pp_thispaydate"), employee.getEm_name(), employee.getEm_id(),
							BaseUtil.getLocalMessage("UNPOST"), BaseUtil.getLocalMessage("ENTERING"), BaseUtil.getLocalMessage("UNPRINT"),
							vend[0], rsPPD.getObject("ppd_vendcode"), rsPPD.getObject("ppd_vendname"), vend[2], vend[1],
							rsPPD.getObject("ppd_currency"), rsPPD.getObject("ppd_currency"), 1, thisamount, thisamount, thisamount,
							rsPPD.getObject("ppd_id"), "预付款申请", sourcecode, sourcecode, rsPPD.getObject("ppd_bankname"),
							rsPPD.getObject("ppd_bankaccount"), rsPPD.getObject("pp_payremark"), rsPPD.getObject("pp_remark"),
							rsPPD.getObject("pp_paymentcode"), rsPPD.getObject("pp_payment") });
			if (bool) {
				int detno = 1;
				if (thisamount < rsPPD.getGeneralDouble("ppd_applyamount")) {
					String res = callProcedure("CT_CATCHPUTOPPSPLIT", new Object[] { ppd_id, thisamount, pp_id });
					if (!("ok").equals(res.trim())) {
						BaseUtil.showError(res);
					}
				} else {
					while (rsPPDD.next()) {
						execute(INSERTPREPAYDETAIL,
								new Object[] { pp_id, code, detno++, rsPPDD.getObject("ppdd_currency"),
										rsPPDD.getObject("ppdd_thisapplyamount"), rsPPDD.getObject("ppdd_type"),
										rsPPDD.getObject("ppdd_pucode"), rsPPDD.getObject("ppdd_makecode"),
										rsPPDD.getObject("ppdd_billdate"), rsPPDD.getGeneralDouble("ppdd_billamount"),
										rsPPDD.getObject("ppdd_account"), rsPPDD.getObject("ppdd_id"),
										rsPPDD.getGeneralInt("ppdd_pddetno"), rsPPDD.getGeneralInt("ppdd_pdid"),
										rsPPDD.getObject("ppdd_prodcode") });
					}
				}
				execute("update PrePayDetail set (ppd_buyer,ppd_buyerid)=(select pu_buyername,pu_buyerid from purchasewithoa_view where ppd_ordercode=pu_code) where nvl(ppd_ordercode,' ')<>' ' and ppd_ppid="
						+ pp_id);
				execute("update PrePayDetail set (ppd_buyer,ppd_buyerid)=(select pp_buyer,pp_buyerid from PrePay where ppd_ppid=pp_id) where nvl(ppd_buyer,' ')=' ' and ppd_ppid="
						+ pp_id);
				updateByCondition("PayPleaseDetail", "ppd_account=nvl(ppd_account,0) + " + amount, "ppd_id=" + ppd_id);
				updateByCondition("PayPleaseDetail", "ppd_statuscode='PARTPP',ppd_status='" + BaseUtil.getLocalMessage("PARTPP") + "'",
						"ppd_id=" + ppd_id + " and round(ppd_account,2) < round(ppd_applyamount,2) AND NVL(ppd_account,0)>0");
				updateByCondition("PayPleaseDetail", "ppd_statuscode='TURNPP',ppd_status='" + BaseUtil.getLocalMessage("TURNPP") + "'",
						"ppd_id=" + ppd_id + " and round(ppd_account,2) = round(ppd_applyamount,2) AND NVL(ppd_account,0)>0");
				updateByCondition("PayPlease", "pp_paystatuscode='PAYMENTED',pp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED") + "'",
						"pp_id in (select ppd_ppid from PayPleaseDetail where ppd_statuscode='TURNPP' and ppd_id=" + ppd_id + ")");
				updateByCondition("PayPlease", "pp_paystatuscode='PARTPAYMENT',pp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT")
						+ "'", "pp_id in (select ppd_ppid from PayPleaseDetail where ppd_statuscode='PARTPP' and ppd_id=" + ppd_id + ")");
				JSONObject j = new JSONObject();
				j.put("pp_id", pp_id);
				j.put("pp_code", code);
				return j;
			}
		}
		return null;
	}

	@Override
	public JSONObject turnBillAP(int id) {
		SqlRowList rs = queryForRowSet(TURNPAYPLEASEDETAIL, new Object[] { id });
		if (rs.next()) {
			Employee employee = SystemSession.getUser();
			int bapid = getSeqId("BILLAP_SEQ");
			String bapcode = sGetMaxNumber("BillAP", 2);
			Object rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + rs.getObject("ppd_currency") + "'");
			Timestamp time = Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS));
			execute(INSERTBILLAP,
					new Object[] { bapid, bapcode, time, rs.getObject("ppd_currency"), rate, rs.getObject("ppd_auditamount"),
							rs.getObject("ppd_vendcode"), rs.getObject("ppd_vendname"), rs.getObject("pp_remark"),
							BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_name(), employee.getEm_name(), time, "未领取",
							"未寄出", rs.getObject("ppd_applyamount"), rs.getObject("ppd_applyamount"), rs.getObject("ppd_currency"),
							rs.getObject("pp_code"), rs.getObject("pp_type"), rs.getObject("ppd_id"), 0, rs.getObject("ppd_applyamount"),
							rs.getObject("pp_paymentcode"), "未付款" });
			JSONObject j = new JSONObject();
			j.put("bap_id", bapid);
			j.put("bap_code", bapcode);
			return j;
		} else {
			return null;
		}
	}

	@Override
	public JSONObject turnBankRegister(int ppd_id, String sourcecode, String type, Object amount, String date, String checkno) {
		Employee employee = SystemSession.getUser();
		Object[] obj = getFieldsDataByCondition("payplease", new String[] { "pp_remark", "pp_payremark", "pp_paymentcode", "pp_payment",
				"pp_id" }, "pp_code='" + sourcecode + "'");
		// 根据ppd_id 查找PayPleaseDetail 表中数据
		SqlRowList rsPPD = queryForRowSet(TURNPAYPLEASEDETAIL, ppd_id);
		// 根据ppdd_ppdid 查找PayPleaseDetailDet 表中数据
		SqlRowList rsPPDD = queryForRowSet(FINDPAYPLEASEDETAILDET, ppd_id);
		Double thisamount = Double.parseDouble(amount.toString());
		if (rsPPD.next()) {
			if (Math.abs(thisamount)
					- (Math.abs(rsPPD.getGeneralDouble("ppd_applyamount")) - Math.abs(rsPPD.getGeneralDouble("ppd_account"))) > 0.01) {
				BaseUtil.showError("本次付款金额大于剩余未付金额!");
			}
			int ar_id = getSeqId("ACCOUNTREGISTER_SEQ");
			String code = sGetMaxNumber("AccountRegister", 2);
			Object[] cate = getFieldsDataByCondition("Category", new String[] { "ca_id", "ca_currency" }, "ca_code='" + obj[2] + "'");
			boolean bool = execute(
					INSERTACCOUNTREGISTER,
					new Object[] { ar_id, rsPPD.getObject("ppd_vendcode"), rsPPD.getObject("ppd_vendname"), amount, type, code, ppd_id,
							sourcecode, "付款申请", BaseUtil.getLocalMessage("ENTERING"), employee.getEm_name(), obj[2], obj[3], cate[1],
							rsPPD.getObject("ppd_bankaccount"), rsPPD.getObject("ppd_bankname"), obj[0], cate[0],
							rsPPD.getObject("ppd_bankman"), rsPPD.getObject("ppd_currency") });
			if (bool) {
				execute("update accountregister set ar_accountrate=(select cm_crrate from currencysmonth where cm_crname=ar_accountcurrency and cm_yearmonth=to_char(ar_date,'yyyymm')) where ar_id="
						+ ar_id);
				execute("update accountregister set ar_refno='" + checkno + "', ar_date=to_date('" + date + "','yyyy-mm-dd') where ar_id="
						+ ar_id);
				int detno = 1;
				if ("应付款".equals(type)) {
					execute("update paypleasedetaildet set ppdd_type=(select ab_class from apbill where ppdd_billcode=ab_code) where ppdd_ppdid="
							+ ppd_id + " and nvl(ppdd_billcode,' ')<>' '");
					if (thisamount < rsPPD.getGeneralDouble("ppd_applyamount")) {
						String res = callProcedure("CT_CATCHABTOPPBBSPLIT", new Object[] { ppd_id, thisamount, ar_id });
						if (!("ok").equals(res.trim())) {
							BaseUtil.showError(res);
						}
					} else {
						while (rsPPDD.next()) {
							execute(INSERTACCOUNTREGISTERDETAIL,
									new Object[] { detno++, rsPPDD.getObject("ppdd_currency"), rsPPDD.getObject("ppdd_thisapplyamount"),
											rsPPDD.getObject("ppdd_thisapplyamount"), rsPPDD.getObject("ppdd_type"),
											rsPPDD.getObject("ppdd_id"), rsPPDD.getObject("ppdd_billcode"), 0,
											rsPPDD.getObject("ppdd_billamount"), 0, rsPPDD.getObject("ppdd_account"), ar_id,
											rsPPDD.getObject("ppdd_catecode"), rsPPDD.getObject("ppdd_makecode"),
											rsPPDD.getObject("ppdd_id"), null, 0 });
							execute("update accountregisterdetail set ard_rate=(select cr_rate from currencys where cr_name=ard_currency) where ard_arid="
									+ ar_id);
						}
					}
				} else if ("预付款".equals(type)) {
					if (thisamount < rsPPD.getGeneralDouble("ppd_applyamount")) {
						String res = callProcedure("CT_CATCHPUTOPPBBSPLIT", new Object[] { ppd_id, thisamount, ar_id });
						if (!("ok").equals(res.trim())) {
							BaseUtil.showError(res);
						}
					} else {
						while (rsPPDD.next()) {
							Object purc = getFieldDataByCondition("PURCHASEWITHOA_VIEW", "pu_id",
									"pu_code='" + rsPPDD.getObject("ppdd_pucode") + "' and pu_type='" + rsPPDD.getObject("ppdd_type") + "'");
							execute(INSERTACCOUNTREGISTERDETAIL,
									new Object[] { detno++, rsPPDD.getObject("ppdd_currency"), rsPPDD.getObject("ppdd_thisapplyamount"),
											rsPPDD.getObject("ppdd_thisapplyamount"), rsPPDD.getObject("ppdd_type"), purc,
											rsPPDD.getObject("ppdd_pucode"), rsPPDD.getGeneralInt("ppdd_pddetno"),
											rsPPDD.getObject("ppdd_billamount"), 0, rsPPDD.getObject("ppdd_account"), ar_id, null,
											rsPPDD.getObject("ppdd_makecode"), rsPPDD.getObject("ppdd_id"),
											rsPPDD.getObject("ppdd_prodcode"), rsPPDD.getGeneralInt("ppdd_pdid") });
							execute("update accountregisterdetail set ard_rate=(select cr_rate from currencys where cr_name=ard_currency) where ard_arid="
									+ ar_id);
						}
					}
				}
				updateByCondition(
						"AccountRegister",
						"ar_araprate=1,ar_aramount=ar_payment,ar_apamount=ar_payment,ar_accountrate=(select nvl(cm_crrate,0) from currencysmonth where cm_crname=ar_accountcurrency and cm_yearmonth=to_char(ar_date,'yyyymm'))",
						"ar_id=" + ar_id);
				updateByCondition("AccountRegister", "ar_cateid=(select ca_id from category where ca_code=ar_accountcode)", "ar_id="
						+ ar_id + " and nvl(ar_accountcode,' ')<>' '");
				updateByCondition("AccountRegisterDetail", "ard_cateid=(select ca_id from category where ca_code=ard_catecode)",
						"ard_arid=" + ar_id + " and nvl(ard_catecode,' ')<>' '");
				updateByCondition("PayPleaseDetail", "ppd_account=nvl(ppd_account,0) + " + amount, "ppd_id=" + ppd_id);
				updateByCondition("PayPleaseDetail", "ppd_statuscode='PARTBR',ppd_status='" + BaseUtil.getLocalMessage("PARTBR") + "'",
						"ppd_id=" + ppd_id + " and round(ppd_account,2) < round(ppd_applyamount,2) AND NVL(ppd_account,0)>0");
				updateByCondition("PayPleaseDetail", "ppd_statuscode='TURNBR',ppd_status='" + BaseUtil.getLocalMessage("TURNBR") + "'",
						"ppd_id=" + ppd_id + " and round(ppd_account,2) = round(ppd_applyamount,2) AND NVL(ppd_account,0)>0");
				updateByCondition("PayPlease", "pp_paystatuscode='PAYMENTED',pp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED") + "'",
						"pp_id in (select ppd_ppid from PayPleaseDetail where ppd_statuscode='TURNBR' and ppd_id=" + ppd_id + ")");
				updateByCondition("PayPlease", "pp_paystatuscode='PARTPAYMENT',pp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT")
						+ "'", "pp_id in (select ppd_ppid from PayPleaseDetail where ppd_statuscode='PARTBR' and ppd_id=" + ppd_id + ")");
				JSONObject j = new JSONObject();
				j.put("ar_id", ar_id);
				j.put("ar_code", code);
				return j;
			}
		}
		return null;
	}

	@Override
	public JSONObject turnBillAP(int ppd_id, String sourcecode, String type, Object amount, String date, String checkno) {
		// 根据ppd_id 查找PayPleaseDetail 表中数据
		SqlRowList rsPPD = queryForRowSet(TURNPAYPLEASEDETAIL, ppd_id);
		Double thisamount = Double.parseDouble(amount.toString());
		if (rsPPD.next()) {
			Employee employee = SystemSession.getUser();
			if (Math.abs(thisamount)
					- (Math.abs(rsPPD.getGeneralDouble("ppd_applyamount")) - Math.abs(rsPPD.getGeneralDouble("ppd_account"))) > 0.01) {
				BaseUtil.showError("本次付款金额大于剩余未付金额!");
			}
			int bapid = getSeqId("BILLAP_SEQ");
			String bapcode = sGetMaxNumber("BillAP", 2);
			Object rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + rsPPD.getObject("ppd_currency") + "'");
			boolean bool = execute(
					INSERTBILLAP,
					new Object[] { bapid, bapcode, rsPPD.getObject("ppd_currency"), rate, rsPPD.getObject("ppd_auditamount"),
							rsPPD.getObject("ppd_vendcode"), rsPPD.getObject("ppd_vendname"), rsPPD.getObject("pp_remark"),
							BaseUtil.getLocalMessage("ENTERING"), "ENTERING", employee.getEm_name(), employee.getEm_name(), "未领取", "未寄出",
							thisamount, thisamount, rsPPD.getObject("ppd_currency"), rsPPD.getObject("pp_code"),
							rsPPD.getObject("pp_type"), rsPPD.getObject("ppd_id"), 0, thisamount, rsPPD.getObject("pp_paymentcode"), "未付款" });
			if (bool) {
				execute("update BILLAP set bap_checkcode='" + checkno + "', bap_date=to_date('" + date + "','yyyy-mm-dd') where bap_id="
						+ bapid);
				updateByCondition("PayPleaseDetail", "ppd_account=nvl(ppd_account,0) + " + amount, "ppd_id=" + ppd_id);
				updateByCondition("PayPleaseDetail", "ppd_statuscode='PARTBA',ppd_status='" + BaseUtil.getLocalMessage("PARTBA") + "'",
						"ppd_id=" + ppd_id + " and ppd_account < ppd_applyamount AND NVL(ppd_account,0)>0");
				updateByCondition("PayPleaseDetail", "ppd_statuscode='TURNBA',ppd_status='" + BaseUtil.getLocalMessage("TURNBA") + "'",
						"ppd_id=" + ppd_id + " and ppd_account = ppd_applyamount AND NVL(ppd_account,0)>0");
				updateByCondition("PayPlease", "pp_paystatuscode='PAYMENTED',pp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED") + "'",
						"pp_id in (select ppd_ppid from PayPleaseDetail where ppd_account = ppd_applyamount AND NVL(ppd_account,0)>0 and ppd_id="
								+ ppd_id + ")");
				updateByCondition("PayPlease", "pp_paystatuscode='PARTPAYMENT',pp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT")
						+ "'",
						"pp_id in (select ppd_ppid from PayPleaseDetail where ppd_account < ppd_applyamount AND NVL(ppd_account,0)>0 and ppd_id="
								+ ppd_id + ")");
				JSONObject j = new JSONObject();
				j.put("bap_id", bapid);
				j.put("bap_code", bapcode);
				return j;
			}
		}
		return null;
	}

	@Override
	public JSONObject turnBillARChange(int ppd_id, String sourcecode, String type, Object amount, String date, String checkno) {
		// 根据ppd_id 查找PayPleaseDetail 表中数据
		SqlRowList rsPPD = queryForRowSet(TURNPAYPLEASEDETAIL, ppd_id);
		Double thisamount = Double.parseDouble(amount.toString());
		if (rsPPD.next()) {
			Employee employee = SystemSession.getUser();
			if (Math.abs(thisamount)
					- (Math.abs(rsPPD.getGeneralDouble("ppd_applyamount")) - Math.abs(rsPPD.getGeneralDouble("ppd_account"))) > 0.01) {
				BaseUtil.showError("本次付款金额大于剩余未付金额!");
			}
			int brcid = getSeqId("BILLARCHANGE_SEQ");
			String brccode = sGetMaxNumber("BillARChange", 2);
			boolean bool = execute(
					INSERTBILLARCHANGE,
					new Object[] { brcid, brccode, rsPPD.getObject("pp_type"), BaseUtil.getLocalMessage("ENTERING"), thisamount,
							rsPPD.getObject("ppd_vendcode"), rsPPD.getObject("ppd_vendname"), rsPPD.getObject("ppd_currency"), 0,
							rsPPD.getObject("ppd_currency"), 1, thisamount, employee.getEm_name(), rsPPD.getObject("pp_code"),
							rsPPD.getObject("ppd_id"), rsPPD.getObject("pp_remark") });
			if (bool) {
				execute("update BILLARCHANGE set brc_cmcurrency=brc_currency,brc_cmrate=1 where brc_id=" + brcid);
				execute("update BILLARCHANGE set brc_date=to_date('" + date + "','yyyy-mm-dd') where brc_id=" + brcid);
				execute("update BILLARCHANGE set brc_rate=(select cm_crrate from currencysmonth where brc_currency=cm_crname and cm_yearmonth=to_char(brc_date,'yyyymm')) where brc_id="
						+ brcid);
				updateByCondition("PayPleaseDetail", "ppd_account=nvl(ppd_account,0) + " + amount, "ppd_id=" + ppd_id);
				updateByCondition("PayPleaseDetail", "ppd_statuscode='PARTBARC',ppd_status='" + BaseUtil.getLocalMessage("PARTBARC") + "'",
						"ppd_id=" + ppd_id + " and ppd_account < ppd_applyamount AND NVL(ppd_account,0)>0");
				updateByCondition("PayPleaseDetail", "ppd_statuscode='TURNBARC',ppd_status='" + BaseUtil.getLocalMessage("TURNBARC") + "'",
						"ppd_id=" + ppd_id + " and ppd_account = ppd_applyamount AND NVL(ppd_account,0)>0");
				updateByCondition("PayPlease", "pp_paystatuscode='PAYMENTED',pp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED") + "'",
						"pp_id in (select ppd_ppid from PayPleaseDetail where round(ppd_account,2) = round(ppd_applyamount,2) AND NVL(ppd_account,0)>0 and ppd_id="
								+ ppd_id + ")");
				updateByCondition("PayPlease", "pp_paystatuscode='PARTPAYMENT',pp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT")
						+ "'",
						"pp_id in (select ppd_ppid from PayPleaseDetail where round(ppd_account,2) < round(ppd_applyamount,2) AND NVL(ppd_account,0)>0 and ppd_id="
								+ ppd_id + ")");
				JSONObject j = new JSONObject();
				j.put("brc_id", brcid);
				j.put("brc_code", brccode);
				return j;
			}
		}
		return null;
	}

	@Override
	// 更新付款申请
	public void updateDetailAmount(Object pp_code) {
		execute("update paypleasedetaildet set ppdd_turnamount=nvl((select nvl(amount,0) from (select sum(pbd_nowbalance) amount ,pbd_ordercode from PayBalanceDetail,PayBalance where pbd_pbid=pb_id and pb_kind in ('付款单','冲应付款') and pb_ppcode='"
				+ pp_code
				+ "' group by pbd_ordercode) where ppdd_billcode=pbd_ordercode),0)+nvl((select sum(ard_nowbalance) from AccountRegisterDetail,AccountRegister where ar_id=ard_arid and ard_orderid=ppdd_id and ar_sourcetype ='付款申请' and ar_source='"
				+ pp_code
				+ "' and ar_statuscode<>'POSTED'),0) where exists (select 1 from paypleasedetail,payplease where ppd_ppid=pp_id and ppdd_ppdid=ppd_id and pp_code='"
				+ pp_code + "')");
		execute("update paypleasedetail set ppd_account=nvl((select sum(bap_topaybalance) from BillAP,payplease where ppd_ppid=pp_id and BAP_PAYBILLCODE=pp_code),0)"
				+ "+nvl((select sum(brc_cmamount) from BillARChange,payplease where ppd_ppid=pp_id and brc_ppcode=pp_code),0)"
				+ "+nvl((select sum(ar_apamount) from AccountRegister,payplease where ppd_ppid=pp_id and ar_sourcetype='付款申请' and ar_source=pp_code),0)"
				+ "+nvl((select sum(pb_apamount) from PayBalance,payplease where ppd_ppid=pp_id and pb_source='付款申请' and pb_sourcecode=pp_code),0) "
				+ "where ppd_ppid in (select pp_id from payplease where pp_code='" + pp_code + "')");
		execute("update PayPlease set pp_paystatuscode='PAYMENTED',pp_paystatus='已付款' where pp_code='"
				+ pp_code
				+ "' and exists (select 1 from PayPleaseDetail where ppd_ppid=pp_id and round(nvl(ppd_account,0),2)=round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0)");
		execute("update PayPlease set pp_paystatuscode='PARTPAYMENT',pp_paystatus='部分付款' where pp_code='"
				+ pp_code
				+ "' and exists (select 1 from PayPleaseDetail where ppd_ppid=pp_id and round(nvl(ppd_account,0),2)<round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0)");
		execute("update PayPleaseDetail set ppd_status = null,ppd_statuscode = null where nvl(ppd_account,0)=0"
				+ " and exists (select 1 from PayPlease where ppd_ppid=pp_id and pp_code='" + pp_code + "')");
		execute("update PayPlease set pp_paystatuscode='UNPAYMENT',pp_paystatus='未付款' where pp_code='" + pp_code
				+ "' and exists (select 1 from PayPleaseDetail where ppd_ppid=pp_id and nvl(ppd_account,0)=0)");
	}

	@Override
	// 更新预付款申请
	public void updateDetailAmountYF(Object pp_code) {
		execute("update paypleasedetaildet set ppdd_turnamount=nvl((select nvl(amount,0) from (select sum(ppd_nowbalance) amount,ppd_ordercode from PrePayDetail,PrePay where ppd_ppid=pp_id and pp_pleasecode='"
				+ pp_code
				+ "' and nvl(ppd_ordercode,' ')<>' ' group by ppd_ordercode) where ppdd_pucode=ppd_ordercode),0)"
				+ "+nvl((select nvl(amount,0) from (select sum(ppd_nowbalance) amount,ppd_makecode from PrePayDetail,PrePay where ppd_ppid=pp_id and pp_pleasecode='"
				+ pp_code
				+ "' and nvl(ppd_makecode,' ')<>' ' group by ppd_makecode) where ppdd_makecode=ppd_makecode),0)"
				+ "+nvl((select sum(ard_nowbalance) from AccountRegisterDetail,AccountRegister where ar_id=ard_arid and ard_ppddid=ppdd_id and ar_sourcetype ='付款申请' and ar_source='"
				+ pp_code
				+ "' and ar_statuscode<>'POSTED'),0) where exists (select 1 from paypleasedetail,payplease where ppd_ppid=pp_id and ppdd_ppdid=ppd_id and pp_code='"
				+ pp_code + "')");
		execute("update paypleasedetail set ppd_account=nvl((select sum(bap_topaybalance) from BillAP,payplease where ppd_ppid=pp_id and BAP_PAYBILLCODE=pp_code),0)"
				+ "+nvl((select sum(brc_cmamount) from BillARChange,payplease where ppd_ppid=pp_id and brc_ppcode=pp_code),0)"
				+ "+nvl((select sum(ar_apamount) from AccountRegister,payplease where ppd_ppid=pp_id and ar_sourcetype='付款申请' and ar_source=pp_code),0)"
				+ "+nvl((select sum(pp_jsamount) from PrePay where pp_source='预付款申请' and pp_sourceid=ppd_id),0)"
				+ "where ppd_ppid in (select pp_id from payplease where pp_code='" + pp_code + "')");
		execute("update PayPlease set pp_paystatuscode='PAYMENTED',pp_paystatus='已付款' where pp_code='"
				+ pp_code
				+ "' and exists (select 1 from PayPleaseDetail where ppd_ppid=pp_id and round(nvl(ppd_account,0),2)=round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0)");
		execute("update PayPlease set pp_paystatuscode='PARTPAYMENT',pp_paystatus='部分付款' where pp_code='"
				+ pp_code
				+ "' and exists (select 1 from PayPleaseDetail where ppd_ppid=pp_id and round(nvl(ppd_account,0),2)<round(nvl(ppd_applyamount,0),2) and nvl(ppd_account,0)>0)");
		execute("update PayPleaseDetail set ppd_status = null,ppd_statuscode = null where nvl(ppd_account,0)=0"
				+ " and exists (select 1 from PayPlease where ppd_ppid=pp_id and pp_code='" + pp_code + "')");
		execute("update PayPlease set pp_paystatuscode='UNPAYMENT',pp_paystatus='未付款' where pp_code='" + pp_code
				+ "' and exists (select 1 from PayPleaseDetail where ppd_ppid=pp_id and nvl(ppd_account,0)=0)");
	}

	@Override
	public JSONObject turnPayBalanceCYF(int ppd_id, String sourcecode, String type, Object amount, String date, String checkno) {
		// 根据ppd_id 查找PayPleaseDetail 表中数据
		SqlRowList rsPPD = queryForRowSet(TURNPAYPLEASEDETAIL, ppd_id);
		Double thisamount = Double.parseDouble(amount.toString());
		if (rsPPD.next()) {
			Employee employee = SystemSession.getUser();
			if (Math.abs(thisamount)
					- (Math.abs(rsPPD.getGeneralDouble("ppd_applyamount")) - Math.abs(rsPPD.getGeneralDouble("ppd_account"))) > 0.01) {
				BaseUtil.showError("本次付款金额大于剩余未付金额!");
			}
			int pb_id = getSeqId("PAYBALANCE_SEQ");
			String code = sGetMaxNumber("PayBalance", 2);
			boolean bool = execute(
					INSERTPAYBALANCE,
					new Object[] { pb_id, code, "冲应付款", rsPPD.getObject("ppd_vendcode"), rsPPD.getObject("ppd_vendname"),
							rsPPD.getObject("ppd_paymethodid"), rsPPD.getObject("ppd_paymethod"), rsPPD.getObject("ppd_currency"), 0,
							rsPPD.getObject("pp_paymentcode"), rsPPD.getObject("pp_payment"), thisamount, thisamount, 0, "付款申请",
							rsPPD.getObject("pp_code"), BaseUtil.getLocalMessage("UNPOST"), BaseUtil.getLocalMessage("ENTERING"),
							rsPPD.getObject("pp_id"), thisamount, rsPPD.getObject("ppd_currency"), 1, thisamount, employee.getEm_name(),
							employee.getEm_id(), rsPPD.getObject("pp_code"), rsPPD.getObject("ppd_bankname"),
							rsPPD.getObject("ppd_bankaccount"), rsPPD.getObject("pp_payremark"), rsPPD.getObject("pp_remark"),
							rsPPD.getObject("ppd_id") });
			if (bool) {
				execute("update paybalance set (pb_vendid,pb_buyer)=(select ve_id,ve_buyername from vendor where pb_vendcode=ve_code) where pb_id="
						+ pb_id);
				execute("update paybalance set (pb_cateid,pb_catetype,pb_currency,pb_catename)=(select ca_id,ca_typename,ca_currency,ca_name from category where pb_catecode=ca_code) where pb_id="
						+ pb_id);
				execute("update paybalance set pb_date=to_date('" + date + "','yyyy-mm-dd') where pb_id=" + pb_id);
				execute("update paybalance set pb_rate=(select cm_crrate from currencysmonth where pb_currency=cm_crname and cm_yearmonth=to_char(pb_date,'yyyymm')) where pb_id="
						+ pb_id);
				SqlRowList rsPPDD = queryForRowSet(FINDPAYPLEASEDETAILDET, ppd_id);
				if (thisamount < rsPPD.getGeneralDouble("ppd_applyamount")) {
					String res = callProcedure("CT_CATCHABTOPBCYF", new Object[] { ppd_id, thisamount, pb_id });
					if (!("ok").equals(res.trim())) {
						BaseUtil.showError(res);
					}
				} else {
					int detno = 1;
					while (rsPPDD.next()) {
						execute(INSERTPAYBALANCEDETAIL,
								new Object[] { detno++, rsPPDD.getObject("ppdd_billcode"), rsPPDD.getObject("ppdd_currency"),
										rsPPDD.getGeneralDouble("ppdd_billamount"), rsPPDD.getGeneralDouble("ab_payamount"),
										rsPPDD.getGeneralDouble("ppdd_thisapplyamount"), code, pb_id, rsPPDD.getObject("ppdd_catecode"),
										rsPPDD.getObject("ppdd_id") });
					}
				}
				execute("update paybalancedetail set (pbd_orderdate,pbd_planpaydate,pbd_duedate,pbd_ordertype,pbd_buyerid,pbd_buyer)=(select ab_date,ab_planpaydate,ab_paydate,ab_class,ab_buyerid,ab_buyer from apbill where ab_code=pbd_ordercode) where nvl(pbd_ordercode,' ')<>' ' and pbd_pbid="
						+ pb_id);
				updateByCondition("paybalancedetail", "pbd_cateid=(select ca_id from category where ca_code=pbd_catecode)", "pbd_pbid="
						+ pb_id + " and nvl(pbd_catecode,' ')<>' '");
				updateByCondition("PayPleaseDetail", "ppd_account=nvl(ppd_account,0) + " + amount, "ppd_id=" + ppd_id);
				updateByCondition("PayPlease", "pp_paystatuscode='PAYMENTED',pp_paystatus='" + BaseUtil.getLocalMessage("PAYMENTED") + "'",
						"pp_id in (select ppd_ppid from PayPleaseDetail where round(ppd_account,2) = round(ppd_applyamount,2) AND NVL(ppd_account,0)>0 and ppd_id="
								+ ppd_id + ")");
				updateByCondition("PayPlease", "pp_paystatuscode='PARTPAYMENT',pp_paystatus='" + BaseUtil.getLocalMessage("PARTPAYMENT")
						+ "'",
						"pp_id in (select ppd_ppid from PayPleaseDetail where round(ppd_account,2) < round(ppd_applyamount,2) AND NVL(ppd_account,0)>0 and ppd_id="
								+ ppd_id + ")");
				JSONObject j = new JSONObject();
				j.put("pb_id", pb_id);
				j.put("pb_code", code);
				return j;
			}
		}
		return null;
	}
}
