package com.uas.erp.ac.service.common;

import java.util.Map;

public interface CompanyNewService {
	public Map<String, Object> checkCompanyExist(String name);
	public Map<String, Object> getInviteUrl();
	public Map<String, Object> getInviteUrl(String name, String vendusername, String userTel);
}
