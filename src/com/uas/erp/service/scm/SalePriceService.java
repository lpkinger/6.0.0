package com.uas.erp.service.scm;

public interface SalePriceService {
	void saveSalePrice(String formStore, String gridStore, String caller);

	void updateSalePriceById(String formStore, String gridStore, String caller);

	void deleteSalePrice(int sp_id, String caller);

	void printSalePrice(int sp_id, String caller);

	void auditSalePrice(int sp_id, String caller);

	void resAuditSalePrice(int sp_id, String caller);

	void submitSalePrice(int sp_id, String caller);

	void resSubmitSalePrice(int sp_id, String caller);

	void abatesalepricestatus(int id);

	void resabatesalepricestatus(int id);

	void auditSalePriceAfter(String caller, Object sp_id);
}
