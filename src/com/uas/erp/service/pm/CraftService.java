package com.uas.erp.service.pm;

public interface CraftService {
	void saveCraft(String formStore, String gridStore, String caller);
	void updateCraftById(String formStore, String gridStore, String caller);
	void deleteCraft(int cr_id, String caller);
	void deleteDetail(int cr_id, String caller);
	void auditCraft(int cr_id, String caller);
	void resAuditCraft(int cr_id, String caller);
	void submitCraft(int cr_id, String caller);
	void resSubmitCraft(int cr_id, String caller);
	void saveStepCollection(String caller, String formStore, String param);
	void refreshCrafts(String code);
}
