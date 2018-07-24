package com.uas.erp.service.ma;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface EncryptService {

	public abstract Map<String, Object> getSob();
	
	public void updateConfigs(String value);
	
}