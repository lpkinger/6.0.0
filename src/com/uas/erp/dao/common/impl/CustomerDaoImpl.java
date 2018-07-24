package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.CustomerDao;

@Repository("customerDao")
public class CustomerDaoImpl extends BaseDao implements CustomerDao{
	@Override
	public boolean checkCustomerByEnId(int cu_enid, int cu_otherenid) {
		String sql = "SELECT * from customer where cu_enid="+ cu_enid + " AND cu_otherenid=" + cu_otherenid;
		return getCount(sql) == 0;
	}
	
}
