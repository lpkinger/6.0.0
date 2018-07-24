package com.uas.erp.service.ma;

import java.util.List;

import net.sf.json.JSONObject;

public interface HelpDocService {
	void saveDoc(String data);
	JSONObject getHelpInfo(String caller);
	String getHelpDoc(String caller);
	List<JSONObject> getUpdateLogs(String caller);
	void download();
}
