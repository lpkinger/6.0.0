package com.uas.erp.service.scm;

public interface ProductLossSetService {
	void saveProductLossSet(String formStore, String param, String caller);
	
	void updateProductLossSetById(String formStore, String param,  String caller);

	void deleteProductLossSet(int pb_id, String caller);

	String auditProductLossSet(int pb_id, String caller);

	void resAuditProductLossSet(int pb_id, String caller);

	void submitProductLossSet(int pb_id, String caller);

	void resSubmitProductLossSet(int pb_id, String caller);
}