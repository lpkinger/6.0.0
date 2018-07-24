package com.uas.erp.service.oa;


public interface BookService {
	void saveBook(String formStore, String caller);
	void deleteBook(int bo_id, String caller);
	void updateBookdById(String formStore, String caller);
	void submitBook(int bo_id, String caller);
	void resSubmitBook(int bo_id, String caller);
	void auditBook(int bo_id, String caller);
	void resAuditBook(int bo_id, String caller);
	String turnBanned(String caller, String data);
}
