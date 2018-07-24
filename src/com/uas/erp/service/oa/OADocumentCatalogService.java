package com.uas.erp.service.oa;


public interface OADocumentCatalogService {
	void save(String save, String  caller);
	void update(String update, String  caller);
	void delete(int id, String  caller);
}
