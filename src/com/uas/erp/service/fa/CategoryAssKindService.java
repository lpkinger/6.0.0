package com.uas.erp.service.fa;



public interface CategoryAssKindService {
	void saveCategoryAssKind(String formStore, String gridStore, String caller);
	void updateCategoryAssKindById(String formStore, String gridStore, String caller);
	void deleteCategoryAssKind(int ca_id, String caller);
}
