package com.uas.erp.service.cost;

import com.uas.erp.model.Employee;

public interface ProdIOCateSetService {
	void saveProdIOCateSet(String formStore, String language, Employee employee);
	void updateProdIOCateSetById(String formStore, String language, Employee employee);
	void deleteProdIOCateSet(int pc_id, String language, Employee employee);
	void auditProdIOCateSet(int pc_id, String language, Employee employee);
	void resAuditProdIOCateSet(int pc_id, String language, Employee employee);
	void submitProdIOCateSet(int pc_id, String language, Employee employee);
	void resSubmitProdIOCateSet(int pc_id, String language, Employee employee);
	void bannedProdIOCateSet(int pc_id, String language, Employee employee);
	void resBannedProdIOCateSet(int pc_id, String language, Employee employee);
}
