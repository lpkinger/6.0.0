package com.uas.erp.service.oa;


public interface CarAllowanceService {

	void auditCarAllowance(int id, String  caller);

	void saveCarAllowance(String formStore, String  caller);

	void updateCarAllowance(String formStore, String  caller);

	void deleteCarAllowance(int id, String  caller);

	void resAuditCarAllowance(int id, String  caller);

	void submitCarAllowance(int id, String  caller);

	void resSubmitCarAllowance(int id, String  caller);

	void confirmCarAllowance(int id, String caller);

}
