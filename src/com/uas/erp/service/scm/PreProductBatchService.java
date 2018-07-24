package com.uas.erp.service.scm;

public interface PreProductBatchService {
	void savePreProductBatch(String formStore, String param, String caller);
	
	void updatePreProductBatchById(String formStore, String param,  String caller);

	void deletePreProductBatch(int pb_id, String caller);

	String auditPreProductBatch(int pb_id, String caller);

	void resAuditPreProductBatch(int pb_id, String caller);

	void submitPreProductBatch(int pb_id, String caller);

	void resSubmitPreProductBatch(int pb_id, String caller);

	void catchProdCode(int pb_id, String caller);
}