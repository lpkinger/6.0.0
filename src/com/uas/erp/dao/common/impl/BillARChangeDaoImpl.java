package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BillARChangeDao;
import com.uas.erp.model.Employee;

@Repository
public class BillARChangeDaoImpl extends BaseDao implements BillARChangeDao{
	static final String TURNBILLARCHANGE = "SELECT * FROM BillARChangeDetail left join BillARChange on brd_brcid=brc_id WHERE brc_id=?";
	static final String INSERTRECBALANCE = "INSERT INTO RecBalance(rb_id,rb_code,rb_source,rb_sourceid,rb_custcode,rb_date" +
			",rb_currency,rb_rate,rb_recorddate,rb_custid,rb_custname,rb_emname,rb_strikestatus" + 
			",rb_strikestatuscode,rb_printstatus,rb_printstatuscode,rb_emid,rb_auditstatus,rb_auditstatuscode,rb_status"+
			",rb_statuscode,rb_kind,rb_sellerid,rb_seller,rb_amount" +
			",rb_catecode,rb_catename) VALUES (?,?,'应收票据异动',?,?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,'收款单',?,?,?,?,?)";
	static final String INSERTPREREC = "INSERT INTO PreRec(pr_id,pr_code,pr_source,pr_sourceid,pr_custcode,pr_date" +
			",pr_currency,pr_rate,pr_indate,pr_custname,pr_recorder,pr_recorderid,pr_cmstatus" + 
			",pr_cmstatuscode,pr_printstatus,pr_printstatuscode,pr_auditstatus,pr_auditstatuscode,pr_status"+
			",pr_statuscode,pr_kind,pr_sellerid,pr_seller,pr_sellercode,pr_amount" +
			",pr_accountcode,pr_accountname) VALUES (?,?,'应收票据异动',?,?,?,?,?,sysdate,?,?,?,?," +
			"'UNSTRIKE',?,'UNPRINT',?,'ENTERING',?,'UNPOST','预收款',?,?,?,?,?,?)";
	static final String INSERTPAYBALANCE = "INSERT INTO PayBalance(pb_id,pb_code,pb_source,pb_sourceid,pb_vendcode,pb_date" +
			",pb_currency,pb_recorddate,pb_vendid,pb_vendname,pb_recorder,pb_vmstatus" + 
			",pb_vmstatuscode,pb_printstatus,pb_printstatuscode,pb_recorderid,pb_auditstatus,pb_auditstatuscode,pb_status"+
			",pb_statuscode,pb_kind,pb_buyerid,pb_buyer,pb_amount" +
			",pb_catecode,pb_catename) VALUES (?,?,'应收票据异动',?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,'付款单',?,?,?,?,?)";
	static final String INSERTPREPAY = "INSERT INTO PrePay(pp_id,pp_code,pp_source,pp_sourceid,pp_vendcode,pp_date" +
			",pp_currency,pp_indate,pp_vendname,pp_recorder,pp_recorderid,pp_vmstatus" + 
			",pp_vmstatuscode,pp_printstatus,pp_printstatuscode,pp_auditstatus,pp_auditstatuscode,pp_status"+
			",pp_statuscode,pp_type,pp_vendid,pp_buyerid,pp_buyer,pp_amount" +
			",pp_accountcode,pp_accountname) VALUES (?,?,'应收票据异动',?,?,?,?,sysdate,?,?,?,?," +
			"'UNSTRIKE',?,'UNPRINT',?,'ENTERING',?,'UNPOST','预付款',?,?,?,?,?,?)";
	
