package com.uas.erp.service.common;

public interface DocumentSetService {

	public abstract void saveDocSetting(String formStore, String caller);

	void deleteDocSetting(int ds_id, String caller);

	void updateDocSettingById(String formStore, String caller);

	void submitDocSetting(int ds_id, String caller);

	void resSubmitDocSetting(int ds_id, String caller);

	void auditDocSetting(int ds_id, String caller);

	void resAuditDocSetting(int ds_id, String caller);

	boolean documentManage(int id, String caller);
	
	boolean beforeResAudit(String caller, int id);

}