package com.uas.erp.service.plm;

public interface ProjectTeamRoleService {

	void updateProjectTeamRole(String formStore, String caller);

	void saveProjectTeamRole(String formStore, String caller);

	void deleteProjectTeamRole(int id, String caller);
}
