package com.uas.erp.service.hr;


public interface KpibillChangeService {
	void saveKpibillChange(String formStore, String caller);
	void deleteKpibillChange(int kbc_id, String caller);
	void updateKpibillChange(String formStore, String caller);
	void submitKpibillChange(int kbc_id, String caller);
	void resSubmitKpibillChange(int kbc_id, String caller);
	void auditKpibillChange(int kbc_id, String caller);
}
