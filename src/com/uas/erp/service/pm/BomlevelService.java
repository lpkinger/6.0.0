package com.uas.erp.service.pm;


public interface BomlevelService {
	
	void saveBomlevel(String formStore, String[] gridStore, String caller);
	
	void updateBomlevelById(String formStore, String[] gridStore, String caller);
	
	void deleteBomlevel(int id, String caller);
	
	void auditBomlevel(int id, String caller);
	
	void reauditBomlevel(int id, String caller);
	
	void submitBomlevel(int id, String caller);
	
	void resubmitBomlevel(int id, String caller);
	
	void updateBomleveldetail(int id,String caller,String param1, String param2,String  param3);
	
}
