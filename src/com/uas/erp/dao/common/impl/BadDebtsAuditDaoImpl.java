package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BadDebtsAuditDao;
import com.uas.erp.model.Employee;

@Repository
public class BadDebtsAuditDaoImpl extends BaseDao implements BadDebtsAuditDao {
	static final String TURNBADDEBTSAUDIT = "SELECT * FROM BadDebtsAudit WHERE bda_id=?";
	static final String TURNBADDEBTSAUDITDETAIL = "SELECT * FROM BadDebtsAuditDetail WHERE bdad_bdaid=?";
	static final String INSERTRECBALANCE = "INSERT INTO RecBalance(rb_id,rb_code,rb_custcode,rb_custname,rb_date"
			+ ",rb_currency,rb_recorddate,rb_strikestatus,rb_strikestatuscode,rb_printstatus,rb_printstatuscode,rb_emname,rb_emid,rb_auditstatus,rb_auditstatuscode,rb_status"
			+ ",rb_statuscode,rb_kind,rb_amount,RB_SOURCE,RB_SOURCEID,RB_SOURCECODE) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTRECBALANCEDETAIL = "INSERT INTO RecBalanceDetail(rbd_detno,rbd_rbid,rbd_id,rbd_orderid,rbd_ordercode,rbd_cateid,"
			+ "rbd_catecode,rbd_catename,rbd_invoicedate,rbd_duedate,rbd_currency,rbd_sellerid,rbd_seller,rbd_aramount,rbd_havepay,rbd_nowbalance,rbd_remark)"
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	public int turnRecBalanceIMRE(int bda_id, String caller) {
		SqlRowList rs = queryForRowSet(TURNBADDEBTSAUDIT, new Object[] { bda_id });
		int rb_id = 0;
		Employee employee = SystemSession.getUser();
		if (rs.next()) {
			rb_id = getSeqId("RECBALANCE_SEQ");
			String code = sGetMaxNumber("RecBalance", 2);
			Timestamp time = Timestamp.valueOf(DateUtil.currentDateString("yyyy-MM-dd HH:mm:ss"));
			execute(INSERTRECBALANCE,
					new Object[] { rb_id, code, rs.getObject("bda_customcode"), rs.getObject("bda_customname"), time,
							rs.getObject("bda_currency"), time, BaseUtil.getLocalMessage("UNSTRIKE"), "UNSTRIKE",
							BaseUtil.getLocalMessage("UNPRINT"), "UNPRINT", employee.getEm_name(), employee.getEm_id(),
							BaseUtil.getLocalMessage("ENTERING"), "ENTERING", BaseUtil.getLocalMessage("UNPOST"), "UNPOST", "冲应收款",
							rs.getObject("bda_arrearageamount"), "呆账处理审批", bda_id, rs.getObject("bda_code") });
			execute("update recbalance set rb_amount=?,rb_cmamount=?,rb_cmcurrency=rb_currency,rb_cmrate=1 where rb_id=?",
					rs.getObject("bda_arrearageamount"), rs.getObject("bda_arrearageamount"), rb_id);
		}
		if (rb_id > 0) {
			SqlRowList rs1 = queryForRowSet(TURNBADDEBTSAUDITDETAIL, new Object[] { bda_id });
			while (rs1.next()) {
				int rbdid = getSeqId("RECBALANCEDETAIL_SEQ");
				execute(INSERTRECBALANCEDETAIL,
						new Object[] { rs1.getObject("bdad_detno"), rb_id, rbdid, rs1.getObject("bdad_orderid"),
								rs1.getObject("bdad_ordercode"), rs1.getObject("bdad_cateid"), rs1.getObject("bdad_catecode"),
								rs1.getObject("bdad_catename"), rs1.getObject("bdad_invoicedate"), rs1.getObject("bdad_duedate"),
								rs1.getObject("bdad_currency"), rs1.getObject("bdad_sellerid"), rs1.getObject("bdad_seller"),
								rs1.getObject("bdad_aramount"), rs1.getObject("bdad_havepay"), rs1.getObject("bdad_nowbalance"),
								rs1.getObject("bdad_remark") });
			}
		}
		return rb_id;
	}
}