	@Override
	public void turnRecBalance(int id, String language, Employee employee) {
		SqlRowList rs = queryForRowSet(TURNBILLARCHANGE, new Object[]{id});
		int rb_id=0;
		while(rs.next()){
			rb_id = getSeqId("RECBALANCE_SEQ");
			String code = sGetMaxNumber("RecBalance", 2);
			Object[] cust = getFieldsDataByCondition("customer", new String[]{"cu_id","cu_sellerid","cu_sellername"}, "cu_code='" + rs.getObject("brc_custcode") + "'");
			Object rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + rs.getObject("brd_currency") + "'");
			execute(INSERTRECBALANCE, new Object[]{rb_id,code,rs.getObject("brd_id"),rs.getObject("brc_custcode"),rs.getObject("brc_date"),
					rs.getObject("brd_currency"), rate, cust[0],rs.getObject("brc_custname"),employee.getEm_name(),
					BaseUtil.getLocalMessage("UNSTRIKE", language), "UNSTRIKE", BaseUtil.getLocalMessage("UNPRINT", language),
					"UNPRINT",employee.getEm_id(), BaseUtil.getLocalMessage("ENTERING", language), "ENTERING",
					BaseUtil.getLocalMessage("UNPOST", language), "UNPOST",cust[1],cust[2],rs.getObject("brc_amount"),
					rs.getObject("brc_catecode"),rs.getObject("brc_catename")});
		}
	}
	@Override
	public void turnPreRec(int id, String language, Employee employee) {
		SqlRowList rs = queryForRowSet(TURNBILLARCHANGE, new Object[]{id});
		int pr_id=0;
		while(rs.next()){
			pr_id = getSeqId("PREREC_SEQ");
			String code = sGetMaxNumber("PreRec", 2);
			Object[] cust = getFieldsDataByCondition("customer", new String[]{"cu_sellerid","cu_sellername","cu_sellercode"}, "cu_code='" + rs.getObject("brc_custcode") + "'");
			Object rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + rs.getObject("brd_currency") + "'");
			execute(INSERTPREREC, new Object[]{pr_id,code,rs.getObject("brd_id"),rs.getObject("brc_custcode"),rs.getObject("brc_date"),
					rs.getObject("brd_currency"),rate,rs.getObject("brc_custname"),employee.getEm_name(),
					employee.getEm_id(),BaseUtil.getLocalMessage("UNSTRIKE", language),
					BaseUtil.getLocalMessage("UNPRINT", language),BaseUtil.getLocalMessage("ENTERING", language),
					BaseUtil.getLocalMessage("UNPOST", language),cust[0],cust[1],cust[2],rs.getObject("brc_amount"),
					rs.getObject("brc_catecode"),rs.getObject("brc_catename")});
		}
	}
	@Override
	public void turnPayBalance(int id, String language, Employee employee) {
		SqlRowList rs = queryForRowSet(TURNBILLARCHANGE, new Object[]{id});
		int pb_id=0;
		while(rs.next()){
			pb_id = getSeqId("PAYBALANCE_SEQ");
			String code = sGetMaxNumber("PayBalance", 2);
			Object[] vend = getFieldsDataByCondition("Vendor", new String[]{"ve_id","ve_buyerid","ve_buyername"}, "ve_code='" + rs.getObject("brc_vendcode") + "'");
			execute(INSERTPAYBALANCE, new Object[]{pb_id,code,rs.getObject("brd_id"),rs.getObject("brc_vendcode"),rs.getObject("brc_date"),
					rs.getObject("brd_currency"), vend[0],rs.getObject("brc_vendname"),employee.getEm_name(),
					BaseUtil.getLocalMessage("UNSTRIKE", language), "UNSTRIKE", BaseUtil.getLocalMessage("UNPRINT", language),
					"UNPRINT",employee.getEm_id(), BaseUtil.getLocalMessage("ENTERING", language), "ENTERING",
					BaseUtil.getLocalMessage("UNPOST", language), "UNPOST",vend[1],vend[2],rs.getObject("brc_amount"),
					rs.getObject("brc_catecode"),rs.getObject("brc_catename")});
		}
	}
	@Override
	public void turnPrePay(int id, String language, Employee employee) {
		SqlRowList rs = queryForRowSet(TURNBILLARCHANGE, new Object[]{id});
		int pp_id=0;
		while(rs.next()){
			pp_id = getSeqId("PREPAY_SEQ");
			String code = sGetMaxNumber("PrePay", 2);
			Object[] vend = getFieldsDataByCondition("Vendor", new String[]{"ve_id","ve_buyerid","ve_buyername"}, "ve_code='" + rs.getObject("brc_vendcode") + "'");
			execute(INSERTPREREC, new Object[]{pp_id,code,rs.getObject("brd_id"),rs.getObject("brc_vendcode"),rs.getObject("brc_date"),
					rs.getObject("brd_currency"),rs.getObject("brc_vendname"),employee.getEm_name(),
					employee.getEm_id(),BaseUtil.getLocalMessage("UNSTRIKE", language),
					BaseUtil.getLocalMessage("UNPRINT", language),BaseUtil.getLocalMessage("ENTERING", language),
					BaseUtil.getLocalMessage("UNPOST", language),vend[0],vend[1],vend[2],rs.getObject("brc_amount"),
					rs.getObject("brc_catecode"),rs.getObject("brc_catename")});
		}
	}
}
