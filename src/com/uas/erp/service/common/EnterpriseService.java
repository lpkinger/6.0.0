package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;

public interface EnterpriseService {
	boolean checkEnterpriseName(String name);

	boolean checkMasterNamePwd(String name, String pwd);

	void saveEnterprise(Enterprise enterprise);

	String loginWithEn(String username, String password);

	Enterprise getEnterpriseById(int id);

	Enterprise getEnterprise();

	String getMasterByUU(Integer uu);
	
	String getMasterByUU(String uu);

	Master getMasterByName(String name);

	Master getMasterByID(int id);

	Master getMasterByManage(long manageId);

	List<Master> getMasters();

	List<Master> getAbleMasters();
	
	List<Master> getAbleMastersByEmMasters(String emMasters, Boolean isOwnerMaster);

	void clearMasterCache();

	/**
	 * 按域名查找账套
	 * 
	 * @param domain
	 * @return
	 */
	Master getMasterByDomain(String domain);

	List<Object> getMasterNames();

	List<Map<String, Object>> getOutMasters();

	boolean checkJobOrgRelation();

	Object getDefaultMasterName();

	Object getDefaultMasterFun();

	Object getDefaultEnterpriseName();
}
