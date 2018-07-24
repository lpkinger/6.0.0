package com.uas.erp.service.fa;



public interface CategoryDepartmentService {
	void saveCategoryDepartment(String formStore, String gridStore, String caller);
	void updateCategoryDepartmentById(String formStore, String gridStore, String caller);
	void deleteCategoryDepartment(int ca_id, String caller);
}
