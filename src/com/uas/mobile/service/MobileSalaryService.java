package com.uas.mobile.service;

import java.util.Map;

import com.uas.erp.model.Employee;

public interface MobileSalaryService {

	Map<String,Object> getEmSalary(String emcode, String date,String phone);

	void updateSalary(boolean result, String sl_id, int fp_id, String msg, Employee employee);

	boolean changePassword(String em_uu, String em_code, String phone,String pwd);

}
