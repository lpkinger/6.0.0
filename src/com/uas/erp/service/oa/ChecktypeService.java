package com.uas.erp.service.oa;

import com.uas.erp.model.Employee;

public interface ChecktypeService {
	
	void saveChecktype(String formStore, String language, Employee employee);
	
	void updateChecktypeById(String formStore, String language, Employee employee);
	
	void deleteChecktype(int ct_id, String language, Employee employee);
	
	
}
