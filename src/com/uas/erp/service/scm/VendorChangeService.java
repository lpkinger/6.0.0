package com.uas.erp.service.scm;

public interface VendorChangeService {
	void saveVendorChange(String caller, String formStore, String gridStore);

	void updateVendorChangeById(String caller, String formStore, String gridStore);

	void deleteVendorChange(String caller, int vc_idid);

	void auditVendorChange(int vc_idid, String caller);

	void resAuditVendorChange(int vc_idid, String caller);

	void submitVendorChange(int vc_idid, String caller);

	void resSubmitVendorChange(int vc_idid, String caller);

	String[] printVendorChange(String caller, int sa_id, String reportName, String condition);
}
