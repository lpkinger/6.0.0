package com.uas.erp.service.pm;

public interface ProductSMTService {
	void saveProductSMT(String formStore, String gridStore, String caller);

	void updateProductSMTById(String formStore, String gridStore, String caller);

	void deleteProductSMT(int ps_id, String caller);

	void printProductSMT(int ps_id, String caller);

	void auditProductSMT(int ps_id, String caller);

	void resAuditProductSMT(int ps_id, String caller);

	void submitProductSMT(int ps_id, String caller);

	void resSubmitProductSMT(int ps_id, String caller);
}
