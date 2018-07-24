package com.uas.erp.service.hr;


public interface WorkreasonService {
	
	void saveWorkreason(String formStore, String caller);
	
	void updateWorkreasonById(String formStore, String caller);
	
	void deleteWorkreason(int wr_id, String caller);
}
