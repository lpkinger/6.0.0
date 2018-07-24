package com.uas.erp.service.crm;



public interface CustomerCommuService {
	void saveCustomerCommu(String formStore,String caller);
	void deleteCustomerCommu(int cc_id,String caller);
	void updateCustomerCommu(String formStore, 	String caller);
}
