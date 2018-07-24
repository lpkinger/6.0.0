package com.uas.erp.service.drp;

public interface AllotApplicationCuService {

	void saveAllotApplicationCu(String formStore, String gridStore,
			String caller);

	void updateAllotApplicationCuById(String formStore, String gridStore,
			String caller);

	void deleteAllotApplicationCu(int aa_id, String caller);

	void auditAllotApplicationCu(int aa_id, String caller);

	void resAuditAllotApplicationCu(int aa_id, String caller);

	void submitAllotApplicationCu(int aa_id, String caller);

	void resSubmitAllotApplicationCu(int aa_id, String caller);

}
