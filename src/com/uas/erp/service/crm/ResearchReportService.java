package com.uas.erp.service.crm;

import com.uas.erp.model.Employee;

public interface ResearchReportService {
	void saveResearchReport(String formStore, String gridStore, String language, Employee employee,String caller);
	void deleteResearchReport(int mr_id, String language, Employee employee,String caller);
	void updateResearchReportById(String formStore,String gridStore, String language, Employee employee,String caller);
	void submitResearchReport(int mr_id, String language, Employee employee,String caller);
	void resSubmitResearchReport(int mr_id, String language, Employee employee,String caller);
	void auditResearchReport(int mr_id, String language, Employee employee,String caller);
	void resAuditResearchReport(int mr_id, String language, Employee employee,String caller);
	String turnFeepleaseCLFBX(int mr_id, String language, Employee employee,String caller);
}
