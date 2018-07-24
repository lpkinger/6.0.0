package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.Document;

public interface DocumentDao {
  List<Document> getDocumentByCondition(String condition);
}
