package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.HRJob;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Role;
import com.uas.erp.model.RoleLimitFields;
import com.uas.erp.model.SelfLimitFields;

public interface HrJobDao {
	/**
	 * @param sob
	 *            帐套名称
	 * @return
	 */
	List<HRJob> getHrJobs(String sob);
	List<Role> getRoles(String sob);
	HRJob getHrJob(int id);
	int getJoIdByEmId(int em_id);

	List<LimitFields> getLimitFieldsByCaller(String caller, int jo_id, String sob);
	
	List<RoleLimitFields> getRoleLimitFieldsByCaller(String caller, Integer ro_id, String sob);
	
	/**
	 * @param caller
	 * @param relativeCaller
	 *            关联列表的caller
	 * @param isForm
	 * @param jo_id
	 * @param sob
	 * @return
	 */
	List<LimitFields> getLimitFieldsByType(String caller, String relativeCaller, int isForm, Integer jo_id, String sob);
	
	List<RoleLimitFields> getRoleLimitFieldsByType(String caller, String relativeCaller, int isForm, Integer ro_id, String sob);

	List<SelfLimitFields> getSelfLimitFieldsByCaller(String caller, Integer em_id);

	List<SelfLimitFields> getSelfLimitFieldsByType(String caller, int isForm, Integer em_id, String sob);
	HRJob getParentJob(int jobid);
	List<HRJob> getJobsByOrgId(int orgId);
	List<HRJob> getAgentJobsByOrgId(int orgId);
}
