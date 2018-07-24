package com.uas.erp.service.pm;

public interface InquiryMouldService {
	void saveInquiryMould(String caller, String formStore, String param, String param2);

	void updateInquiryMouldById(String caller, String formStore, String param, String param2);

	void deleteInquiryMould(int in_id, String caller);

	void printInquiryMould(int in_id, String caller);

	void auditInquiryMould(int in_id, String caller);

	void resAuditInquiryMould(int in_id, String caller);

	void submitInquiryMould(int in_id, String caller);

	void resSubmitInquiryMould(int in_id, String caller);

	int turnPurcPrice(int in_id, String caller);

	void nullifybeforeCheck(int in_id);

	void nullifyInquiryMould(int in_id, String caller, String reason);

	void returnPriceMould(int in_id, int idd_id, int ind_id);

	void returnPriceMouldDet(int in_id, int idd_id);
}
