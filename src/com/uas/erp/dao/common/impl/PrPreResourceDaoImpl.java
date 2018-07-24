package com.uas.erp.dao.common.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.PrPreResourceDao;
import com.uas.erp.model.Employee;
@Transactional
@Repository
public class PrPreResourceDaoImpl extends BaseDao implements PrPreResourceDao {
	static final String TURNPRERESOURCE = "SELECT pr_code,pr_recorder,pr_recorddate,pr_status," +
			"pr_name,pr_shortname,pr_engname,pr_add,pr_developer1,pr_developer2,pr_type," +
			"pr_statuscode,pr_bank,pr_legalperson,pr_date,pr_bankaccount,pr_auditor,pr_auditdate," +
			"pr_contact,pr_position,pr_email,pr_web,pr_postcode,pr_area,pr_phone,pr_tel," +
			"pr_fax,pr_deliveryadd,pr_transportation,pr_remark,pr_tax,pr_currency,pr_payment," +
			"pr_paymentdays,pr_prodbrand FROM PrPreResource WHERE pr_id=?";
	static final String INSERTPRERESOURCE = "INSERT INTO PRERESOURCE(pr_id,pr_code,pr_recorder,pr_recorddate" +
			",pr_status,pr_name,pr_shortname,pr_engname,pr_add" +
			",pr_developer1,pr_developer2,pr_type,pr_statuscode,pr_bank,pr_legalperson" +
			",pr_date,pr_bankaccount,pr_auditor,pr_auditdate,pr_contact,pr_position" +
			",pr_email,pr_web,pr_postcode,pr_area,pr_phone,pr_tel,pr_fax,pr_deliveryadd" +
			",pr_transportation,pr_remark,pr_tax,pr_currency,pr_payment,pr_paymentdays,pr_prodbrand)values" +
			"(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	


	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int turnPreResource(int pr_id, String language, Employee employee) {
		// TODO Auto-generated method stub
		SqlRowList rs = queryForRowSet(TURNPRERESOURCE, new Object[]{pr_id});
		int prid=0;
		if(rs.next()){
			prid = getSeqId("PreResource_SEQ");
			//String code = sGetMaxNumber("Customer", 2);		
			//Object sellerid = getFieldDataByCondition("Employee", "em_id", "em_code='" + rs.getObject(6) + "'");
			execute(INSERTPRERESOURCE, new Object[]{prid,rs.getObject(1),employee.getEm_name(),Timestamp.valueOf(DateUtil.currentDateString(Constant.YMD_HMS)),rs.getObject(4),rs.getObject(5),
					rs.getObject(6),rs.getObject(7),rs.getObject(8),rs.getObject(9),rs.getObject(10),rs.getObject(11),rs.getObject(12),
					rs.getObject(13),rs.getObject(14),rs.getObject(15),rs.getObject(16),rs.getObject(17),rs.getObject(18),rs.getObject(19),
					rs.getObject(20),rs.getObject(21),rs.getObject(22),rs.getObject(23),rs.getObject(24),rs.getObject(25),rs.getObject(26),
					rs.getObject(27),rs.getObject(28),rs.getObject(29),rs.getObject(30),rs.getObject(31),rs.getObject(32),rs.getObject(33),
					rs.getObject(34),rs.getObject(35)}); 			
			/*updateByCondition("PrPreResource", "pr_statuscode='TURNED',pr_status='" + 
					BaseUtil.getLocalMessage("TURNED", language) + "'", "pr_id=" + pr_id);*/
		}
		return prid;
	}

}
