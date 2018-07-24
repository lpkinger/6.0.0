package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.Category;
import com.uas.erp.model.HROrg;

public interface CategoryStrDao {

	List<HROrg> getHrOrgbyParentId(int parentid);

	List<Category> getAllCategorys(String condition);

	HROrg getHrOrgByCode(String em_code);

	HROrg getHrOrgByEmId(int em_id);

	String getToUi(String key, String caller);

	List<Category> getCategoryBank(int parentid);
}
