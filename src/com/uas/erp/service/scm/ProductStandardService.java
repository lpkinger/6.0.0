package com.uas.erp.service.scm;

public interface ProductStandardService {
	void saveProductStandard(String formStore, String param, String caller);
	void deleteProductStandard(int id, String caller);
	void updateProductStandardById(String formStore, String param, String caller);
	void submitProductStandard(int id, String caller);
	void auditProductStandard(int id, String caller);
	void resAuditProductStandard(int id, String caller);
	void resSubmitProductStandard(int id, String caller);
}
