package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.FormAttach;
import com.uas.erp.model.FormPanel;
import com.uas.erp.model.JSONTree;

public interface RecordService {
	FormPanel getFormItemsByCaller(String caller, String condition, String language);

	void saveWorkRecord(String formStore, String language, Employee employee);

	void updateWorkRecord(String formStore, String language);

	void updateBillRecord(Integer wr_raid, String wr_redcord, String language, Employee employee);
	
	void endBillTask(Integer ra_id, Integer id,String record, String language, Employee employee);
	
	void changeBillTask(Integer ra_id, Integer em_id, String language, Employee employee);

	List<JSONTree> getJSONResource(int id, String language);

	List<FormAttach> getFormAttachs(String condition);

	List<JSONTree> getJSONRecord(String condition, String language);

	void resSubmitWorkRecord(int id, String language, Employee employee);

	String getRecordData(int id, Employee employee, String language);

	void submitWorkRecord(int id, String language, Employee employee);

	void confirmBillTask(Integer ra_id, Integer id,String record, String language, Employee employee);

	void noConfirmBillTask(Integer ra_id, Integer id, String record, String language, Employee employee);
	public List<Map<String, Object>> getMsg(Integer ra_id);

	Map<String,Object> getTaskFiles(Integer id);
	
	public void auditWorkRecord(int id,String caller);
	
	public void submitWorkRecordFlow(int id);
	
	public void taskTransfer(String ids,String from,String to);

	public List<Map<String,Object>> loadRelationData(String id);
}
