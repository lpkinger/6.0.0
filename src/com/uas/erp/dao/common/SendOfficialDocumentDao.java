package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.SendOfficialDocument;

public interface SendOfficialDocumentDao {
	
	void insertSOD(SendOfficialDocument sod);
	
	SendOfficialDocument getSODById(int id);
	
	void delete(int rod_id);
	List<SendOfficialDocument> getByCondition(String condition, int page, int pageSize);
	int getSearchCount(String condition);
	List<SendOfficialDocument> getList(int page, int pageSize);
	int getListCount();

}
