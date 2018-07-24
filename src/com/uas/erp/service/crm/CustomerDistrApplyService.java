package com.uas.erp.service.crm;



public interface CustomerDistrApplyService {
	void saveCustomerDistrApply(String formStore,String gridStore,String caller);
	void deleteCustomerDistrApply(int ca_id,String caller);
	void updateCustomerDistrApply(String formStore,String gridStore,String caller);
	void submitCustomerDistrApply(int ca_id,String caller);
	void resSubmitCustomerDistrApply(int ca_id,String caller);
	void auditCustomerDistrApply(int ca_id,String caller);
	void resAuditCustomerDistrApply(int ca_id,String caller);
}
