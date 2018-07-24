package com.uas.erp.service.scm;

public interface ProductApprovalService {
	
	void saveProductApproval(String formStore,String param1,String param2,String param3, String caller);
	
	void saveproductApprovalDetail(String formStore,String gridStore,String caller);
	
	void saveprodApprovalDetail(String formStore,String gridStore,String caller);
	
	void saveprodAppDetail(String formStore,String gridStore,String caller);
	
	void updateProductApprovalById(String formStore, String caller);
	
	void deleteProductApproval(int pa_id, String caller);
	
	void auditProductApproval(int pa_id, String caller);
	
	void resAuditProductApproval(int pa_id, String caller);
	
	void submitProductApproval(int pa_id, String caller);
	
	void resSubmitProductApproval(int pa_id, String caller);

	void saveApprovalResult(String formStore, String caller);

	void saveProductApproval(String formStore, String caller);
}
