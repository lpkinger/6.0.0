package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.ReceiveOfficialDocument;

public interface ReceiveOfficialDocumentDao {
	
	ReceiveOfficialDocument findRODById(int id);
	void delete(int rod_id);
	List<ReceiveOfficialDocument> getByCondition(String condition, int page, int pageSize);
	int getSearchCount(String condition);
//	ReceiveOfficialDocument getRODById(int id);
	List<ReceiveOfficialDocument> getList(int page, int pageSize);
	int getListCount();

}
