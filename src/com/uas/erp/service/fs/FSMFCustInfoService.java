package com.uas.erp.service.fs;

public interface FSMFCustInfoService {
	void saveFSMFCustInfo(String formStore, String gridStore, String caller);

	void updateFSMFCustInfo(String formStore, String gridStore, String caller);

	void deleteFSMFCustInfo(int id, String caller);

	void submitFSMFCustInfo(int id, String caller);

	void resSubmitFSMFCustInfo(int id, String caller);

	void auditFSMFCustInfo(int id, String caller);

	void resAuditFSMFCustInfo(int id, String caller);

	void saveFSMFCustInfoDet(String gridStore);

}
