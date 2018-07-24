package com.uas.erp.service.scm;

import java.util.Map;

public interface CustomerService {
	void updateCustomer(String formStore, String caller);
	void saveCustomer(String formStore, String caller);
	void updateUU(Integer id, String uu, String caller, String cu_businesscode, String cu_lawman, String cu_add1);
	Map<String,Object> getCustLabelCode(String condition);
}
