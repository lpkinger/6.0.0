package com.uas.erp.service.crm;



public interface VendorDistrApplyService {
	void saveVendorDistrApply(String formStore,String gridStore,String caller);
	void deleteVendorDistrApply(int ca_id,String caller);
	void updateVendorDistrApply(String formStore,String gridStore,String caller);
	void submitVendorDistrApply(int ca_id,String caller);
	void resSubmitVendorDistrApply(int ca_id,String caller);
	void auditVendorDistrApply(int ca_id,String caller);
	void resAuditVendorDistrApply(int ca_id,String caller);
}
