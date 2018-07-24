package com.uas.erp.service.ma;


import net.sf.json.JSONObject;


public interface DBfindSetUIService {
	JSONObject getDbFindSetUIByField(String id);
	int saveDbFindSetUI(String formStore, String gridStore);
	int deleteDbFindSetUI(String id);
}
