package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlMap;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.ReturnDao;
import com.uas.erp.model.Employee;

@Repository
public class ReturnDaoImpl extends BaseDao implements ReturnDao {
	static final String INSERTACCOUNTREGISTER = "insert into AccountRegister ("
			+ "ar_id,ar_vendcode,ar_vendname,ar_recorddate,ar_date,ar_payment,ar_type,"
			+ "ar_code,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
			+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_recbankaccount,ar_recbank,ar_memo,ar_cateid,ar_checktitle) values("
			+ "?,?,?,sysdate,sysdate,?,?,?,?,?,?,'ENTERING',?,?,?,?,?,?,?,?,?,?)";
	static final String INSERTACCOUNTREGISTERDETAIL = "insert into AccountRegisterDetail ("
			+ "ard_detno,ard_currency,ard_debit,ard_nowbalance,ard_ordertype,ard_orderid,ard_ordercode,ard_orderdetno,ard_orderamount,"
			+ "ard_payments,ard_haveprepay,ard_id,ard_arid,ard_catecode) values (?,?,?,?,?,?,?,?,?,"
			+ "?,?,ACCOUNTREGISTERDETAIL_SEQ.NEXTVAL,?,?)";

	@Override
	public JSONObject turnBankRegister(String language, Employee employee) {
		int ar_id = getSeqId("ACCOUNTREGISTER_SEQ");
		JSONObject j = new JSONObject();
		j.put("ar_id", ar_id);
		return j;
	}

	static final String TURNBANKREGISTER = "Select * from CreditContractRegisterDet left join CreditContractRegister on ccrd_ccrid=ccr_id"
			+ " WHERE ccrd_id=?";

	public JSONObject turnBankRegister1(int ccrd_id, double ccrd_thisturnamount) {
		SqlRowList list = queryForRowSet(TURNBANKREGISTER, ccrd_id);
		int ar_id = 0;
		int ard_id = 0;
		String ar_code = null;
		SqlMap map = null;
		SqlMap map1 = null;
		Employee employee = SystemSession.getUser();
		if (list.next()) {
			map = new SqlMap("AccountRegister");
			map1 = new SqlMap("AccountRegisterDetail");
			ar_id = getSeqId("AccountRegister_SEQ");
			ard_id = getSeqId("AccountRegisterDetail_SEQ");
			ar_code = sGetMaxNumber("AccountRegister", 2);
			// form 字段
			map.set("ar_id", ar_id);
			map.set("ar_payment", ccrd_thisturnamount);
			map.set("ar_type", "其它付款");
			map.set("ar_code", ar_code);
			map.set("ar_sourceid", ccrd_id);
			map.set("ar_source", ar_code);
			map.set("ar_sourcetype", "贷款合同还款");
			map.set("ar_statuscode", "ENTERING");
			map.set("ar_status", BaseUtil.getLocalMessage("ENTERING"));
			map.set("ar_recordman", employee.getEm_name());
			map.set("ar_accountcode", list.getString("ccr_accountcode"));
			map.set("ar_accountname", list.getString("ccr_accountname"));
			map.set("ar_accountcurrency", list.getString("ccr_currency"));
			map.set("ar_accountrate", list.getString("ccr_rate"));
			map.set("ar_memo", list.getString("ccr_remark"));
			// detailgrid 字段
			map1.set("ard_detno", list.getString("ccrd_detno"));
			map1.set("ard_catecode", list.getString("ccr_accountcode"));
			// map1.set("ard_catedesc", list.getString("ccr_accountname"));
			map1.set("ard_currency", list.getString("ccr_currency"));
			map1.set("ard_rate", list.getString("ccr_rate"));
			map1.set("ard_debit", ccrd_thisturnamount);
			map1.set("ard_ordertype", "贷款合同还款");
			map1.set("ard_orderid", ccrd_id);
			map1.set("ard_ordercode", list.getString("ccr_contractno"));
			map1.set("ard_id", ard_id);
			map1.set("ard_arid", ar_id);
			map.execute();
			map1.execute();
			;
		}
		execute("update AccountRegister set ar_recorddate=sysdate,ar_date=sysdate where ar_id="
				+ ar_id);
		JSONObject j = new JSONObject();
		j.put("ar_id", ar_id);
		j.put("ard_id", ard_id);
		j.put("ar_code", ar_code);
		return j;
	}
}
