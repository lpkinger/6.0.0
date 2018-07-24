package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.EmployeeMail;
import com.uas.erp.model.JSONTree;

public interface EmployeeMailService {
	void saveAddrBook(String formStore);
	void updateAddrBookById(String formStore);
	void deleteAddrBook(int emm_id);

	List<JSONTree> getJsonTrees(String master, int parentid);
	List<JSONTree> getJSONMail();
	EmployeeMail getEmployeeMailByEmployee(int id);
}
