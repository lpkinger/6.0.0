package com.uas.erp.service.scm;

import java.util.List;
import com.uas.erp.model.JSONTree;

public interface CustomerKindService {
	void saveCustomerKind(String formStore);
	void updateCustomerKindById(String formStore);
	void deleteCustomerKind(int ck_id);
	List<JSONTree> getJsonTrees(int parentid);
	String getCustomerKindNum(int id);
	void end(int id);
	void resEnd(int id);
}
