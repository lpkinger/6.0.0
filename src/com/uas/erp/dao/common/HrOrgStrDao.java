package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.HROrg;

public interface HrOrgStrDao {
	
	List<HROrg> getHrOrgbyParentId(int parentid);
	
	List<HROrg> getAllHrOrgs(String condition);
	
	HROrg getHrOrgByCode(String em_code);
	
	HROrg getHrOrgByEmId(int em_id);

}
