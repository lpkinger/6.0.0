package com.uas.erp.service.scm;

public interface ProductMatchService {
	void saveProductMatch(String formStore, String caller);
	void updateProductMatchById(String formStore, String caller);
	void deleteProductMatch(int pm_id, String caller);

}
