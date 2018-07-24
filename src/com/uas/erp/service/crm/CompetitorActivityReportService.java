package com.uas.erp.service.crm;



public interface CompetitorActivityReportService {
	void saveCompetitorActivityReport(String formStore,String caller);
	void deleteCompetitorActivityReport(int car_id,String caller);
	void updateCompetitorActivityReport(String formStore,String caller);}
