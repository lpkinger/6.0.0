package com.uas.erp.service.scm;

public interface ProductBrandService {
	void saveProductBrand(String formStore, String caller);
	void updateProductBrandById(String formStore, String caller);
	void deleteProductBrand(int id, String caller);
	void auditProductBrand(int id, String caller);
	void resAuditProductBrand(int id, String caller);
	void submitProductBrand(int id, String caller);
	void resSubmitProductBrand(int id, String caller);
	void bannedProductBrand(int id, String caller);
	void resBannedProductBrand(int id, String caller);
}
