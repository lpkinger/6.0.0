package com.uas.erp.service.scm;


public interface InvoiceService {
	void saveInvoice(String formStore, String gridStore);

	void updateInvoiceById(String formStore, String gridStore);

	void deleteInvoice(int in_id);

	String[] printInvoice(int in_id, String reportName, String condition);

	void auditInvoice(int in_id);

	void resAuditInvoice(int in_id);

	void submitInvoice(int in_id);

	void resSubmitInvoice(int in_id);

	void getSalePrice(int in_id);

	void savePreInvoice(String gridStore);

}
