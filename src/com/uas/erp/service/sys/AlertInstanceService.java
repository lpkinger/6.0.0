package com.uas.erp.service.sys;

import java.util.List;

import net.sf.json.JSONObject;

public interface AlertInstanceService {
	
	public void save(String caller, String baseFormStore, String paramFormStore, String assignGridRecord);

	public void update(String caller, String baseFormStore, String paramFormStore, String assignGridRecord);

	public void submit(String caller, int id);
	
	public void resSubmit(String caller, int id);
	
	public void audit(String caller, int id);
	
	public void resAudit(String caller, int id);
	
	public void banned(String caller, int id);
	
	public void resBanned(String caller, int id);
	
	public void delete(String caller, int id);
	
	public List<JSONObject> getParamItems(String itemId, String instanceId);
	
	public List<JSONObject> getAssign(String instanceId);
	
	public List<JSONObject> getOutputParams(String itemId);
}
