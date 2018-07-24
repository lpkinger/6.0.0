package com.uas.erp.service.scm;

public interface BorrowApplyService {
	void saveBorrowApply(String formStore, String gridStore, String caller);
	void updateBorrowApplyById(String formStore, String gridStore, String caller);
	void deleteBorrowApply(int ba_id, String caller);
	void printBorrowApply(int ba_id, String caller);
	void auditBorrowApply(int ba_id, String caller);
	void resAuditBorrowApply(int ba_id, String caller);
	void submitBorrowApply(int ba_id, String caller);
	void resSubmitBorrowApply(int ba_id, String caller);
	int turnBorrow(int id, String caller);
	String turnProdBorrow(String data, String caller);
}
