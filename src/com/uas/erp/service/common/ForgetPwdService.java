package com.uas.erp.service.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface ForgetPwdService {

	public abstract Map<String, Object> sendChangePwdEmail(
			HttpServletRequest request, String em_code, String em_name) throws Exception;

	public abstract Map<String, Object> changePwd(String key) throws Exception;
	
	public Map<String, Object> updatePassword(String password, String em_code);

}