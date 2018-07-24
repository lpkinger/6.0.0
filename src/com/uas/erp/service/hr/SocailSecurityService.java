package com.uas.erp.service.hr;


public interface SocailSecurityService {
	
	void saveSocailSecurity(String formStore, String caller);
	
	void updateSocailSecurityById(String formStore, String caller);
	
	void deleteSocailSecurity(int or_id, String caller);

	void vastSocailsecu(String caller,String[] mark, int[] id);
}
