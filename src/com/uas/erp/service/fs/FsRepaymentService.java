package com.uas.erp.service.fs;

public interface FsRepaymentService {
	void saveFsRepayment(String formStore, String caller);

	void updateFsRepayment(String formStore, String caller);

	void deleteFsRepayment(int id, String caller);

	void submitFsRepayment(int id, String caller);

	void resSubmitFsRepayment(int id, String caller);

	void auditFsRepayment(int id, String caller);

	void resAuditFsRepayment(int id, String caller);

}
