package com.uas.erp.service.fs;

public interface FsInvoiceService {
	void saveFsInvoice(String formStore, String caller);

	void updateFsInvoice(String formStore, String caller);

	void deleteFsInvoice(int id, String caller);

	void submitFsInvoice(int id, String caller);

	void resSubmitFsInvoice(int id, String caller);

	void auditFsInvoice(int id, String caller);

	void resAuditFsInvoice(int id, String caller);

}
