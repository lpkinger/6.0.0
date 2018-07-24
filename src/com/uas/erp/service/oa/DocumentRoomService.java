package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.JSONTree;

public interface DocumentRoomService {
	void saveDocumentRoom(String formStore,String   caller);
	void updateDocumentRoom(String formStore,String  caller);
	void deleteDocumentRoom(int id,String  caller);
	List<JSONTree> getJSONTree(String caller);
	void addDept(int drid, String dept, int deptid);
}
