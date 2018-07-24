package com.uas.erp.service.fs;

public interface FsLoadedInvestService {

	void saveInvestReport(String formStore, String caller);

	void updateInvestReport(String formStore, String caller);

	void deleteInvestReport(int id, String caller);

	void submitInvestReport(int id, String caller);

	void resSubmitInvestReport(int id, String caller);

	void auditInvestReport(int id, String caller);

	void resAuditInvestReport(int id, String caller);

	void getDefault(int id);

	void updateTransactionCheck(String formStore, String gridStore, String caller);

	void updateGuaranteeCheck(String formStore, String gridStore, String caller);
	
	void saveSettleAccountCheck(String formStore, String param1, String param2, String caller);

}
