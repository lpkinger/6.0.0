package com.uas.erp.service.hr;

public interface RoleService {
	void saveRole(String caller,String formStore,String param);
	void updateRole(String caller,String formStore,String param);
	void deleteRole(String caller,int id);
	void auditRole(String caller, int id);
	void resAuditRole(String caller, int id);
	void submitRole(String caller,int id);
	void resSubmitRole(String caller,int id);
	void bannedRole(String caller,int id);
	void resBannedRole(String caller,int id);
}

