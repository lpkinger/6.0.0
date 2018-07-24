package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

public interface CustomerService {

	List<Map<String, Object>>  getCustomerbySeller(String sellercode);

	List<Map<String, Object>> getCustomerDetail(String emcode, int start, int end, int type,int kind,int isSelected,String emplist);

	Map<String, Object> getCustomerbycode(String cu_code);
	void saveVisitPlan(String formStore, String caller);

	Map<String, Object> getRankList(String condition,Employee employee);
	
	public List<Map<String,Object>> getVisitPlan(Employee employee,String date,int page,int pageSize);
	public List<Map<String,Object>> getTaskPlan(String emcode,String date,String status);
	public List<Map<String,Object>> getScheduleMsg(String emcode,String date,String status);
	public Map<String,Object> getTaskAndScheduleMsg(String emcode,String date,String type);
	public Map<String,Object> getTaskAndScheduleAndVisitPlanMsg(String emcode,String date);
	
	Map<String, Object> getPersonalRank(String emcode, String yearmonth,Employee employee);

	Map<String, Object> getSalesKit(String emcode, String yearmonth,Employee employee);

	Map<String, Object> getTargets(String emcode, String yearmonth, int start, int end,Employee employee);

	Map<String, Object> getInactionCusts(String emcode, int start, int end,Employee employee);
	String updateMeeting(int ma_id);
	String updateVistPlan(int vp_id,String cu_nichestep,String cu_code,String nichecode,int vr_id);
	String updateMatype(String ma_code);
	
	List<Map<String,Object>> getStaffMsg(String emcode);

	Map<String, Object> getheadmanmsg(String emcode);
	
	public boolean ifConfigs(String caller,String code);
	
	public boolean ifOverRecv(String emcode);
	
	public Map<String,Object> ifBusinessDataBaseAdmin(String emcode);
    List<Map<String, Object>> openVersion();
    String updateSchedule(String code);

    List<Map<String, Object>>  getSchedule(String bccode, String emname);
    
    public List<Map<String,Object>> getNichecode(String cu_code,int start,int end,String custname);

	List<Map<String, Object>> getVisitType(String custname, String custcode);

	Map<String, Object> getDatasbycode(String custcode);

	List<Map<String, Object>> getContactPerson(String condition, int start,
			int end);

	List<Map<String, Object>> getBusinesschanceBewrite(String custcode,
			String custname, int start, int end);
	
	List<String> searchCustomer(String likestr, int start, int end);
}
