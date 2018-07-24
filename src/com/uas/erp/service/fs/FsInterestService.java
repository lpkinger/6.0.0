package com.uas.erp.service.fs;

public interface FsInterestService {

	void updateFsInterest(String formStore, String caller);

	void deleteFsInterest(int id, String caller);

	void submitFsInterest(int id, String caller);

	void resSubmitFsInterest(int id, String caller);

	void auditFsInterest(int id, String caller);

	void resAuditFsInterest(int id, String caller);

}
