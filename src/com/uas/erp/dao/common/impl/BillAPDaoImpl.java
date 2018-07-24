package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BillAPDao;
import com.uas.erp.model.Employee;

@Repository
public class BillAPDaoImpl extends BaseDao implements BillAPDao {
	static final String TURNBILLAP = "SELECT * FROM BillAP WHERE bap_id=?";
	static final String TURNPAYPLEASEYF = "SELECT * FROM BillAP left join PayPlease on pp_code=bap_paybillcode"
			+ " left join PayPleaseDetail on pp_id=ppd_ppid left join PayPleaseDetailDet on ppdd_ppdid=ppd_id"
			+ " left join Purchase on ppdd_pucode=pu_code WHERE bap_id=?";
	static final String TURNPAYPLEASE = "SELECT * FROM BillAP left join PayPlease on pp_code=bap_paybillcode"
			+ " left join PayPleaseDetail on pp_id=ppd_ppid left join PayPleaseDetailDet on ppdd_ppdid=ppd_id"
			+ " left join APBill on ppdd_billcode=ab_code WHERE bap_id=?";
	static final String INSERTPAYBALANCE = "INSERT INTO PayBalance(pb_id,pb_code,pb_source,pb_sourceid,pb_vendcode,pb_date"
			+ ",pb_currency,pb_rate,pb_bank,pb_recorddate,pb_billcode,pb_billdate,pb_vendid,pb_vendname,pb_recorder,pb_vmstatus"
			+ ",pb_vmstatuscode,pb_printstatus,pb_printstatuscode,pb_recorderid,pb_auditstatus,pb_auditstatuscode,pb_status"
			+ ",pb_statuscode,pb_kind,pb_amount,pb_vmcurrency,pb_vmrate,pb_vmamount,pb_remark,pb_sourcecode,pb_apamount,pb_catecode,pb_refno) VALUES (?,?,'应付票据',?,?,?"
			+ ",?,?,?,sysdate,?,?,?,?,?,?,'UNSTRIKE',?,'UNPRINT',?,?,'ENTERING',?,'UNPOST','付款单',?,?,?,?,?,?,?,?,?)";
	static final String INSERTPAYBALANCEDETAIL = "INSERT INTO PayBalanceDetail(pbd_id,pbd_pbid,pbd_detno,pbd_cateid,pbd_catecode,pbd_currency"
			+ ",pbd_nowbalance,pbd_bapid,pbd_code) VALUES (PAYBALANCEDETAIL_SEQ.Nextval,?,?,0,?,?,?,?,?)";
	static final String INSERTPREPAY = "INSERT INTO PrePay(pp_id,pp_code,pp_source,pp_sourceid,pp_vendcode,pp_date"
			+ ",pp_currency,pp_indate,pp_vendname,pp_recorder,pp_recorderid,pp_vmstatus"
			+ ",pp_vmstatuscode,pp_printstatus,pp_printstatuscode,pp_auditstatus,pp_auditstatuscode,pp_status"
			+ ",pp_statuscode,pp_type,pp_buyerid,pp_buyer,pp_accountcode,pp_accountname,pp_accountcurrency,pp_vendid"
			+ ",pp_amount,pp_vmcurrency,pp_vmrate,pp_vmamount,pp_remark,pp_sourcecode,pp_jsamount,pp_refno,pp_pleasecode)"
			+ " VALUES (?,?,'应付票据',?,?,?,?,sysdate,?,?,?,?,"
			+ "'UNSTRIKE',?,'UNPRINT',?,'ENTERING',?,'UNPOST','预付款',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTPREPAYDETAIL = "INSERT INTO PrePayDetail(ppd_id,ppd_ppid,ppd_detno,ppd_cateid,ppd_catecode,ppd_currency"
			+ ",ppd_nowbalance,ppd_bapid,PPD_CODE) VALUES (PREPAYDETAIL_SEQ.Nextval,?,?,0,?,?,?,?,?)";
	static final String INSERTPAYBALANCEASS = "INSERT INTO PAYBALANCEASS(ASS_ID,ASS_CONID,ASS_ASSNAME,ASS_CODEFIELD,ASS_NAMEFIELD,ASS_ASSTYPE)"
			+ " VALUES (PAYBALANCEASS_SEQ.Nextval,?,'供应商往来',?,?,'Vend')";
	static final String INSERTPREPAYASS = "INSERT INTO PREPAYASS(ASS_ID,ASS_CONID,ASS_ASSNAME,ASS_CODEFIELD,ASS_NAMEFIELD,ASS_ASSTYPE)"
			+ " VALUES (PREPAYASS_SEQ.Nextval,?,'供应商往来',?,?,'Vend')";
	final static String INSERT_PREPAYDETAIL = "INSERT INTO PREPAYDETAIL(ppd_id, ppd_ppid, PPD_CODE, PPD_DETNO, PPD_ORDERTYPE, ppd_ordercode,"
			+ " PPD_ORDERAMOUNT,PPD_HAVEBALANCE, PPD_NOWBALANCE, PPD_REMARK, PPD_NOWPREBALANCE, PPD_DATE, ppd_buyerid,"
			+ " ppd_buyer, ppd_catecode, ppd_currency, ppd_makecode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	final static String INSERT_PAYBALANCEDETAIL = "INSERT INTO PAYBALANCEDETAIL(PBD_ID, PBD_PBID, PBD_CODE, PBD_DETNO, PBD_ORDERTYPE, "
			+ "PBD_ORDERCODE, PBD_ORDERAMOUNT, PBD_REMAINAMOUNT, PBD_NOWBALANCE, PBD_REMARK, PBD_PLANPAYDATE, PBD_SOURCE, PBD_CATECODE,"
			+ "PBD_HAVEPAY, PBD_CURRENCY, PBD_ORDERDATE, PBD_BUYER) values (PAYBALANCEDETAIL_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public String turnPayBalance(int id) {
		SqlRowList rs = queryForRowSet(TURNBILLAP, new Object[] { id });
		int pbid = 0;
		Object ppd_id = null;
		Double thisamount = 0.0;
		StringBuffer sb = new StringBuffer();
		if (rs.next()) {
			Employee employee = SystemSession.getUser();
			pbid = getSeqId("PAYBALANCE_SEQ");
			String code = sGetMaxNumber("PayBalance", 2);
			Object vendid = getFieldDataByCondition("Vendor", "ve_id", "ve_code='" + rs.getObject("bap_vendcode") + "'");
			Object billdate = getFieldDataByCondition("APBill", "ab_date", "ab_code='" + rs.getObject("bap_paybillcode") + "'");
			execute(INSERTPAYBALANCE,
					new Object[] { pbid, code, id, rs.getObject("bap_vendcode"), rs.getObject("bap_date"), rs.getObject("bap_currency"),
							rs.getObject("bap_rate"), rs.getObject("bap_bank"), rs.getObject("bap_paybillcode"), billdate, vendid,
							rs.getObject("bap_vendname"), employee.getEm_name(), BaseUtil.getLocalMessage("UNSTRIKE"),
							BaseUtil.getLocalMessage("UNPRINT"), employee.getEm_id(), BaseUtil.getLocalMessage("ENTERING"),
							BaseUtil.getLocalMessage("UNPOST"), rs.getObject("bap_doublebalance"), rs.getObject("bap_cmcurrency"),
							rs.getObject("bap_cmrate"), rs.getObject("bap_topaybalance"), rs.getObject("bap_remark"),
							rs.getObject("bap_code"), rs.getGeneralDouble("bap_topaybalance") - rs.getGeneralDouble("bap_feeamount"),
							rs.getObject("bap_othercatecode"), rs.getObject("bap_checkcode") });
			execute("update PAYBALANCE set pb_catename=(select ca_name from Category where ca_code=pb_catecode) where pb_id=" + pbid);
			execute("update paybalance set pb_ppcode=(select bap_paybillcode from BillAP where nvl(bap_paybillcode,' ')<>' ' and bap_billkind='应付款' and PB_SOURCECODE=bap_code) where PB_SOURCE='应付票据' and nvl(pb_ppcode,' ')=' ' and pb_id="
					+ pbid);
			execute("update paybalance set pb_apamount =(select round(sum(pbd_nowbalance),2) from PayBalanceDetail where nvl(pbd_ordercode,' ') <>' ' and pbd_pbid=pb_id) where pb_id="
					+ pbid);
			execute(INSERTPAYBALANCEASS, new Object[] { pbid, rs.getObject("bap_vendcode"), rs.getObject("bap_vendname") });
			ppd_id = rs.getObject("bap_ppdid");
			if (ppd_id != null && Integer.parseInt(ppd_id.toString()) != 0) {
				thisamount = rs.getDouble("bap_topaybalance");
				String res = callProcedure("CT_CATCHPPDDTOPBDSPLIT", new Object[] { ppd_id, thisamount, pbid });
				if (!("ok").equals(res.trim())) {
					BaseUtil.showError(res);
				}
			}
			int detno = 1;
			SqlRowList rs1 = queryForRowSet("SELECT max(pbd_detno) from paybalancedetail where pbd_pbid=?", new Object[] { pbid });
			if (rs1.next()) {
				detno = rs1.getGeneralInt(1) + 1;
			}
			execute("update paybalance set pb_apamount=nvl((select sum(nvl(pbd_nowbalance,0)) from paybalancedetail where pbd_pbid=pb_id and nvl(pbd_ordercode,' ')<>' '),0) where pb_id="
					+ pbid);
			if (rs.getObject("bap_feecatecode") != null && !"".equals(rs.getObject("bap_feecatecode"))) {
				execute(INSERTPAYBALANCEDETAIL, new Object[] { pbid, detno, rs.getObject("bap_feecatecode"),
						rs.getObject("bap_cmcurrency"), rs.getGeneralDouble("bap_feeamount"), rs.getObject("bap_id"), code });
				execute("update PAYBALANCEDETAIL set pbd_cateid=(select ca_id from category where ca_code=pbd_catecode) where pbd_pbid="
						+ pbid + " and nvl(pbd_catecode,' ')<>' '");
			}
			execute("update billap set bap_checkno='" + code + "' where bap_id=" + id);
			sb.append("转入成功,付款单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/paybalance.jsp?formCondition=pb_idIS" + pbid
					+ "&gridCondition=pbd_pbidIS" + pbid + "&whoami=PayBalance')\">" + code + "</a>&nbsp;");
		}
		return sb.toString();
	}

	@Override
	public String turnPrePay(int id) {
		SqlRowList rs = queryForRowSet(TURNBILLAP, new Object[] { id });
		int pp_id = 0;
		Object ppd_id = null;
		Double thisamount = 0.0;
		StringBuffer sb = new StringBuffer();
		if (rs.next()) {
			Employee employee = SystemSession.getUser();
			pp_id = getSeqId("PREPAY_SEQ");
			String code = sGetMaxNumber("PrePay", 2);
			Object[] vend = getFieldsDataByCondition("vendor", new String[] { "ve_buyerid", "ve_buyername", "ve_id" },
					"ve_code='" + rs.getObject("bap_vendcode") + "'");
			Object[] bank = getFieldsDataByCondition("Category", new String[] { "ca_name", "ca_currency" },
					"ca_code='" + rs.getObject("bap_othercatecode") + "'");
			execute(INSERTPREPAY,
					new Object[] { pp_id, code, id, rs.getObject("bap_vendcode"), rs.getObject("bap_date"), rs.getObject("bap_currency"),
							rs.getObject("bap_vendname"), employee.getEm_name(), employee.getEm_id(), BaseUtil.getLocalMessage("UNSTRIKE"),
							BaseUtil.getLocalMessage("UNPRINT"), BaseUtil.getLocalMessage("ENTERING"), BaseUtil.getLocalMessage("UNPOST"),
							vend[0], vend[1], rs.getObject("bap_othercatecode"), bank[0], bank[1], vend[2],
							rs.getObject("bap_doublebalance"), rs.getObject("bap_cmcurrency"), rs.getObject("bap_cmrate"),
							rs.getObject("bap_topaybalance"), rs.getObject("bap_remark"), rs.getObject("bap_code"),
							rs.getGeneralDouble("bap_topaybalance") - rs.getGeneralDouble("bap_feeamount"), rs.getObject("bap_checkcode"),
							rs.getObject("BAP_PAYBILLCODE") });
			execute(INSERTPREPAYASS, new Object[] { pp_id, rs.getObject("bap_vendcode"), rs.getObject("bap_vendname") });
			ppd_id = rs.getObject("bap_ppdid");
			if (ppd_id != null && Integer.parseInt(ppd_id.toString()) != 0) {
				thisamount = rs.getDouble("bap_topaybalance");
				String res = callProcedure("CT_CATCHPPDDTOPPDSPLIT", new Object[] { ppd_id, thisamount, pp_id });
				if (!("ok").equals(res.trim())) {
					BaseUtil.showError(res);
				}
			}
			int count = 1;
			SqlRowList rs1 = queryForRowSet("SELECT max(ppd_detno) from PREPAYDETAIL where ppd_ppid=?", new Object[] { pp_id });
			if (rs1.next()) {
				count = rs1.getGeneralInt(1) + 1;
			}
			if (rs.getObject("bap_feecatecode") != null && !"".equals(rs.getObject("bap_feecatecode"))) {
				execute(INSERTPREPAYDETAIL, new Object[] { pp_id, count, rs.getObject("bap_feecatecode"), rs.getObject("bap_cmcurrency"),
						rs.getGeneralDouble("bap_feeamount"), rs.getObject("bap_id"), code });
				execute("update PREPAYDETAIL set ppd_cateid=(select ca_id from category where ca_code=ppd_catecode) where ppd_ppid="
						+ pp_id + " and nvl(ppd_catecode,' ')<>' '");
			}
			execute("update billap set bap_checkno='" + code + "' where bap_id=" + id);
			execute("update prepaydetail set ppd_bapid=" + id + " where ppd_ppid=" + pp_id);
			execute("update prepaydetail set (PPD_ORDERTYPE,ppd_date,ppd_delivery,ppd_buyer,ppd_buyerid)=(select '委外单',ma_date,ma_planenddate,ve_buyername,ve_buyerid from make left join vendor on ve_code=ma_vendcode where ppd_makecode=ma_code) where ppd_ppid="
					+ pp_id + " and nvl(ppd_makecode,' ')<>' '");
			sb.append("转入成功,预付单号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/prepay.jsp?formCondition=pp_idIS" + pp_id
					+ "&gridCondition=ppd_ppidIS" + pp_id + "&whoami=PrePay!Arp!PAMT')\">" + code + "</a>&nbsp;");
		}
		return sb.toString();
	}
}
