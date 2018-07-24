package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.SearchTemplate;

public interface SearchDao {

	List<SearchTemplate> getSearchTemplates(String caller, String sob);

	SearchTemplate getSearchTemplate(Integer sId, String sob);
}
