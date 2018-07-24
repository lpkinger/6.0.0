package com.uas.erp.dao.common;
import java.util.List;
import com.uas.erp.model.JSONTree;
public interface RecordDao {
	List<JSONTree> getJSONResource(int id);
	List<JSONTree> getJSONRecord(String condition);
}
