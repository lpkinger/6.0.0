package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

import com.uas.b2b.model.QuotationDetailDet;

public interface QuotationService {
	void saveQuotation(String formStore, String gridStore, String caller);
	void updateQuotationById(String formStore, String gridStore, String caller);
	void deleteQuotation(int qu_id, String caller);
	String[] printQuotation(int qu_id, String caller, String reportName, String condition);
	void auditQuotation(int qu_id, String caller);
	void resAuditQuotation(int qu_id, String caller);
	void submitQuotation(int qu_id, String caller);
	void resSubmitQuotation(int qu_id, String caller);
	void bannedQuotation(int qu_id, String caller);
	int turnSale(int qu_id, String caller);
	int toSalePrice(int qu_id, String caller);
	void resBannedQuotation(int qu_id, String caller);
	List<Map<String, Object>> getStepDet(Integer in_id);
	List<QuotationDetailDet> findReplyByInid(int id);
	void saveZDquotation(String formStore, String param, String param2, String caller);
	void deleteZDquotation(int id, String caller);
	void updateZDquotation(String formStore, String param,String param2, String caller);
	void submitZDquotation(int id, String caller);
	void resSubmitZDquotation(int id, String caller);
	void auditZDquotation(int id, String caller);
	void resAuditZDquotation(int id, String caller);
}
