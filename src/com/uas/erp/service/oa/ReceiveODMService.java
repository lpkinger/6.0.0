package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.ReceiveOfficialDocument;

public interface ReceiveODMService {
	void saveROD(String formStore, String  caller);

	void deleteROD(int id, String  caller);

	void updateRODById(String formStore, String  caller);

	ReceiveOfficialDocument getRODById(int id, String  caller);

	void submitROD(int rod_id, String  caller);
	
	void deleteById(int rod_id);
	
	List<ReceiveOfficialDocument> getList(int page, int pageSize);
	
	int getListCount();
	
	List<ReceiveOfficialDocument> getByCondition(String condition, int page, int pageSize);
	
	int getSearchCount(String condition);

}
