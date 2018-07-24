package com.uas.erp.service.fa;



public interface ARBillBookService {
	void saveARBillBook(String formStore, String gridStore, String caller);
	void updateARBillBookById(String formStore, String gridStore, String caller);
	void deleteARBillBook(int abb_id, String caller);
	void printARBillBook(int abb_id, String caller);
	void auditARBillBook(int abb_id, String caller);
	void resAuditARBillBook(int abb_id, String caller);
	void submitARBillBook(int abb_id, String caller);
	void resSubmitARBillBook(int abb_id, String caller);


}
