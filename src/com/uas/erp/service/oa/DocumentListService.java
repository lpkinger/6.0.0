package com.uas.erp.service.oa;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.uas.erp.model.DocumentList;
import com.uas.erp.model.JSONTree;
public interface DocumentListService {
	List<JSONTree> getDirectoryByCondition(int parentId, String condition, String  caller);
	boolean save(String caller,String formStore);
	void update(String formStore, String  caller);
	List<JSONTree> loadDir(int parentId,String condition, String  caller);
	List<DocumentList> getDocumentsByParentId(int parentId, String condition,
			String  caller);
	void DocUpdateByType(String caller,String formData, String type);
	void saveChange(String formStore, String caller);
	void delete(int id, String caller);
	void review(String formStore, String caller);
	void deleteDoc(String data, String  caller);
	void moveDoc(String data, int folderId);
	void relateDoc(String data, String relateCode);
	void extendParentPower(Object parentId,Object dirId);
	Set<DocumentList> getFilesBySearch(String condition);
	void downloadbyIds(HttpServletResponse response,String ids,String zipFileName) throws IOException, KeyManagementException, NoSuchAlgorithmException;
	String download(HttpServletResponse response,String path,String escape,String fileName) throws IOException;
	List<Map<String, Object>> getDocLog(int docId);
}
