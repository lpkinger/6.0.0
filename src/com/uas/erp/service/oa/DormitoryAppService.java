package com.uas.erp.service.oa;


public interface DormitoryAppService {

	void saveDormitoryApp(String formStore, String  caller);

	void deleteDormitoryApp(int da_id, String  caller);

	void updateDormitoryAppById(String formStore, String  caller);

	void submitDormitoryApp(int da_id, String  caller);

	void resSubmitDormitoryApp(int da_id, String  caller);

	void auditDormitoryApp(int da_id, String  caller);

	void resAuditDormitoryApp(int da_id, String  caller);

}
