package com.uas.erp.dao.common;
import java.util.List;

import com.uas.erp.model.DocumentList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
public interface DocumentListDao {
	List<JSONTree> getDocumentListByCondition(int parentId, String condition,Employee employee ,String language);

	List<DocumentList> getDocumentsByCondition(int parentId, String condition,
			Employee employee, String language);
}
