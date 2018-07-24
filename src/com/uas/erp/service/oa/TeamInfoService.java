package com.uas.erp.service.oa;


public interface TeamInfoService {
	void saveTeamInfo(String formStore, String  caller);
	void updateTeamInfo(String formStore, String  caller);
	void deleteTeamInfo(int ti_id, String  caller);
}
