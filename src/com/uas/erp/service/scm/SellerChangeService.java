package com.uas.erp.service.scm;

public interface SellerChangeService {
	void deleteSellerChange(int sc_id, String caller);

	void saveSellerChange(String formStore, String gridStore, String caller);

	void updateSellerChange(String formStore, String gridStore, String caller);

	void auditSellerChange(int sc_id, String caller);
 
	void submitSellerChange(int sc_id, String caller);

	void resSubmitSellerChange(int sc_id, String caller); 
}
