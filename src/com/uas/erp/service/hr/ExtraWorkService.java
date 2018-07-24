package com.uas.erp.service.hr;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.uas.erp.model.Employee;

public interface ExtraWorkService {
	void saveExtraWork(HttpSession session,String formStore,String caller);

	void deleteExtraWork(int id, String caller);

	void updateExtraWork(String formStore, String caller);

	void submitExtraWork(int id, String caller);

	void resSubmitExtraWork(int id, String caller);

	void auditExtraWork(int id, String caller);

	void resAuditExtraWork(int id, String caller);

	void checkTime(Map<Object, Object> formStore);

	Map<String,Object> ExtraWorkSaveAndSubmit(HttpSession session, String caller,
			String formStore, Employee employee);

	void ExtraWorkUpdateAndSubmit(String caller, String formStore);

}
