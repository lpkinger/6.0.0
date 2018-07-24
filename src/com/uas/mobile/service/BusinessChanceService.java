package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.uas.erp.model.Employee;

public interface BusinessChanceService {

	List<Map<String,Object>> getBusinessChanceByMonthAndProcess(String emcode,String currentdate,String currentprocess,int start,int end);
	
	List<Map<String, Object>> getBusinessChancebyMonth(String currentdate, Employee employee);

	List<Map<String, Object>> getBusinessChanceCombo(Employee employee,String caller, String field);

	List<Map<String, Object>>  getBusinessChanceStage(String condition);

	List<Map<String, Object>>  getBusinessChanceRecorder(String condition);

	void updatebusinessChanceData(String gridStore, String caller);

	void abateBusinessChance(int bcd_id, String caller);

	void updateBusinessChanceHouse(String bc_code, String bc_nichehouse);

	void updateBusinessChanceCust(String bc_code, String cu_code, String cu_name);

	void updateBusinessChanceDoman(String bc_code, String bc_doman,String bc_domancode);
	
	List<Map<String, Object>>  getnichedata(String bc_doman,int type,int pageIndex);
	int nichlecount(String bc_domancode,int type);
	String updateLastdate(String bc_code);
	List<Map<String, Object>> getNichehouse();
	String isadmin(String em_code);
	void isBusinesslimit(String bc_doman);
	void updateBusinessChanceDataMsg(String bc_code, String bc_doman,String bc_domancode,int type);

	Map<String, Object> getStagePoints(String bccode,String currentStep);

	Map<String, Object> addContactPerson(String caller, String formStore);

	void updateBusinessChanceType(String bc_code, String bc_nichehouse);

	List<Map<String, Object>> searchData(String stringSearch, int page, int end);
}