package com.uas.erp.service.crm;

import com.uas.erp.model.Employee;

public interface MarketResearchService {
	void saveMarketResearch(String formStore, String language, Employee employee);

	void deleteMarketResearch(int mr_id, String language, Employee employee);

	void updateMarketResearchById(String formStore, String language,
			Employee employee);

	void submitMarketResearch(int mr_id, String language, Employee employee);

	void resSubmitMarketResearch(int mr_id, String language, Employee employee);

	void auditMarketResearch(int mr_id, String language, Employee employee);

	void resAuditMarketResearch(int mr_id, String language, Employee employee);
}
