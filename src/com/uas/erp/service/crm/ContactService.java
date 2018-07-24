package com.uas.erp.service.crm;



public interface ContactService {
	void saveContact(String formStore, String gridStore,String caller);
	void deleteContact(int id,String caller);
	void updateContactById(String formStore,String gridStore,String caller);
}
