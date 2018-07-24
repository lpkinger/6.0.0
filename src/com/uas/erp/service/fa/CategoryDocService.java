package com.uas.erp.service.fa;



public interface CategoryDocService {
	void saveCategoryDoc(String formStore, String caller);
	void updateCategoryDocById(String formStore, String caller);
	void deleteCategoryDoc(int cd_id, String caller);
}
