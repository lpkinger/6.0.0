package com.uas.sysmng.service;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.JSONTree;

public interface SysmngUpgradeService {

	List<JSONTree> getJSONTreeByParentId(int parentId, String condition);

	void updateVersionLog(String id,String numid,String version,String remark,String name);
	int getUpgradeSqlCount(String condition);
	List<Map<String, Object>> getUpgradeSqlData(String condition,int page,int pageSize);
	List<Map<String, Object>> searchLog(String id);
	Map<String,Object> getUpgradeSql(String condition);
	Map<String,Object> saveUpgradeSql(String formStore);
	Map<String,Object> updateUpgradeSql(String formStore);
	void deleteUpgradeSqlByID(int id);
	Map<String,Object> checkSqls(int id,String sqls);

}
