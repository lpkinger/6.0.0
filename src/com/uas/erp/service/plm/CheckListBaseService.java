package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

public interface CheckListBaseService {
	void loadTestItem(int id, String type, String sourceclass);

	void saveCheckListBase(String formStore, String param);

	void deleteCheckListBase(int id);

	void updateCheckListBase(String formStore, String param);

	void submitCheckListBase(int id);

	void reSubmitCheckListBase(int id);

	void auditCheckListBase(int id);

	void resAuditCheckListBase(int id);

	void setItemResult(String result, String data);

	void EndProject(int id);

	void resEndProject(int id);

	void deleteAllDetails(int id);
	 void updateResult(String data, String field, String keyValue);
	 void batchUpdateResult(String formdata, String data);
	 
	 void updateResultCheckListBase(String data,int id);

	 List<Map<Object, Object>> getCheckListGridData(Integer id);
	 
	 
}
