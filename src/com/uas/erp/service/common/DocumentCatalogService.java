package com.uas.erp.service.common;

import java.util.List;

import com.uas.erp.model.DocumentCatalog;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;

public interface DocumentCatalogService {
	List<JSONTree> getJSONTree();
	List<JSONTree> getJSONTreeByParentId(int parentId, String language, Employee employee);
	List<JSONTree> getJSONTreeBySearch(String search);
	String getDocumentPath(String node_id);
	void insertDocumentCatalog(DocumentCatalog dc, Employee employee, String language);
	DocumentCatalog getDocumentCatalogById(int id);
	List<DocumentCatalog> getFileListById(int id);
	void deleteByVersion(int dcl_number, int dc_ParentId);
}
