package com.uas.erp.service.hr;


public interface SocailAccountService {
	
	void saveSocailAccount(String formStore, String  caller);
	
	void updateSocailAccountById(String formStore, String  caller);
	
	void deleteSocailAccount(int or_id, String  caller);
	
	void vastSocailAccount(String  caller,String[] mark,int[] id);
}
