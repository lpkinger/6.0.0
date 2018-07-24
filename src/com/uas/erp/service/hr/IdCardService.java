package com.uas.erp.service.hr;


public interface IdCardService {
	
	void saveIdCard(String formStore, String  caller);
	
	void updateIdCardById(String formStore, String  caller);
	
	void deleteIdCard(int ic_id, String  caller);
}
