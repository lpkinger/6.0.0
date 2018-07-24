package com.uas.erp.service.plm;

import com.uas.erp.model.FormPanel;

public interface CheckService {
	FormPanel getFormItemsByCaller(String caller, String condition);

	void saveCheck(String formStore);

	void updateCheck(String formStore);

	void submitCheck(int id);

	void resubmitCheck(int id);

	void auditCheck(int id);

	void reauditCheck(int id);

	void changeBugStatus(int id);

	void changeHandler(int id, int oldemid, int newemid, String description);

	void updateFeedback(int clid);

	void deleteCheck(int id);

	void confirm(int id);
}
