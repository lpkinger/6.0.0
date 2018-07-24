package com.uas.erp.service.oa;


public interface ContactListTypeService {
	void saveContactListType(String formStore, String  caller);
	void updateContactListType(String formStore, String  caller);
	void deleteContactListType(int bd_id, String  caller);
}
