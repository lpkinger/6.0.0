package com.uas.erp.service.fs;

public interface ContractApplyService {
	void saveContractApply(String formStore, String caller);

	void updateContractApply(String formStore, String caller);

	void deleteContractApply(int id, String caller);

	void submitContractApply(int id, String caller);

	void resSubmitContractApply(int id, String caller);

	void auditContractApply(int id, String caller);

	void resAuditContractApply(int id, String caller);

}
