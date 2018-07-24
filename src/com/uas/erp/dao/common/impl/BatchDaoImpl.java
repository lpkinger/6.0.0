package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.BatchDao;
import com.uas.erp.model.Employee;

@Repository
public class BatchDaoImpl extends BaseDao implements BatchDao{
	static final String INSERTQUABATCH = "INSERT INTO QUABATCH(qba_id,qba_code,qba_status,qba_statuscode,qba_recorder,qba_indate"
			+ ",qba_checkman,qba_date) VALUES (?,?,?,'ENTERING',?,sysdate,?,sysdate)";
	static final String INSERTQUABATCHDETAIL = "INSERT INTO QUABATCHDetail(qbd_id,qbd_qbaid,qbd_detno,qbd_baid,qbd_batchcode,qbd_prodcode,qbd_whcode,qbd_qty,"
			+ "qbd_validtime,qbd_newvalidtime) VALUES (?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTPRODIO = "INSERT INTO ProdInOut(pi_id,pi_inoutno,pi_invostatus,pi_invostatuscode,pi_recordman,pi_recorddate"
			+ ",pi_type,pi_date,pi_class,pi_status,pi_statuscode,pi_printstatus,pi_printstatuscode) VALUES (?,?,?,'ENTERING',?,sysdate,"
			+ "'库存转移',sysdate,'拨出单',?,'UNPOST',?,'UNPRINT')";
	static final String INSERTPRODIODETAIL = "INSERT INTO ProdIODetail(pd_id,pd_piid,pd_pdno,pd_baid,pd_batchcode,pd_prodcode,pd_whcode,pd_outqty,"
			+ "pd_ordercode,pd_orderid,pd_sellercode,pd_seller,pd_piclass,pd_inoutno,pd_prodid,PD_ORDERDETNO) VALUES (PRODIODETAIL_SEQ.nextVal,?,?,?,?,?,?,?,?,?)";
	@Override
	public JSONObject turnQUABatch() {
		int qbaid = getSeqId("QUABATCH_SEQ");
		String code = sGetMaxNumber("QUABatch", 2);
		Employee employee = SystemSession.getUser();
		boolean bool = execute(
					INSERTQUABATCH,
					new Object[] { qbaid, code, BaseUtil.getLocalMessage("ENTERING"),
							employee.getEm_name(), employee.getEm_name()});
		if (bool) {
			JSONObject j = new JSONObject();
			j.put("qba_id", qbaid);
			j.put("qba_code", code);
			return j;
		}
		return null;
	}

	@Override
	public int toAppointedQUABatch(String qba_code, int ba_id) {
		Object qbaid = getFieldDataByCondition("QUABatch", "qba_id", "qba_code='" + qba_code + "'");
		SqlRowList rs = queryForRowSet("select * from Batch where ba_id=?", ba_id);
		if (rs.next()) {
			int qbdid = getSeqId("QUABATCHDETAIL_SEQ");
			Object count = getFieldDataByCondition("QUABatchDetail", "max(qbd_detno)", "qbd_qbaid=" + qbaid);
			count = count == null ? 0 : count;
			int detno = Integer.parseInt(count.toString());
			execute(INSERTQUABATCHDETAIL,
					new Object[] { qbdid, qbaid, ++detno, ba_id, rs.getObject("ba_code"),rs.getObject("ba_prodcode"),rs.getObject("ba_whcode"), 
							rs.getObject("ba_remain"), rs.getObject("ba_validtime"), rs.getObject("ba_validtime")});
			return qbdid;
		} else
			return 0;
	}

	@Override
	public JSONObject turnBoChu() {
		int pi_id = getSeqId("PRODINOUT_SEQ");
		String pi_inoutno = sGetMaxNumber("ProdInOut!AppropriationOut", 2);
		Employee employee = SystemSession.getUser();
		boolean bool = execute(INSERTPRODIO,
					new Object[] { pi_id, pi_inoutno, BaseUtil.getLocalMessage("ENTERING"), employee.getEm_name(), 
							BaseUtil.getLocalMessage("UNPOST"), BaseUtil.getLocalMessage("UNPRINT")});
		if (bool) {
			JSONObject j = new JSONObject();
			j.put("pi_id", pi_id);
			j.put("pi_inoutno", pi_inoutno);
			return j;
		}
		return null;
	}

	@Override
	public int toAppointedBoChu(int piid, String no, int qbd_id, Double qty) {
		int qbdid = getSeqId("QUABATCHDETAIL_SEQ");
		Object count = getFieldDataByCondition("ProdIODetail", "max(pd_pdno)", "pd_inoutno='" + no + "' and pd_piclass='拨出单'");
		count = count == null ? 0 : count;
		int detno = Integer.parseInt(count.toString());
		execute("INSERT INTO ProdIODetail(pd_id,pd_piid,pd_pdno,pd_batchid,pd_batchcode,pd_prodcode,pd_whcode,pd_outqty,"
				+ "pd_ordercode,pd_qbdid,pd_sellercode,pd_seller,pd_piclass,pd_inoutno,pd_prodid,PD_ORDERDETNO, pd_location) "
				+ "select PRODIODETAIL_SEQ.nextVal, " + piid + ", " + (++detno) + ", qbd_baid, qbd_batchcode, qbd_prodcode, qbd_whcode, "
				+ qty + ", qba_code, qbd_id, BA_SELLERCODE, BA_SELLER, '拨出单', '" + no + "', pr_id, qbd_detno, ba_location "
				+ "from QUABATCH left join QUABATCHDETAIL on qba_id=qbd_qbaid left join Product on qbd_prodcode=pr_code "
				+ "left join Batch on qbd_baid=ba_id where qbd_id=" + qbd_id);
		return qbdid;
	} 
}
