package com.uas.erp.service.cost;

public interface ContractCostCloseService {
	void saveContractCostClose(String formStore, String gridStore, String caller);

	void deleteContractCostClose(int id, String caller);

	void updateContractCostClose(String formStore, String param, String caller);

	void submitContractCostClose(int id, String caller);

	void resSubmitContractCostClose(int id, String caller);

	void auditContractCostClose(int id, String caller);

	void resAuditContractCostClose(int id, String caller);

	int createCostVoucher(int id, String caller);

	void cancelCostVoucher(int id, String caller);

	void catchProjectCost(String caller, String formStore);

	void cleanProjectCost(String caller, String formStore);
}
