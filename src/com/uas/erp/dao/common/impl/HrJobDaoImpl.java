package com.uas.erp.dao.common.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.HrJobDao;
import com.uas.erp.model.HRJob;
import com.uas.erp.model.LimitFields;
import com.uas.erp.model.Role;
import com.uas.erp.model.RoleLimitFields;
import com.uas.erp.model.SelfLimitFields;

@Repository("hrjobDao")
public class HrJobDaoImpl extends BaseDao implements HrJobDao {

	@Override
	@Cacheable(value = "hrjob", key = "#sob + '@hrjob'")
	public List<HRJob> getHrJobs(String sob) {
		try {
			return getJdbcTemplate("Job").query("select * from job where nvl(jo_statuscode,' ')<>'DISABLE' order by NLSSORT(jo_name,'NLS_SORT = SCHINESE_PINYIN_M ') asc ",
					new BeanPropertyRowMapper<HRJob>(HRJob.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<Role> getRoles(String sob) {
		try {
			return getJdbcTemplate("Job").query("select * from role where nvl(ro_statuscode,' ')<>'DISABLE' order by ro_id desc ",
					new BeanPropertyRowMapper<Role>(Role.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<HRJob> getJobsByOrgId(int orgId) {
		try {
			return getJdbcTemplate("Job").query("select * from job where nvl(jo_statuscode,' ')<>'DISABLE' and jo_orgid="+orgId+" and nvl(jo_orgid,0)<>0 order by NLSSORT(jo_name,'NLS_SORT = SCHINESE_PINYIN_M ') asc ",
					new BeanPropertyRowMapper<HRJob>(HRJob.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public List<HRJob> getAgentJobsByOrgId(int orgId) {
		try {
			return getJdbcTemplate("Job").query("select * from job where nvl(jo_statuscode,' ')<>'DISABLE' and jo_orgid="+orgId+" and nvl(jo_orgid,0)<>0  and nvl(ISAGENT,0)=-1 order by NLSSORT(jo_name,'NLS_SORT = SCHINESE_PINYIN_M ') asc ",
					new BeanPropertyRowMapper<HRJob>(HRJob.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public int getJoIdByEmId(int em_id) {
		String sql = "select em_defaulthsid from Employee where em_id=?";
		SqlRowList list = queryForRowSet(sql, em_id);
		if (list.next()) {
			int jo_id = list.getInt(1);
			if (jo_id >= 0) {
				return jo_id;
			}
		}
		return -1;
	}

	@Override
	@Cacheable(value = "limitfields", key = "#sob + '@' + #caller + #jo_id + 'getLimitFieldsByCaller'")
	public List<LimitFields> getLimitFieldsByCaller(String caller, int jo_id, String sob) {
		try {
			return getJdbcTemplate().query("select * from LimitFields WHERE (lf_caller=? or (lf_caller in(select mad_caller from mobileauditdetail where mad_mancaller =? ) and lf_isform=0)"
					+ "  or  (lf_caller like (?) and lf_isform=3)) AND lf_joid=?",//支持多从表
					new BeanPropertyRowMapper<LimitFields>(LimitFields.class), caller, caller,caller+"|%",jo_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	@Cacheable(value = "limitfields", key = "#sob + '@' + #caller + '&' + #relativeCaller +'job'+#jo_id +'type'+ #isForm + 'getLimitFieldsByType'")
	public List<LimitFields> getLimitFieldsByType(String caller, String relativeCaller, int isForm, Integer jo_id, String sob) {
		List<LimitFields> list =new ArrayList<LimitFields>();
		if (jo_id == null) {
			return list;
		}
		try { 
			relativeCaller = !StringUtil.hasText(relativeCaller) ? caller : relativeCaller;
			return getJdbcTemplate().query("select * from LimitFields WHERE (lf_caller=? or lf_caller=?) AND lf_joid=? AND lf_isform=?",
					new BeanPropertyRowMapper<LimitFields>(LimitFields.class), caller, relativeCaller, jo_id, isForm);
		} catch (Exception e) {
			return list;
		}
	}


	@Override
	@Cacheable(value = "rolelimitfields", key = "#sob + '@' + #caller + '&' + #relativeCaller +'role'+#ro_id +'type'+ #isForm + 'getRoleLimitFieldsByType'")
	public List<RoleLimitFields> getRoleLimitFieldsByType(String caller,
			String relativeCaller, int isForm, Integer ro_id, String sob) {
		List<RoleLimitFields> list =new ArrayList<RoleLimitFields>();
		if (ro_id == null) {
			return list;
		}
		try { 
			relativeCaller = !StringUtil.hasText(relativeCaller) ? caller : relativeCaller;
			return getJdbcTemplate().query("select * from RoleLimitFields WHERE (lf_caller=? or lf_caller=?) AND lf_roid=? AND lf_isform=?",
					new BeanPropertyRowMapper<RoleLimitFields>(RoleLimitFields.class), caller, relativeCaller, ro_id, isForm);
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
	}
	
	@Override
	@Cacheable(value = "rolelimitfields", key = "#sob + '@' + #caller + #ro_id + 'getRoleLimitFieldsByCaller'")
	public List<RoleLimitFields> getRoleLimitFieldsByCaller(String caller,
			Integer ro_id, String sob) {
		try {
			return getJdbcTemplate().query("select * from RoleLimitFields WHERE (lf_caller=? or (lf_caller in(select mad_caller from mobileauditdetail where mad_mancaller =? ) and lf_isform=0)"
					+ "  or  (lf_caller like (?) and lf_isform=3)) AND lf_roid=?",//支持多从表
					new BeanPropertyRowMapper<RoleLimitFields>(RoleLimitFields.class), caller, caller,caller+"|%",ro_id);
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<SelfLimitFields> getSelfLimitFieldsByCaller(String caller, Integer em_id) {
		if (em_id == null) {
			return null;
		}
		try {
			return getJdbcTemplate().query("select * from SelfLimitFields WHERE (lf_caller=? or (lf_caller like (?) and lf_isform=3)) AND lf_emid = ?",
					new BeanPropertyRowMapper<SelfLimitFields>(SelfLimitFields.class), caller,caller+"|%", em_id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Cacheable(value = "limitfields", key = "#sob + '@' + #caller + #em_id + #isForm + 'getSelfLimitFieldsByEmId'")
	public List<SelfLimitFields> getSelfLimitFieldsByType(String caller, int isForm, Integer em_id, String sob) {
		if (em_id == null) {
			return null;
		}
		try {
			return getJdbcTemplate().query("select * from SelfLimitFields WHERE lf_caller=? AND lf_emid =? AND lf_isform=?",
					new BeanPropertyRowMapper<SelfLimitFields>(SelfLimitFields.class), caller, em_id, isForm);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public HRJob getHrJob(int id) {
		// TODO Auto-generated method stub
		try {
			return getJdbcTemplate("Job").queryForObject("select * from job where jo_id=?",
					new BeanPropertyRowMapper<HRJob>(HRJob.class),id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public HRJob getParentJob(int jobid) {
		// TODO Auto-generated method stub
		try {
			return getJdbcTemplate("Job").queryForObject("select * from job where jo_id =(select  jo_subof from job a where a.jo_id="+jobid+")",
					new BeanPropertyRowMapper<HRJob>(HRJob.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
