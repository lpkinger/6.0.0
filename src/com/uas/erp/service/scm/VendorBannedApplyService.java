package com.uas.erp.service.scm;

public interface VendorBannedApplyService {
	void saveVendorBannedApply(String formStore, String caller);
	void updateVendorBannedApplyById(String formStore, String caller);
	void deleteVendorBannedApply(int vba_id, String caller);
	void auditVendorBannedApply(int vba_id, String caller);
	void resAuditVendorBannedApply(int vba_id, String caller);
	void submitVendorBannedApply(int vba_id, String caller);
	void resSubmitVendorBannedApply(int vba_id, String caller);
}
