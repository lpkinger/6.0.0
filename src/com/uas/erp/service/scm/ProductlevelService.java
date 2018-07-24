package com.uas.erp.service.scm;

public interface ProductlevelService {
	
	void saveProductlevel(String formStore, String gridstore,String caller);
	
	void updateProductlevelById(String formStore, String gridstore, String caller);
	
	void deleteProductlevel(int pl_id, String caller);
	
	void auditProductlevel(int pl_id, String caller);
	
	void resAuditProductlevel(int pl_id, String caller);
	
	void submitProductlevel(int pl_id, String caller);
	
	void resSubmitProductlevel(int pl_id, String caller);
	
	void updatePurchasetypedetail(int id,String griddata,String caller);
}
