package com.uas.erp.service.pm;

import net.sf.json.JSONObject;

import com.uas.erp.model.GridPanel;

public interface MRPDataService {
	void saveMRPData(String formStore, String caller);
	void updateMRPDataById(String formStore, String caller);
	void deleteMRPData(int md_id, String caller);
	void auditMRPData(int md_id, String caller);
	void resAuditMRPData(int md_id, String caller);
	void submitMRPData(int md_id, String caller);
	void resSubmitMRPData(int md_id, String caller);
	void updateFieldData(String caller,String data,String field ,String keyField,String keyValue);
	GridPanel getMRPThrowConfig(String caller, String condition);
	JSONObject getMrpData(String caller, String condition, int page, int start, int limit);
}
