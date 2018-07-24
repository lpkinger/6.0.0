package com.uas.erp.service.crm;



public interface CustomerDistrService {
	void saveCustomerDistr(String formStore,String gridStore,String caller);
	void deleteCustomerDistr(int cu_id,String caller);
	void updateCustomerDistr(String formStore,String gridStore,String caller);
}
