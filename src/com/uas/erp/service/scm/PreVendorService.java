package com.uas.erp.service.scm;

public interface PreVendorService {
	void savePreVendor(String formStore);
	void updatePreVendorById(String formStore);
	void deletePreVendor(int ve_id);
	void auditPreVendor(int ve_id);
	void resAuditPreVendor(int ve_id);
	void submitPreVendor(int ve_id);
	void resSubmitPreVendor(int ve_id);
	int turnVendor(int ve_id);
	int turnVendorBase(int ve_id);
}
