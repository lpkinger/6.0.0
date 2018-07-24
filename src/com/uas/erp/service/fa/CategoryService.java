package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.CheckBoxTree;

public interface CategoryService {
	List<String> getCateClass();
	List<CheckBoxTree> getAllCategoryTree(String caller, String condition);
	Map<String,Object> getToUi(String key ,String caller);
}
