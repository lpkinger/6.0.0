package com.uas.erp.service.scm;

public interface BrandVendorService {
	void saveBrandVendor(String formStore, String caller);
	void updateBrandVendor(String formStore, String caller);
	void deleteBrandVendor(int id, String caller);
	void auditBrandVendor(int id, String caller);
	void resAuditBrandVendor(int id, String caller);
	void submitBrandVendor(int id, String caller);
	void resSubmitBrandVendor(int id, String caller);
	void bannedBrandVendor(int id, String caller);
	void resBannedBrandVendor(int id, String caller);
}
