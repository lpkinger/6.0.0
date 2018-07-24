package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;

import com.uas.erp.model.Employee;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;
import com.uas.erp.model.RolePower;
import com.uas.erp.model.SpecialPower;
import com.uas.erp.model.SysSpecialPower;

public interface PowerDao {
	boolean getPowerByType(String caller, String type, String sob, Integer jobId);

	boolean getSelfPowerByType(String caller, String type, Employee employee);

	boolean getOtherPowerByType(String caller, int id, String type,
			Employee employee);

	boolean getOtherPowerByType(String caller, String formStore, String type,
			Employee employee);

	boolean getOtherPowerByType(String caller, String type, Employee employee,
			String condition);

	boolean getOtherSelfPowerByType(String caller, int id, String type,
			Employee employee);

	boolean getOtherSelfPowerByType(String caller, String type,
			Employee employee, String condition);

	boolean getSelfOtherPowerByType(String caller, String formStore,
			String type, Employee employee);

	List<PositionPower> getPositionPowersByCaller(String caller, String sob);

	List<RolePower> getRolePowersByCaller(String caller, String sob);

	List<PersonalPower> getPersonalPowersByCaller(String caller, String sob);

	List<PersonalPower> getPersonalPowersByEm(String caller, String emid);

	boolean getSpecialPowerByActionId(String ssp_id, Integer jobId, String sob);

	boolean getSelfSpecialPowerByActionId(String ssp_id, Employee employee);

	List<SysSpecialPower> getSysSpecialPowers(String caller, String sob);

	SysSpecialPower getSysSPower(String caller, String url, String sob);

	List<SpecialPower> getSpecialPowers(int ssp_id);

	/**
	 * 特殊权限 {caller@action, [ssp_id, ssp_business]}
	 * 
	 * @param sob
	 *            帐套名称
	 * @return
	 */
	Map<String, Object[]> getSpecialActions(String sob);

	/**
	 * 当前帐套不能编辑的界面的caller
	 * 
	 * @param sob
	 *            帐套
	 * @return
	 */
	Set<String> getUnEditableCallers(String sob);

	/**
	 * 岗位下属权限判断
	 * 
	 * @param caller
	 * @param type
	 * @param employee
	 * @return
	 */
	boolean getJobEmployeePowerByType(String caller, String type, String sob,
			Employee employee);

	boolean getJobEmployeePowerByType(String caller, String type, String sob,
			Integer jobId);

	boolean checkJobEmployeeExists(Integer jobId);

	public boolean checkDefaultHrJobPowerExists();
	
	String getRecorderCondition(String condition,String formRecorderField,Employee employee,boolean jobEmployee);
	
	boolean isMyForm(String caller, Employee employee, String condition,boolean jobEmployee);
	
	boolean getPowerByTypeIncludeExtraJob(String caller, String type, Employee employee);
	
	boolean isMyForm(String caller, Employee employee, String condition);
	
}
