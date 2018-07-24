package com.uas.erp.service.oa;


public interface OADocumentPowerService {
	
	void save(String save, String table, String otherField, Object[] otherValues, String  caller);
	void update(String update, String table, String keyField, String  caller);
	void delete(int id, String caller);

}
