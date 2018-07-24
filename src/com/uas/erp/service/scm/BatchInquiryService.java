package com.uas.erp.service.scm;

public interface BatchInquiryService {
	void saveBatchInquiry(String formStore, String param1,String param2, String caller);
	
	void updateBatchInquiryById(String formStore, String param1,String param2,  String caller);

	void deleteBatchInquiry(int bi_id, String caller);

	String auditBatchInquiry(int bi_id, String caller);

	void resAuditBatchInquiry(int bi_id, String caller);

	void submitBatchInquiry(int bi_id, String caller);

	void resSubmitBatchInquiry(int bi_id, String caller);

}