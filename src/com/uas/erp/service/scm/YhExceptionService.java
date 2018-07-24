package com.uas.erp.service.scm;

public interface YhExceptionService {
	void saveYhException(String formStore);
	void updateYhExceptionById(String formStore);
	void deleteYhException(int ye_id);
	void auditYhException(int ye_id);
	void resAuditYhException(int ye_id);
	void submitYhException(int ye_id);
	void resSubmitYhException(int ye_id);
}
