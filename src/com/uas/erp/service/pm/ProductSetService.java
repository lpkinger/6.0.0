package com.uas.erp.service.pm;

public interface ProductSetService {
	void saveProductSet(String formStore, String gridStore, String  caller);
	void updateProductSetById(String formStore, String gridStore, String  caller);
	void deleteProductSet(int ps_id, String  caller);
	void printProductSet(int ps_id, String  caller);
	void auditProductSet(int ps_id, String  caller);
	void resAuditProductSet(int ps_id, String  caller);
	void submitProductSet(int ps_id, String  caller);
	void resSubmitProductSet(int ps_id, String  caller);
	void updateReturnqty(String data);
	void updateVendReturn(Integer id, String vendstatus, String vendremark);
	void updateCustReturn(Integer id, String custstatus, String custremark);
}
