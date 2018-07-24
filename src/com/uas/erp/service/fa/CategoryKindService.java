package com.uas.erp.service.fa;



public interface CategoryKindService {
	void saveCategoryKind(String formStore, String caller);
	void updateCategoryKindById(String formStore, String caller);
	void deleteCategoryKind(int ck_id, String caller);
}
