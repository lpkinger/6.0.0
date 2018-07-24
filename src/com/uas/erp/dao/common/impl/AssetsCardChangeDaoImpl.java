package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.AssetsCardChangeDao;

@Repository
public class AssetsCardChangeDaoImpl extends BaseDao implements AssetsCardChangeDao{
	static final String TURNCARD = "SELECT acc_accode,acc_ascode,acc_asname,acc_accatecode,acc_ascatecode,acc_totalcatecode" + 
			",acc_location,acc_usestatus,acc_department,acc_departmentname,acc_addmethod,acc_remark,acc_currency,acc_qty" + 
			",acc_unit,acc_spec,acc_useman,acc_kind FROM AssetsCardChange WHERE acc_id=?";
	static final String UPDATECARD = "update AssetsCard set ac_ascode=?,ac_asname=?,ac_accatecode=?,ac_ascatecode=?,ac_totalcatecode=?" +
			",ac_location=?,ac_usestatus=?,ac_department=?,ac_departmentname=?,ac_addmethod=?,ac_remark=?,ac_currency=?" + 
			",ac_qty=?, ac_unit=?, ac_spec=?, ac_useman=?, ac_kind=? where ac_code=?";
	@Override
	@Transactional
	public String turnAssetsCard(int id, String language) {
		SqlRowList rs = queryForRowSet(TURNCARD, new Object[]{id});
		String accode = null;
		if(rs.next()){
			accode = rs.getString("acc_accode");
			execute(UPDATECARD, new Object[]{rs.getObject("acc_ascode"), rs.getObject("acc_asname"), 
				rs.getObject("acc_accatecode"), rs.getObject("acc_ascatecode"), rs.getObject("acc_totalcatecode"), 
				rs.getObject("acc_location"), rs.getObject("acc_usestatus"), rs.getObject("acc_department"), 
				rs.getObject("acc_departmentname"),rs.getObject("acc_addmethod"),rs.getObject("acc_remark"),
				rs.getObject("acc_currency"),rs.getObject("acc_qty"),rs.getObject("acc_unit"),rs.getObject("acc_spec"),
				rs.getObject("acc_useman"),rs.getObject("acc_kind"),rs.getObject("acc_accode")});
		}
		return accode;
	}	
}
