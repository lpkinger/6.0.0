package com.uas.erp.service.fs;

public interface MoneyDemandPlanService {
	void saveMoneyDemandPlan(String formStore, String param, String caller);

	void updateMoneyDemandPlan(String formStore, String param, String caller);

	void deleteMoneyDemandPlan(int id, String caller);

	void submitMoneyDemandPlan(int id, String caller);

	void resSubmitMoneyDemandPlan(int id, String caller);

	void auditMoneyDemandPlan(int id, String caller);

	void resAuditMoneyDemandPlan(int id, String caller);
}
