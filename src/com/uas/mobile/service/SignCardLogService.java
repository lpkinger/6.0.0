package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

public interface SignCardLogService {
	public Map<String, Object> getDutyTime(String emcode, String date);

	public Map<String, Object> getOutSet(String condition);

	public Map<String, Object> saveCardLog(String formStore, String caller);

	public Map<String, Object> saveOutPlan(String formStore, String caller,
			String param);

	public Map<String, Object> saveOutAddress(String formStore, String caller);

	public Map<String, Object> updateOutSet(String formStore, String caller);

	public Map<String, Object> ifAdmin(String emcode);

	public Map<String, Object> saveConfigs(String caller, String formStore);

	public Map<String, Object> getConfigs(String condition);

	public void updateConfigs(String caller, String formStore);

	public boolean ifInCompany(String emcode);

	public boolean ifNeedSignCard(String emcode);

	List<Map<String, Object>> getOutAddressDate(String condition,
			int pageIndex, int pageSize);

	public Map<String, Object> autoCardLog(String caller, String formStore);

	public Map<String, Object> savecomaddressset(String caller, String formStore);

	public Map<String, Object> updatecomaddressset(String caller,
			String formStore);

	public List<Map<String, Object>> getcomaddressset(String condition);

	public Map<String, Object> saveWorkDateTime(String caller, String formStore);

	public Map<String, Object> updateWorkDateTime(String caller,
			String formStore);

	public List<Map<String, Object>> getManAndDefaultor(String formStore);

	public List<Map<String, Object>> getAllWorkDate(String emcode);

	public Map<String, Object> deletecomaddressset(String caller, String id);

	public Map<String, Object> deleteWorkDate(String caller, String wdcode);

	public List<Map<String, Object>> myComPlan(String emcode);

	public List<Map<String, Object>> getPersonAttend(String emcode,
			String yearmonth);

	public Map<String, Object> getTeamAttend(String emcode,
			String yearmonth);

	public Map<String, Object> updateEmpWorkDate(String deptcodes,
			String emcodes, String date, String workcode);

	public Map<String, Object> deleteEmpworkDate(String deptcodes,
			String emcodes, String date, String workcode, String flag);
	
	public List<Map<String, Object>> effectiveWorkdata(String em_code, String date);
}
