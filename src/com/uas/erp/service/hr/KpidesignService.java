package com.uas.erp.service.hr;

public interface KpidesignService {
	void saveKpidesign(String formStore, String gridStore,String caller);
	void updateKpidesign(String formStore, String gridStore, String caller);
	void deleteKpidesign(int kd_id, String caller);
	void submitKpidesign(int kd_id, String  caller);
	void resSubmitKpidesign(int kd_id, String  caller);
	void auditKpidesign(int kd_id, String  caller);
	void resAuditKpidesign(int kd_id, String  caller);
	int saveDetail(String caller, String formStore, String gridStore);
	void updateDetail(String caller, String formStore, String gridStore);
	void deleteDetail(String caller, int id);
	String kpidesignLaunch(String caller, String data,String time_from,String time_to,String period);
	String kpidSummary(String caller, String data);
}
