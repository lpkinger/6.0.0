package com.uas.erp.service.salary;


import java.util.Map;
import com.uas.erp.model.Employee;

public interface SalaryService {

	void sendMsg(String ilid,String text,String date, int signature);

	Map<String,Object> getHistory(String date, String condition, int start, int end);

	void toFormalData(Employee employee, String date,Integer id, Integer start, Integer end,String type);

	void reSend(String form);

	void deleteData(String ids);

	Map<String, Object> exportAllHis(String date);

	Map<String,Object> verify(String phone, String type);

	Map<String,Object> login(String emcode, String password, String value, Object ve_code);

	Map<String, Object> modifyPwd(String emcode, String password,
			String phonecode, Object attribute);
	
	Map<String, Object> getMessgeLog(int page, int start, int limit);

	void saveDate(String ilid, String date, String text, Integer signature);

}
