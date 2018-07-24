package com.uas.erp.dao.common;

import java.util.List;
import com.uas.erp.model.DocumentCatalog;
import com.uas.erp.model.Employee;

public interface DocumentCatalogDao {
	List<DocumentCatalog> getDocumentCatalogs();
	List<DocumentCatalog> getDocumentCatalogsByParentId(int parentId);
	List<DocumentCatalog> getDocumentCatalogsBySearch(String search);
//	List<DocumentCatalog> getDocumentCatalogsById(int id);
	void insertDocumentCatalog(DocumentCatalog dc, Employee employee);
	String getPathById(int id);
	DocumentCatalog getDocumentCatalogById(int id);
//	List<DocumentCatalog> getFileListById(int id);
	void deleteByVersion(int dcl_number, int dc_ParentId);
}
