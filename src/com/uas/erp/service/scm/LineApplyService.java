package com.uas.erp.service.scm;

import com.uas.erp.model.Employee;

public interface LineApplyService {
	void saveLineApply(String formStore, String gridStore, String caller);
	void deleteLineApply(int la_id, String caller);
	void updateLineApplyById(String formStore, String gridStore, String caller);
	void submitLineApply(int la_id, String caller);
	void resSubmitLineApply(int la_id, String caller);
	void auditLineApply(int la_id, String caller);
	void resAuditLineApply(int la_id, String caller);
}
