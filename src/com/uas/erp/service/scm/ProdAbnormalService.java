package com.uas.erp.service.scm;

public interface ProdAbnormalService {

	void saveProdAbnormal(String formStore, String caller);

	void updateProdAbnormalById(String formStore, String caller);

	void deleteProdAbnormal(int pa_id, String caller);

	void auditProdAbnormal(int pa_id, String caller);

	void resAuditProdAbnormal(int pa_id, String caller);

	void submitProdAbnormal(int pa_id, String caller);

	void resSubmitProdAbnormal(int pa_id, String caller);

	void checkProdAbnormal(int pa_id, String caller);

	void resCheckProdAbnormal(int pa_id, String caller);
}
