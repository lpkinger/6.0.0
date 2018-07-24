package com.uas.erp.service.scm;

public interface NewProductConService {
	void saveNewProductCon(String formStore, String caller);
	void updateNewProductCon(String formStore, String caller);
	void deleteNewProductCon(int id, String caller);
    void submitNewProductCon(int id, String caller);
    void resSubmitNewProductCon(int id,String caller);
    void auditNewProductCon(int id,String caller);
    void resAuditNewProductCon(int id,String caller);
}
