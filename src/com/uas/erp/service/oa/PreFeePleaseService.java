package com.uas.erp.service.oa;


public interface PreFeePleaseService {

	void savePreFeePlease(String formStore, String param);

	void deletePreFeePlease(int id);

	void updatePreFeePleaseById(String formStore, String param);

	void submitPreFeePlease(int id);

	void resSubmitPreFeePlease(int id);

	void auditPreFeePlease(int id);

	void resAuditPreFeePlease(int id);

	int turnFYBX(int id, String language);

}
