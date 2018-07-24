package com.uas.erp.service.pm;

import com.uas.erp.model.FileUpload;

public interface ECNService {
	void saveECN(String formStore, String gridStore, String caller);
	void updateECNById(String formStore, String gridStore, String caller);
	void deleteECN(int ecn_id, String caller);
	void auditECN(int ecn_id, String caller);
	void resAuditECN(int ecn_id, String caller);
	void submitECN(int ecn_id, String caller);
	void resSubmitECN(int ecn_id, String caller);
	String[] printECN(int ecn_id, String caller,String reportName,String condition);
	void closeECNDetail(int ed_id, String caller);
	void openECNDetail(int ed_id, String caller);
	void executeAutoECN();
	void closeECNAllDetail(int id, String caller);
	void openECNAllDetail(int id, String caller);
	void turnAutoECN(int id, String caller);
	void autoNewProdECN(int id, String caller);
	public String importECN(String caller, FileUpload uploadItem);
	String turnApplication(int id, String caller);
}
