package com.uas.erp.service.hr;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.HRJob;
import com.uas.erp.model.Role;

public interface HrJobService {
	void saveHrJob(String formStore, String caller);

	void updateHrJobById(String formStore, String caller);

	void deleteHrJob(int jo_id, String caller);

	List<HRJob> getHrJobs();
	
	List<Role> getRoles();

	Map<String, Object> getLimitFieldsByCaller(String caller, int jo_id, String utype);

	Map<String, Object> getSelfLimitFieldsByCaller(String caller, Integer em_id, String utype);
	
	Map<String, Object> getRoleLimitFieldsByCaller(String caller, Integer ro_id, String utype);
}
