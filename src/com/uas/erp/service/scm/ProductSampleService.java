package com.uas.erp.service.scm;

public interface ProductSampleService {
	
	void saveProductSample(String formStore, String gridstore, String caller);
	
	void updateProductSampleById(String formStore,String gridstore, String caller);
	
	void deleteProductSample(int id, String caller);
	
	void auditProductSample(int id, String caller);
	
	void resAuditProductSample(int id, String caller);
	
	void submitProductSample(int id, String caller);
	
	void resSubmitProductSample(int id, String caller);
	
	void nullifyProductSample(int id, String caller);
	
}
