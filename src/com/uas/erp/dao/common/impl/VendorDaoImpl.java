package com.uas.erp.dao.common.impl;

import org.springframework.stereotype.Repository;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.VendorDao;

@Repository("vendorDao")
public class VendorDaoImpl extends BaseDao implements VendorDao{

	@Override
	public boolean checkVendorByEnId(int ve_enid, int ve_otherenid) {
		String sql = "SELECT * from vendor where ve_enid="+ ve_enid + " AND ve_otherenid=" + ve_otherenid;
		return getCount(sql) == 0;
	}
	
}
