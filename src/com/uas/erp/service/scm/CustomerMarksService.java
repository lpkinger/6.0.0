package com.uas.erp.service.scm;

public interface CustomerMarksService {
	void saveCustomerMarks(String formStore);
	void updateCustomerMarksById(String formStore);
	void deleteCustomerMarks(int cm_id);
}
