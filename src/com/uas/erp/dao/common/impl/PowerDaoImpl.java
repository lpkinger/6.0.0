package com.uas.erp.dao.common.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.DataListDao;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.erp.model.RolePower;
import com.uas.erp.model.SpecialPower;
import com.uas.erp.model.SysSpecialPower;

@Repository("powerDao")
public class PowerDaoImpl extends BaseDao implements PowerDao {

	@Autowired
	private DataListDao dataListDao;
	
	@Override
	@Cacheable(value = "power", key = "#sob + '@' + #caller + #type + #jobId + 'getPowerByType'")
	public boolean getPowerByType(String caller, String type, String sob, Integer jobId) {
		SqlRowList rs = queryForRowSet("SELECT " + type + " FROM positionpower WHERE pp_caller=? AND pp_joid=?",
				caller, jobId);
		if (rs.next()) {
			return rs.getInt(type) == 1;
		}
		return false;
	}

	@Override
	@Cacheable(value = "positionpower", key = "#sob + '@' + #caller + 'getPositionPowersByCaller'")
	public List<PositionPower> getPositionPowersByCaller(String caller, String sob) {
		try {
			return getJdbcTemplate().query("select * from positionpower where pp_caller=?",
					new BeanPropertyRowMapper<PositionPower>(PositionPower.class), caller);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Cacheable(value = "power", key = "#employee.em_master + '@' + #caller + #type + #employee.em_id + 'getSelfPowerByType'")
	public boolean getSelfPowerByType(String caller, String type, Employee employee) {
		SqlRowList rs = queryForRowSet("SELECT " + type + " FROM personalpower WHERE pp_caller=? AND pp_emid=?",
				caller, employee.getEm_id());
		if (rs.next()) {
			return rs.getInt(type) == 1;
		}
		return false;
	}

	@Cacheable(value = "positionpower", key = "#sob+ '@' + #caller + 'getPersonalPowersByCaller'")
	public List<PersonalPower> getPersonalPowersByCaller(String caller, String sob) {
		try {
			return getJdbcTemplate().query("select * from personalpower where pp_caller=?",
					new BeanPropertyRowMapper<PersonalPower>(PersonalPower.class), caller);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Cacheable(value = "rolepower", key = "#sob+ '@' + #caller + 'getRolePowersByCaller'")
	public List<RolePower> getRolePowersByCaller(String caller, String sob) {
		try {
			return getJdbcTemplate().query("select * from rolepower where pp_caller=?",
					new BeanPropertyRowMapper<RolePower>(RolePower.class), caller);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<PersonalPower> getPersonalPowersByEm(String caller, String emid) {
		try {
			return getJdbcTemplate().query(
					"select * from personalpower where pp_caller=? AND pp_emid in(" + emid + ")",
					new BeanPropertyRowMapper<PersonalPower>(PersonalPower.class), caller);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public boolean getOtherPowerByType(String caller, int id, String type, Employee employee) {
		boolean bool = getPowerByType(caller, type, employee.getEm_master(), employee.getEm_defaulthsid());
		if (bool) {
			return true;
		} else {
			bool = checkExtraJobPower(caller, type, employee);
			if (bool)
				return true;
			return isMyForm(caller, employee, id);
		}
	}

	@Override
	public boolean getOtherSelfPowerByType(String caller, int id, String type, Employee employee) {
		boolean bool = getSelfPowerByType(caller, type, employee);
		if (bool) {
			return true;
		} else {
			return isMyForm(caller, employee, id);
		}
	}

	@Override
	public boolean getOtherPowerByType(String caller, String formStore, String type, Employee employee) {
		boolean bool = getPowerByType(caller, type, employee.getEm_master(), employee.getEm_defaulthsid());
		if (bool) {
			return true;
		} else {
			bool = checkExtraJobPower(caller, type, employee);
			if (bool)
				return true;
			return isMyForm(caller, formStore, employee);
		}
	}

	public boolean getSelfOtherPowerByType(String caller, String formStore, String type, Employee employee) {
		boolean bool = getSelfPowerByType(caller, type, employee);
		if (bool) {
			return true;
		} else {
			return isMyForm(caller, formStore, employee);
		}
	}

	public boolean getOtherPowerByType(String caller, String type, Employee employee, String condition) {
		boolean bool = getPowerByType(caller, type, employee.getEm_master(), employee.getEm_defaulthsid());
		if (bool) {
			return true;
		} else {
			bool = checkExtraJobPower(caller, type, employee);
			if (bool)
				return true;
			return isMyForm(caller, employee, condition);
		}
	}

	@Override
	public boolean getPowerByTypeIncludeExtraJob(String caller, String type, Employee employee) {
		boolean bool = getPowerByType(caller, type, employee.getEm_master(), employee.getEm_defaulthsid());
		if (bool) {
			return true;
		} else {
			bool = checkExtraJobPower(caller, type, employee);
			if (bool)
				return true;
		}
		return false;
	}
	
	public boolean getOtherSelfPowerByType(String caller, String type, Employee employee, String condition) {
		boolean bool = getSelfPowerByType(caller, type, employee);
		if (bool) {
			return true;
		} else {
			return isMyForm(caller, employee, condition);
		}
	}

	/**
	 * 检查额外的岗位的权限
	 * 
	 * @param caller
	 * @param type
	 * @param employee
	 * @return
	 */
	private boolean checkExtraJobPower(String caller, String type, Employee employee) {
		boolean bool = false;
		if (employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = getPowerByType(caller, type, employee.getEm_master(), empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}

	private boolean isMyForm(String caller, Employee employee, int id) {
		SqlRowList list = queryForRowSet("SELECT fo_table,fo_keyfield,fo_recorderfield FROM Form WHERE fo_caller=?",
				caller);
		if (list.next()) {
			String rField = list.getString("fo_recorderfield");
			String kField = list.getString("fo_keyfield");
			String tab = list.getString("fo_table");
			if (rField != null && kField != null && tab != null) {
				Object emVal = employee.getEm_id(); // recorderfield默认与em_id对应
				if (rField.endsWith("@C")) { // recorderfield与em_code对应
					rField = rField.substring(0, rField.lastIndexOf("@C"));
					emVal = employee.getEm_code();
				} else if (rField.endsWith("@N")) { // recorderfield与em_name对应
					rField = rField.substring(0, rField.lastIndexOf("@N"));
					emVal = employee.getEm_name();
				}
				StringBuffer sb = new StringBuffer(kField);
				sb.append("=");
				sb.append(id);
				sb.append(" AND ");
				sb.append(rField);
				sb.append("='");
				sb.append(emVal);
				sb.append("'");
				return !checkByCondition(tab, sb.toString());
			}
			return true;
		}
		return true;
	}

	@Override
	public String getRecorderCondition(String condition,String formRecorderField,Employee employee,boolean jobEmployee){
		String[] recorderFields = formRecorderField.split(",");
		Object emVal = null;
		String hrJobConditionField = null;
		String recorderFieldDecode = null;
		String hrJobRecorderCondition = null;
		String selfRecorderCondition = null;
		StringBuilder hrJobCondition = new StringBuilder();
		StringBuilder selfCondition = new StringBuilder();
		
		for(String recorderField:recorderFields){
			if(!"".equals(recorderField)){
				recorderField = recorderField.trim();
				if(recorderField.indexOf("@")>-1){
					recorderFieldDecode = recorderField.substring(0,recorderField.indexOf("@")).trim();
				}else{
					recorderFieldDecode = recorderField.trim();
				}
				
				if(recorderField.endsWith("@C")){
					emVal = employee.getEm_code();
					hrJobConditionField = "hj_em_code";
				}else if(recorderField.endsWith("@N")){
					emVal = employee.getEm_name();
					hrJobConditionField = "hj_em_name";
				}else{
					emVal = employee.getEm_id();
					hrJobConditionField = "hj_em_id";
				}

				if(recorderFieldDecode.length()>0){
					selfCondition.append(recorderFieldDecode + "='" + emVal + "' or ");
					
					if(jobEmployee){
						hrJobCondition.append(recorderFieldDecode + " in (select "+hrJobConditionField+" from " + Constant.TEMP_TABLE_NAME + ") or ");
					}					
				}
			}
		}
		
		if(selfCondition.length()>0){
			selfRecorderCondition = selfCondition.substring(0,selfCondition.lastIndexOf(" or "));		
			
			if(condition == null){
				condition = "";
			}else if (condition != null && condition.trim().length() > 0) {
				condition += " and ";
			}
			
			if(jobEmployee){
				hrJobRecorderCondition = hrJobCondition.substring(0,hrJobCondition.lastIndexOf(" or "));
				
				condition += " ("+hrJobRecorderCondition+")";
			}else{
				condition += " (" + selfRecorderCondition + ")";
			}
		}	
		return condition;
	}
	
	@Override
	public boolean isMyForm(String caller, Employee employee, String condition,boolean jobEmployee) {
		SqlRowList list = queryForRowSet("SELECT fo_table,fo_keyfield,fo_recorderfield FROM Form WHERE fo_caller=?",caller);
		if(list.next()){
			String formRecorderField = list.getString("fo_recorderfield");
			String keyField = list.getString("fo_keyfield");
			
			String table = list.getString("fo_table");
			
			if(formRecorderField!=null&&keyField!=null&&table!=null){
				condition = getRecorderCondition(condition,formRecorderField,employee,jobEmployee);
				
				return checkByConditionAndJobEmployee(table, condition,employee,jobEmployee);
			}
			return true;
		}
		return true;
	}
	
	private boolean checkByConditionAndJobEmployee(String table,String condition,Employee employee,boolean jobEmployee){
		if(jobEmployee){
			String sqlTemp = dataListDao.getSqlWithJobEmployee(employee);
			SqlRowList rs = queryForRowSet(sqlTemp + " select * from " + table + " where " + condition);
			if(rs.next()){
				return true;
			}
			return false;
		}else{
			return !checkByCondition(table, condition);
		}
	}
	
	@Override
	public boolean isMyForm(String caller, Employee employee, String condition) {
		SqlRowList list = queryForRowSet("SELECT fo_table,fo_keyfield,fo_recorderfield FROM Form WHERE fo_caller=?",
				caller);
		if (list.next()) {
			String rField = list.getString("fo_recorderfield");
			String kField = list.getString("fo_keyfield");
			String tab = list.getString("fo_table");
			if (rField != null && kField != null && tab != null) {
				Object emVal = employee.getEm_id(); // recorderfield默认与em_id对应
				if (rField.endsWith("@C")) { // recorderfield与em_code对应
					rField = rField.substring(0, rField.lastIndexOf("@C"));
					emVal = employee.getEm_code();
				} else if (rField.endsWith("@N")) { // recorderfield与em_name对应
					rField = rField.substring(0, rField.lastIndexOf("@N"));
					emVal = employee.getEm_name();
				}
				StringBuffer sb = new StringBuffer(rField);
				sb.append("='");
				sb.append(emVal);
				sb.append("' AND ");
				sb.append(condition);
				return !checkByCondition(tab, sb.toString());
			}
			return true;
		}
		return true;
	}

	private boolean isMyForm(String caller, String formStore, Employee employee) {
		SqlRowList list = queryForRowSet("SELECT fo_table,fo_keyfield,fo_recorderfield FROM Form WHERE fo_caller=?",
				caller);
		if (list.next()) {
			String rField = list.getString("fo_recorderfield");
			String kField = list.getString("fo_keyfield");
			String tab = list.getString("fo_table");
			if (rField != null && kField != null && tab != null) {
				Object emVal = employee.getEm_id(); // recorderfield默认与em_id对应
				if (rField.endsWith("@C")) { // recorderfield与em_code对应
					rField = rField.substring(0, rField.lastIndexOf("@C"));
					emVal = employee.getEm_code();
				} else if (rField.endsWith("@N")) { // recorderfield与em_name对应
					rField = rField.substring(0, rField.lastIndexOf("@N"));
					emVal = employee.getEm_name();
				}
				Map<Object, Object> map = BaseUtil.parseFormStoreToMap(formStore);
				/**
				 * rField未必配置在了FormDetail中，不能直接return
				 * employee.getEm_code().equals(map.get(rField));
				 */
				StringBuffer sb = new StringBuffer(kField);
				sb.append("=");
				sb.append(map.get(kField));
				sb.append(" AND ");
				sb.append(rField);
				sb.append("='");
				sb.append(emVal);
				sb.append("'");
				return !checkByCondition(tab, sb.toString());
			}
			return true;
		}
		return true;
	}

	@Override
	@Cacheable(value = "specialpower", key = "#sob + '@' + #ssp_id + 'getSpecialPowerByActionId' + #jobId ")
	public boolean getSpecialPowerByActionId(String ssp_id, Integer jobId, String sob) {
		return checkIf("SpecialPower", "sp_sspid in(" + ssp_id + ") AND sp_joid=" + jobId);
	}

	@Override
	@Cacheable(value = "specialpower", key = "#employee.em_master + '@' + #ssp_id + 'getSelfSpecialPowerByActionId' + #employee.em_id ")
	public boolean getSelfSpecialPowerByActionId(String ssp_id, Employee employee) {
		return checkIf("SpecialPower", "sp_sspid in(" + ssp_id + ") AND sp_emid=" + employee.getEm_id());
	}

	@Override
	@Cacheable(value = "sysspecialpowers", key = "#sob + '@' + #caller+ 'getSysSpecialPowers'")
	public List<SysSpecialPower> getSysSpecialPowers(String caller, String sob) {
		try {
			return getJdbcTemplate().query("SELECT * FROM SysSpecialPower WHERE ssp_caller=?",
					new BeanPropertyRowMapper<SysSpecialPower>(SysSpecialPower.class), caller);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<SpecialPower> getSpecialPowers(int ssp_id) {

		return null;
	}

	@Override
	@Cacheable(value = "specialactions", key = "#sob + '@specialactions'")
	public Map<String, Object[]> getSpecialActions(String sob) {
		try {
			SqlRowList rs = queryForRowSet("SELECT ssp_action, wmsys.wm_concat(ssp_id), ssp_business FROM SysSpecialPower where ssp_valid=-1 group by ssp_action,ssp_business");
			Map<String, Object[]> map = new HashMap<String, Object[]>();
			while (rs.next()) {
				map.put(rs.getString(1), new Object[] { rs.getString(2), rs.getInt(3) });
			}
			return map;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@Cacheable(value = "basedataset", key = "#sob + '@basedataset'")
	public Set<String> getUnEditableCallers(String sob) {
		Set<String> callers = new HashSet<String>();
		SqlRowList rs = queryForRowSet("select bds_caller from basedataset where nvl(bds_editable,0)=1");
		while (rs.next()) {
			callers.add(rs.getGeneralString(1));
		}
		return callers;
	}
   
	@Override
	@Cacheable(value = "sysspecialpowers", key = "#sob + '@' + #caller + #url")
	public SysSpecialPower getSysSPower(String caller, String url, String sob) {
		try {
			return getJdbcTemplate().queryForObject("SELECT * FROM SysSpecialPower WHERE ssp_caller=? and ssp_action=?",
					new BeanPropertyRowMapper<SysSpecialPower>(SysSpecialPower.class), caller,url);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@Cacheable(value = "power", key = "#employee.em_master + '@' + #caller + #type + #employee.em_id + 'getJobEmployeePowerByType'")
	public boolean getJobEmployeePowerByType(String caller, String type,
			String sob, Employee employee) {
		SqlRowList rs = queryForRowSet("SELECT " + type + " FROM personalpower WHERE pp_caller=? AND pp_emid=?",
				caller, employee.getEm_id());
		if (rs.next()) {
			return rs.getInt(type) == 1;
		}
		return false;
	}

	@Override
	@Cacheable(value = "power", key = "#sob + '@' + #caller + #type + #jobId + 'getJobEmployeePowerByType'")
	public boolean getJobEmployeePowerByType(String caller, String type,
			String sob, Integer jobId) {
		SqlRowList rs = queryForRowSet("SELECT " + type + " FROM positionpower WHERE pp_caller=? AND pp_joid=?",
				caller, jobId);
		if (rs.next()) {
			return rs.getInt(type) == 1;
		}
		return false;
	}

	@Override
	@Cacheable(value = "power", key = "#jobId + 'checkJobEmployeeExists'")
	public boolean checkJobEmployeeExists(Integer jobId) {
		int count = getJdbcTemplate().queryForObject("select count(*) count from hrjobemployees where hj_joid=?",Integer.class,jobId);
		if(count>0){
			return true;
		}
		return false;
	}


	@Override
	@Cacheable(value = "configs", key = "'checkDefaultHrJobPower'")
	public boolean checkDefaultHrJobPowerExists() {
		return isDBSetting("defaultHrJobPowerExists");
	}

}
