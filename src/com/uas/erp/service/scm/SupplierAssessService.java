package com.uas.erp.service.scm;

public interface SupplierAssessService {
	void saveSupplierAssess(String formStore, String caller);
	void deleteSupplierAssess(int sa_id, String caller);
	void updateSupplierAssess(String formStore, String caller);
	void submitSupplierAssess(int sa_id, String caller);
	void resSubmitSupplierAssess(int sa_id, String caller);
	void auditSupplierAssess(int sa_id, String caller);
	void resAuditSupplierAssess(int sa_id, String caller);
	void turnPreVendor(int sa_id, String caller);
}
