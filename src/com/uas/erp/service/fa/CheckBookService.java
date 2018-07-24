package com.uas.erp.service.fa;



public interface CheckBookService {
	void saveCheckBook(String formStore, String gridStore, String caller);
	void updateCheckBookById(String formStore, String gridStore, String caller);
	void deleteCheckBook(int ap_id, String caller);
}
