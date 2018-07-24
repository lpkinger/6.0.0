package com.uas.erp.service.fa;



public interface CategoryBaseService {
	void saveCategoryBase(String formStore, String caller);
	void updateCategoryBaseById(String formStore, String caller);
	void deleteCategoryBase(int ca_id, String caller);
	void auditCategory(int ca_id, String caller);
	void resAuditCategory(int ca_id, String caller);
	void submitCategory(int ca_id, String caller);
	void resSubmitCategory(int ca_id, String caller);
	void bannedCategory(int ca_id, String caller);
	void resBannedCategory(int ca_id, String caller);
	String getDefaultCurrency();
}
