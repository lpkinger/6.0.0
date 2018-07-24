package com.uas.erp.dao.common;

import java.sql.Blob;
import java.util.List;

import com.uas.erp.model.Enterprise;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.Master;

public interface EnterpriseDao {
	Enterprise getEnterpriseByName(String name);

	void saveEnterprise(Enterprise enterprise);

	Enterprise getEnterpriseByEnUU(int en_uu);

	Enterprise getEnterpriseById(int id);

	Enterprise getEnterprise();

	Blob getLogo();

	List<Master> getMasters();
	
	Master getMasterByName(String dbname);

	List<Master> getAbleMaster();

	Master getMasterByDomain(String domain);

	List<JSONTree> getMastersTree(Integer pid);
}
