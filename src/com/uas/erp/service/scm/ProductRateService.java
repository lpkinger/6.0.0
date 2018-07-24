package com.uas.erp.service.scm;

public interface ProductRateService {
	void saveProductRate(String formStore, String gridStore, String caller);
	void updateProductRateById(String formStore, String gridStore, String caller);
	void deleteProductRate(int pdr_id, String caller);
	void printProductRate(int pdr_id, String caller);
	void auditProductRate(int pdr_id, String caller);
	void resAuditProductRate(int pdr_id,String caller );
	void submitProductRate(int pdr_id, String caller);
	void resSubmitProductRate(int pdr_id, String caller);
}
