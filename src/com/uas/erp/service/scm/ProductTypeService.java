package com.uas.erp.service.scm;

public interface ProductTypeService {
	void saveProductType(String formStore, String caller);
	void updateProductTypeById(String formStore, String caller);
	void deleteProductType(int pt_id, String caller);
}
