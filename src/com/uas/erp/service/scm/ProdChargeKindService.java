package com.uas.erp.service.scm;

public interface ProdChargeKindService {
	void saveProdChargeKind(String formStore, String caller);

	void updateProdChargeKindById(String formStore, String caller);

	void deleteProdChargeKind(int pck_id, String caller);

	void auditProdChargeKind(int pck_id, String caller);

	void resAuditProdChargeKind(int pck_id, String caller);

	void submitProdChargeKind(int pck_id, String caller);

	void resSubmitProdChargeKind(int pck_id, String caller);
}
