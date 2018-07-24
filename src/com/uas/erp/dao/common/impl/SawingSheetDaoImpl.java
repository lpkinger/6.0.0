package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.SawingSheetDao;

@Repository
public class SawingSheetDaoImpl extends BaseDao implements SawingSheetDao {
	static final String SAWINGSHEET = "SELECT * FROM SAWINGSHEET WHERE ss_id=?";
	static final String SAWINGSHEETBEFORE = "SELECT * FROM SAWINGSHEETBEFORE WHERE ssb_ssid=? order by ssb_detno";
	static final String SAWINGSHEETAFTER = "SELECT * FROM SAWINGSHEETAFTER WHERE ssa_ssid=? order by ssa_detno";
	static final String PRODINOUT = "INSERT INTO PRODINOUT(pi_id, pi_inoutno, pi_invostatuscode,pi_statuscode,pi_printstatuscode, pi_invostatus,pi_status,"
			+ "pi_printstatus, pi_recordman,pi_recorddate,pi_date,pi_class,pi_sourcecode,pi_departmentname,pi_departmentcode,pi_remark,pi_fromcode,pi_type)"
			+ " values (?,?,'ENTERING','UNPOST','UNPRINT',?,?,?,?,sysdate,sysdate,?,?,?,?,?,?,?)";
	final static String PRODIODETAIL = "INSERT INTO prodiodetail(pd_id,pd_status,pd_auditstatus,pd_prodid,pd_prodcode,pd_inqty,pd_outqty,"
			+ "pd_piid,pd_inoutno,pd_piclass,pd_pdno,pd_whcode,pd_whname,pd_price,pd_taxrate,pd_taxtotal,pd_total,pd_batchcode,pd_batchid)"
			+ " values (PRODIODETAIL_SEQ.nextval,0,'ENTERING',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	@Transactional
	public JSONObject turnProdInOut(int id, String piclass) {
		SqlRowList rs = queryForRowSet(SAWINGSHEET, id);
		int pi_id = 0;
		String pi_inoutno = null;
		String type = null;
		String caller = null;
		if (rs.next()) {
			pi_id = getSeqId("PRODINOUT_SEQ");
			if ("其它出库单".equals(piclass)) {
				caller = "ProdInOut!OtherOut";
				type = "开料出库";
			} else if ("其它入库单".equals(piclass)) {
				caller = "ProdInOut!OtherIn";
				type = "开料入库";
			}
			pi_inoutno = sGetMaxNumber(caller, 2);
			execute(PRODINOUT,
					new Object[] { pi_id, pi_inoutno, BaseUtil.getLocalMessage("ENTERING"), BaseUtil.getLocalMessage("UNPOST"),
							BaseUtil.getLocalMessage("UNPRINT"), SystemSession.getUser().getEm_name(), piclass, rs.getObject("ss_code"),
							rs.getObject("ss_departmentname"), rs.getObject("ss_departmentcode"), rs.getObject("ss_remark"),
							rs.getObject("ss_code"), type });
			if (isDBSetting(caller, "postNeedAudit")) {
				execute("update prodinout set pi_invostatuscode='AUDITED',pi_invostatus='" + BaseUtil.getLocalMessage("AUDITED")
						+ "' WHERE pi_id=" + pi_id);
			}
			JSONObject j = new JSONObject();
			j.put("pi_id", pi_id);
			j.put("pi_inoutno", pi_inoutno);
			return j;
		}
		return null;
	}

	@Override
	public void turnProdIODetail(int id, String piclass, Object pi_id, Object pi_inoutno) {
		if ("其它出库单".equals(piclass)) {
			SqlRowList rs = queryForRowSet(SAWINGSHEETBEFORE, id);
			int count = 1;
			while (rs.next()) {
				double total = NumberUtil.formatDouble(rs.getGeneralDouble("ssb_outqty") * rs.getGeneralDouble("ssb_price"), 2);
				execute(PRODIODETAIL,
						new Object[] { rs.getGeneralInt("ssb_prodid"), rs.getString("ssb_prodcode"), 0, rs.getGeneralDouble("ssb_outqty"),
								pi_id, pi_inoutno, piclass, count++, rs.getObject("ssb_whcode"), rs.getObject("ssb_whname"),
								rs.getGeneralDouble("ssb_price"), 0, total, total, rs.getObject("ssb_batchcode"),
								rs.getObject("ssb_batchid") });
			}
		} else if ("其它入库单".equals(piclass)) {
			SqlRowList rs = queryForRowSet(SAWINGSHEETAFTER, id);
			int count = 1;
			while (rs.next()) {
				double total = NumberUtil.formatDouble(rs.getGeneralDouble("ssa_inqty") * rs.getGeneralDouble("ssa_price"), 2);
				execute(PRODIODETAIL,
						new Object[] { rs.getGeneralInt("ssa_prodid"), rs.getString("ssa_prodcode"), rs.getGeneralDouble("ssa_inqty"), 0,
								pi_id, pi_inoutno, piclass, count++, rs.getObject("ssa_whcode"), rs.getObject("ssa_whname"),
								rs.getGeneralDouble("ssa_price"), 0, total, total, rs.getObject("ssa_batchcode"),
								rs.getObject("ssa_batchid") });
			}
		}
		execute("update prodinout set (pi_whcode,pi_whname)=(select pd_whcode,pd_whname from prodiodetail where pd_piid=pi_id and pd_pdno=1) where pi_id="
				+ pi_id);
	}
}