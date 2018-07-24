package com.uas.erp.service.crm;



public interface MarketTaskReportService {
	void saveMarketTaskReport(String formStore,String caller, String gridStore);
	void updateMarketTaskReport(String formStore,String caller,  String gridStore);
	void deleteMarketTaskReport(int mr_id, String caller);
	void auditMarketTaskReport(int mr_id, String caller);
	void resAuditMarketTaskReport(int mr_id, String caller);
	void submitMarketTaskReport(int mr_id, String caller);
	void resSubmitMarketTaskReport(int mr_id, String caller);
}
