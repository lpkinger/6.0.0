package com.uas.erp.service.ma;


import java.util.List;
import java.util.Map;


public interface UpgradeSchemeService {
	List<Map<String, Object>> check(String ids);
	void saveUpgradeScheme(String param);
}
