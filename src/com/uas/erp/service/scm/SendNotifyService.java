package com.uas.erp.service.scm;

public interface SendNotifyService {
	void saveSendNotify(String formStore, String gridStore, String caller);

	void updateSendNotifyById(String formStore, String gridStore, String caller);

	void deleteSendNotify(int sn_id, String caller);

	void auditSendNotify(int sn_id, String caller);

	void resAuditSendNotify(int sn_id, String caller);

	void submitSendNotify(int sn_id, String caller);

	void resSubmitSendNotify(int sn_id, String caller);

	int turnProdIO(int sn_id, String caller);

	String[] printSendNotify(int sn_id, String caller, String reportName, String condition);

	void loadOnHandQty(int id);

	void splitSendtify(String formdata, String data, String caller);

	void saveShip(String formStore, String caller);
}
