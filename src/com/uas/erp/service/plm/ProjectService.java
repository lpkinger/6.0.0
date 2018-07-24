package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ProjectService {
	void saveProject(String formStore);
	void deleteProject(int prj_id);
	void updateProject(String formStore);
	void submitProject(int prj_id);
	void auditProject(int prj_id,String caller);
	String TurnProjectreview(int id);
	void resSubmitProject(int id);
	void resAuditProject(int id);
	void updateProjectjzxh(int prj_id,String prj_sptext70,String caller);
	Map<Object, List<JSONObject>> getPhases(String condition);
}
