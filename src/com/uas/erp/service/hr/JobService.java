package com.uas.erp.service.hr;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Job;
public interface JobService {
	void saveJob(String formStore, String  caller);
	void submitJob(int jo_id, String  caller);
	void resSubmitJob(int jo_id, String  caller);
	void auditJob(int jo_id, String  caller);
	void resAuditJob(int jo_id, String  caller);
	void updateJobById(String formStore, String  caller);
	void deleteJob(int jo_id, String  caller);
	List<Job> getJobs(String orgid, String isStandard);
	void saveJobs(String created);
	void updateJobs(String jsonData);
	List<Job> getJobsWithStandard();
	Map<String, String> getEmployees(int id);
	void bannedJob(int id ,String caller);
	void resBannedJob(int id ,String caller);
	/*
	 * Wsy
	 */
	void saveEmployees(String jsonData,String enUU);
	void updateEmployees(String jsonData);
	List<Job> getJobsByCondition(String condition);
	List<Job> getSaasJobs(String isStandard);
	void saveSaasJobs(String gridStore);
	String[] getInfo(String id);
	String getSaasEmployees(int parseInt);
	void deleteSaasJob(int id);
	void updateDescription(String jo_id, String jo_power, String jo_description);
}
