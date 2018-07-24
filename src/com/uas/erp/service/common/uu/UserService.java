package com.uas.erp.service.common.uu;

import com.uas.erp.model.Employee;
import com.uas.erp.model.MobileInfo;

public interface UserService {
	String getPlainPassword(String token, String date);

	String decryptPassword(String token, String date);

	void createUser(String username, String password);

	MobileInfo getMobileInfo(Employee employee);

	String getDBSetting(String code);

}
