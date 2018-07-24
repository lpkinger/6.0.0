package com.uas.erp.service.crm;



public interface CustomerImpDistrApplyService {
	void saveCustomerImpDistrApply(String formStore,String gridStore,String caller);
	void deleteCustomerImpDistrApply(int ca_id,String caller);
	void updateCustomerImpDistrApply(String formStore,String gridStore,String caller);
	void submitCustomerImpDistrApply(int ca_id,String caller);
	void resSubmitCustomerImpDistrApply(int ca_id,String caller);
	void auditCustomerImpDistrApply(int ca_id,String caller);
	void resAuditCustomerImpDistrApply(int ca_id,String caller);
}
