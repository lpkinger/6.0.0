package com.uas.erp.service.ma;

import java.util.List;

import com.uas.erp.model.JSONTree;

public interface MASysNavigationService {
	List<JSONTree> getJSONTreeByParentId(int parentId, String condition);

	void save(String save);

	void update(String update);

	void delete(int id);
	
	void addRoot(String save);
}
