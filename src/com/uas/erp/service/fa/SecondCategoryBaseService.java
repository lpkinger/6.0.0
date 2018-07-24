package com.uas.erp.service.fa;

import com.uas.erp.model.Employee;

public interface SecondCategoryBaseService {
	void saveSecondCategoryBase(String formStore, String language,
			Employee employee);

	void updateSecondCategoryBaseById(String formStore, String language,
			Employee employee);

	void deleteSecondCategoryBase(int ca_id, String language, Employee employee);

	void auditSecondCategory(int ca_id, String language, Employee employee);

	void submitSecondCategory(int ca_id, String language, Employee employee);

	void resSubmitSecondCategory(int ca_id, String language, Employee employee);
}
