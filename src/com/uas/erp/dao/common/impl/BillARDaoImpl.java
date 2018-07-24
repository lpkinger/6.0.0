package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BillARDao;

@Repository
public class BillARDaoImpl extends BaseDao implements BillARDao {
	static final String TURNBILLAR = "SELECT * FROM BillAR WHERE bar_id=?";
	static final String INSERTRECBALANCE = "INSERT INTO RecBalance(rb_id,rb_code,rb_source,rb_sourceid,rb_custcode,rb_date"
			+ ",rb_currency,rb_rate,rb_recorddate,rb_billcode,rb_billdate,rb_custid,rb_custname,rb_emname,rb_strikestatus"
			+ ",rb_strikestatuscode,rb_printstatus,rb_printstatuscode,rb_emid,rb_auditstatus,rb_auditstatuscode,rb_status"
			+ ",rb_statuscode,rb_kind,rb_sellerid,rb_seller,rb_banknoname,rb_amount,rb_cmcurrency,rb_cmrate,rb_cmamount"
			+ ",rb_bankno,rb_catename,rb_remark,rb_sourcecode,rb_aramount,rb_departmentcode,rb_departmentname) VALUES (?,?,'应收票据',?,?,?,?,?,sysdate,?,?,?,?,?,"
			+ "?,?,?,?,?,?,?,?,?,'收款单',?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTRECBALANCEDETAIL = "INSERT INTO RecBalanceDetail(rbd_id,rbd_rbid,rbd_detno,rbd_cateid,rbd_catecode,rbd_currency"
			+ ",rbd_nowbalance,rbd_barid) VALUES (RECBALANCEDETAIL_SEQ.Nextval,?,?,0,?,?,?,?)";
	static final String INSERTPREREC = "INSERT INTO PreRec(pr_id,pr_code,pr_source,pr_sourceid,pr_custcode,pr_date"
			+ ",pr_currency,pr_rate,pr_indate,pr_custname,pr_recorder,pr_recorderid,pr_cmstatus"
			+ ",pr_cmstatuscode,pr_printstatus,pr_printstatuscode,pr_auditstatus,pr_auditstatuscode,pr_status"
			+ ",pr_statuscode,pr_kind,pr_sellerid,pr_seller,pr_accountcode,pr_accountname,pr_sellercode,pr_amount,"
			+ "pr_cmcurrency,pr_cmrate,pr_cmamount,pr_remark,pr_sourcecode,pr_jsamount,pr_departmentcode,pr_departmentname) "
			+ "VALUES (?,?,'应收票据',?,?,?,?,?,sysdate,?,?,?,?,'UNSTRIKE',?,'UNPRINT',?,'ENTERING',?,'UNPOST','预收款',?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTPRERECDETAIL = "INSERT INTO PreRecDetail(prd_id,prd_prid,prd_detno,prd_cateid,prd_catecode,prd_currency"
			+ ",prd_nowbalance,prd_barid,prd_code) VALUES (PRERECDETAIL_SEQ.Nextval,?,?,0,?,?,?,?,?)";
	static final String INSERTRecBalanceAss = "INSERT INTO RecBalanceAss(ASS_ID,ASS_CONID,ASS_ASSNAME,ASS_CODEFIELD,ASS_NAMEFIELD,ASS_ASSTYPE)"
			+ " VALUES (RECBALANCEASS_SEQ.Nextval,?,'客户往来',?,?,'Cust')";
	static final String INSERTPRERECASS = "INSERT INTO PRERECASS(ASS_ID,ASS_CONID,ASS_ASSNAME,ASS_CODEFIELD,ASS_NAMEFIELD,ASS_ASSTYPE)"
			+ " VALUES (PRERECASS_SEQ.Nextval,?,'客户往来',?,?,'Cust')";

	@Override
	public String turnRecBalance(int id) {
		SqlRowList rs = queryForRowSet(TURNBILLAR, new Object[] { id });
		int rb_id = 0;
		Object sourceid = null;
		Object sourcetype = null;
		StringBuffer sb = new StringBuffer();
		if (rs.next()) {
			rb_id = getSeqId("RECBALANCE_SEQ");
			String code = sGetMaxNumber("RecBalance", 2);
			Object cust = getFieldDataByCondition("customer", "cu_id", "cu_code='" + rs.getObject("bar_custcode") + "'");
			Object em = getFieldDataByCondition("employee", "em_id", "em_code='" + rs.getObject("bar_sellercode") + "'");
			Object catename = getFieldDataByCondition("Category", "ca_name", "ca_code='" + rs.getObject("bar_othercatecode") + "'");
			Object billdate = getFieldDataByCondition("APBill", "ab_date", "ab_code='" + rs.getObject("bar_paybillcode") + "'");
			execute(INSERTRECBALANCE,
					new Object[] { rb_id, code, id, rs.getObject("bar_custcode"), rs.getObject("bar_date"), rs.getObject("bar_currency"),
							rs.getObject("bar_rate"), rs.getObject("bar_paybillcode"), billdate, cust, rs.getObject("bar_custname"),
							SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("UNSTRIKE"), "UNSTRIKE",
							BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT", SystemSession.getUser().getEm_id(),
							BaseUtil.getLocalMessage("ENTERING"), "ENTERING", BaseUtil.getLocalMessage("UNPOST"), "UNPOST", em,
							rs.getObject("bar_seller"), rs.getObject("bar_bankaccount"), rs.getObject("bar_doublebalance"),
							rs.getObject("bar_cmcurrency"), rs.getObject("bar_cmrate"), rs.getObject("bar_topaybalance"),
							rs.getObject("bar_othercatecode"), catename, rs.getObject("bar_remark"), rs.getObject("bar_code"),
							rs.getGeneralDouble("bar_topaybalance") - rs.getGeneralDouble("bar_feeamount"), rs.getObject("bar_departcode"),
							rs.getObject("bar_departname") });
			sourceid = rs.getObject("bar_sourceid");
			sourcetype = rs.getObject("bar_sourcetype");
			if (sourceid != null && Integer.parseInt(sourceid.toString()) != 0) {
				if ("回款通知单".equals(sourcetype)) {
					execute("insert into recbalanceDetail(RBD_ID,RBD_RBID,RBD_DETNO,rbd_ordercode,rbd_orderid,RBD_CURRENCY,rbd_cateid,rbd_catecode,rbd_invoicedate,rbd_aramount,RBD_ORDERAMOUNT,rbd_nowbalance,rbd_havepay,rbd_seller,rbd_sellerid,rbd_remark) "
							+ "select RECBALANCEDETAIL_SEQ.NEXTVAL, "
							+ rb_id
							+ ", RBD_DETNO,RBD_ABCODE,AB_ID,RBD_CURRENCY,RBD_CATEID,RBD_CATECODE,AB_DATE,ab_aramount,ab_aramount,RBD_AMOUNT,ab_payamount,RBD_SELLERNAME,EM_ID,RBD_REMARK FROM recbalancenoticedetail,ARBILL,EMPLOYEE "
							+ "WHERE RBD_ABCODE=AB_CODE AND RBD_SELLERCODE=EM_CODE AND RBD_RBID=" + sourceid);
				}
			}
			int detno = 1;
			SqlRowList rs1 = queryForRowSet("SELECT max(rbd_detno) from recbalancedetail where rbd_rbid=?", new Object[] { rb_id });
			if (rs1.next()) {
				detno = rs1.getGeneralInt(1) + 1;
			}
			if (rs.getObject("bar_feecatecode") != null && !"".equals(rs.getObject("bar_feecatecode"))) {
				execute(INSERTRECBALANCEDETAIL,
						new Object[] { rb_id, rs.getObject("bar_feecatecode"), detno, rs.getObject("bar_cmcurrency"),
								rs.getGeneralDouble("bar_feeamount"), rs.getObject("bar_id") });
				execute("update RECBALANCEDETAIL set rbd_cateid=(select ca_id from category where ca_code=rbd_catecode) where rbd_rbid="
						+ rb_id + " and nvl(rbd_catecode,' ')<>' '");
			}
			execute(INSERTRecBalanceAss, new Object[] { rb_id, rs.getObject("bar_custcode"), rs.getObject("bar_custname") });
			execute("update billar set bar_checkno='" + code + "' where bar_id=" + id);
			sb.append("转入成功,收款单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/recBalance.jsp?formCondition=rb_idIS" + rb_id
					+ "&gridCondition=rbd_rbidIS" + rb_id + "&whoami=RecBalance!PBIL')\">" + code + "</a>&nbsp;");
		}
		return sb.toString();
	}

	@Override
	public String turnPreRec(int id) {
		SqlRowList rs = queryForRowSet(TURNBILLAR, new Object[] { id });
		int pr_id = 0;
		Object sourceid = null;
		Object sourcetype = null;
		StringBuffer sb = new StringBuffer();
		boolean autoCommit = isDBSetting("BillAR", "autoCommitPreRec");
		if (rs.next()) {
			pr_id = getSeqId("PREREC_SEQ");
			String code = sGetMaxNumber("PreRec", 2);
			Object em = getFieldDataByCondition("employee", "em_id", "em_code='" + rs.getObject("bar_sellercode") + "'");
			Object bankname = getFieldDataByCondition("Category", "ca_name", "ca_code='" + rs.getObject("bar_othercatecode") + "'");
			execute(INSERTPREREC,
					new Object[] { pr_id, code, id, rs.getObject("bar_custcode"), rs.getObject("bar_date"), rs.getObject("bar_currency"),
							rs.getObject("bar_rate"), rs.getObject("bar_custname"), SystemSession.getUser().getEm_name(),
							SystemSession.getUser().getEm_id(), BaseUtil.getLocalMessage("UNSTRIKE"), BaseUtil.getLocalMessage("UNPRINT"),
							BaseUtil.getLocalMessage("ENTERING"), BaseUtil.getLocalMessage("UNPOST"), em, rs.getObject("bar_seller"),
							rs.getObject("bar_othercatecode"), bankname, rs.getObject("bar_sellercode"), rs.getObject("bar_doublebalance"),
							rs.getObject("bar_cmcurrency"), rs.getObject("bar_cmrate"), rs.getObject("bar_topaybalance"),
							rs.getObject("bar_remark"), rs.getObject("bar_code"),
							rs.getGeneralDouble("bar_topaybalance") - rs.getGeneralDouble("bar_feeamount"), rs.getObject("bar_departcode"),
							rs.getObject("bar_departname") });
			sourceid = rs.getObject("bar_sourceid");
			sourcetype = rs.getObject("bar_sourcetype");
			if (sourceid != null && Integer.parseInt(sourceid.toString()) != 0) {
				if ("回款通知单".equals(sourcetype)) {
					execute("insert into PreRecDetail(PRD_ID,PRD_CODE,PRD_PRID,PRD_DETNO,PRD_ORDERCODE,PRD_CURRENCY,prd_cateid,PRD_CATECODE,PRD_CATENAME,PRD_DATE,PRD_ORDERAMOUNT,PRD_NOWBALANCE,PRD_HAVEBALANCE,PRD_SELLER,PRD_SELLERID,PRD_REMARK,PRD_DELIVERY,PRD_PAYMENT) "
							+ "select PRERECDETAIL_SEQ.NEXTVAL, '"
							+ code
							+ "',"
							+ pr_id
							+ ", RBD_DETNO,RBD_SACODE,RBD_CURRENCY,RBD_CATEID,RBD_CATECODE,RBD_CATENAME,SA_DATE,sa_total,RBD_AMOUNT,sa_prepayamount,RBD_SELLERNAME,EM_ID,RBD_REMARK,sa_plandelivery,RBD_PAYMENTS FROM recbalancenoticedetail,sale,EMPLOYEE "
							+ "WHERE RBD_SACODE=SA_CODE AND RBD_SELLERCODE=EM_CODE AND RBD_RBID=" + sourceid);
				}
			}
			int detno = 1;
			SqlRowList rs1 = queryForRowSet("SELECT max(prd_detno) from PreRecDetail where prd_prid=?", new Object[] { pr_id });
			if (rs1.next()) {
				detno = rs1.getGeneralInt(1) + 1;
			}
			if (rs.getObject("bar_feecatecode") != null && !"".equals(rs.getObject("bar_feecatecode"))) {
				execute(INSERTPRERECDETAIL, new Object[] { pr_id, detno, rs.getObject("bar_feecatecode"), rs.getObject("bar_cmcurrency"),
						rs.getGeneralDouble("bar_feeamount"), rs.getObject("bar_id"), code });
				execute("update PRERECDETAIL set prd_cateid=(select ca_id from category where ca_code=prd_catecode) where prd_prid="
						+ pr_id + " and nvl(prd_catecode,' ')<>' '");
			}
			execute(INSERTPRERECASS, new Object[] { pr_id, rs.getObject("bar_custcode"), rs.getObject("bar_custname") });
			execute("update billar set bar_checkno='" + code + "' where bar_id=" + id);
			if (autoCommit) {
				String res = callProcedure("SP_COMMITEPREREC", new Object[] { code });
				if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
					BaseUtil.showError(res);
				}
			}
			sb.append("转入成功,预收单号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/preRec.jsp?formCondition=pr_idIS" + pr_id
					+ "&gridCondition=prd_pridIS" + pr_id + "&whoami=PreRec!Ars!DERE')\">" + code + "</a>&nbsp;");
		}
		return sb.toString();
	}

	static final String INSERTBILLARCHANGE = "INSERT INTO BILLARCHANGE(brc_id,brc_code,brc_date,brc_custcode,brc_custname"
			+ ",brc_catecode,brc_catename,brc_amount,brc_cucode,brc_cuname,brc_currency,brc_rate,brc_explain,brc_sourceid,brc_source,brc_recorder,"
			+ "brc_indate,brc_kind,brc_status,brc_statuscode,brc_sourcetype) "
			+ "VALUES (BILLARCHANGE_SEQ.Nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,'拆分','已审核','AUDITED','应收票据')";

	@Override
	public String splitDetail(int brs_id) {
		StringBuffer sb = new StringBuffer();
		Object[] bar = getFieldsDataByCondition("BillARSplit", new String[] { "brs_barid", "nvl(brs_amount,0)" }, "brs_id=" + brs_id);
		if (bar != null) {
			int brcid = getSeqId("BILLARCHANGE_SEQ");
			String brccode = sGetMaxNumber("BillARChange", 2);
			// 插入应收票据异动主表
			execute("INSERT INTO BILLARCHANGE(brc_id,brc_code,brc_date,brc_custcode,brc_custname,brc_catecode,brc_catename,brc_amount,brc_cucode,brc_cuname,"
					+ "brc_currency,brc_rate,brc_explain,brc_brsid,brc_sourceid,brc_source,brc_recorder,brc_indate,brc_kind,brc_status,brc_statuscode,brc_sourcetype) "
					+ " SELECT "
					+ brcid
					+ ",'"
					+ brccode
					+ "',brs_date,bar_custcode,bar_custname,brs_othercatecode,ca_description,brs_amount,brs_custcode,brs_custname,"
					+ "bar_currency,bar_rate,brs_remark,brs_id,bar_id,bar_code,'"
					+ SystemSession.getUser().getEm_name()
					+ "',sysdate,'拆分','已记账','POSTED','应收票据'"
					+ " from BillARSplit left join billar on brs_barid=bar_id left join category on brs_othercatecode=ca_code where brs_id="
					+ brs_id);
			// 插入应收票据异动明细
			execute("INSERT INTO BILLARCHANGEDETAIL(brd_id,brd_detno,brd_barcode,brd_checkcode,brd_custcode,brd_custname,brd_bank,brd_duedate,brd_doublebalance,"
					+ "brd_brcid,brd_barid,brd_catecode,brd_catecurrency,brd_amount,brd_remark) "
					+ " SELECT BILLARCHANGEDETAIL_SEQ.NEXTVAL,1,bar_code,bar_checkcode,bar_custcode,bar_custname,bar_bank,bar_duedate,bar_doublebalance,"
					+ brcid
					+ ",brs_barid,bar_othercatecode,ca_currency,brs_amount,bar_remark"
					+ " from BillARSplit left join billar on brs_barid=bar_id left join category on bar_othercatecode=ca_code where brs_id="
					+ brs_id);
			// 插入票据
			int barid = getSeqId("BILLAR_SEQ");
			String barcode = sGetMaxNumber("BillAR", 2);
			// 插入应收票据异动主表
			execute("INSERT INTO BILLAR(BAR_ID,bar_code,bar_date,bar_checkcode,bar_kind,bar_billkind,bar_custcode,bar_custname,bar_outdate,bar_duedate,"
					+ "bar_doublebalance,bar_currency,bar_rate,bar_cmcurrency,bar_cmrate,bar_topaybalance,bar_othercatecode,bar_settleamount,bar_leftamount,"
					+ "bar_sellercode,bar_seller,bar_departcode,bar_departname,bar_remark,bar_recorder,bar_sourcecode,bar_brsid,bar_sourceid,bar_sourcetype,bar_indate,bar_statuscode,bar_status,bar_vouchercode"
					+ ") "
					+ " SELECT "
					+ barid
					+ ",'"
					+ barcode
					+ "',brs_date,brs_checkcode,brs_kind,brs_billkind,brs_custcode,brs_custname,brs_outdate,brs_duedate,"
					+ "brs_amount,bar_currency,bar_rate,bar_cmcurrency,bar_cmrate,brs_amount,brs_othercatecode,0,brs_amount,"
					+ "cu_sellercode,cu_sellername,bar_departcode,bar_departname,brs_remark,'"
					+ SystemSession.getUser().getEm_name()
					+ "',bar_code,brs_id,bar_id,'应收票据',sysdate,'AUDITED','已审核','UNNEED'"
					+ " from BillARSplit left join billar on brs_barid=bar_id left join customer on brs_custcode=cu_code where brs_id="
					+ brs_id);
			// 更新应收票据票面余额
			execute("update billar set bar_leftamount=nvl(bar_leftamount,0)-" + bar[1] + " where bar_id=" + bar[0]);
			// 更新拆分状态
			execute("update billarsplit set brs_nowstatus='已拆分' where brs_id=" + brs_id);
			// 记录日志
			logger.others("应收票据拆分[" + bar[1] + "]", "拆分成功", "BillAR", "bar_id", bar[0]);
			logger.others("应收票据拆分[" + bar[1] + "]", "拆分成功", "BillARSplit", "bar_id", bar[0]);
			sb.append(
					"拆分成功，应收票据异动单号:" + "<a href=\"javascript:openUrl('jsps/fa/gs/billARChange.jsp?formCondition=brc_idIS" + brcid
							+ "&gridCondition=brd_brcidIS" + brcid + "')\">" + brccode + "</a>&nbsp;").append("<hr>");
			sb.append(
					"应收票据单号:" + "<a href=\"javascript:openUrl('jsps/fa/gs/billAR.jsp?formCondition=bar_idIS" + barid + "')\">" + barcode
							+ "</a>&nbsp;").append("<hr>");
		}
		return sb.toString();
	}

}
