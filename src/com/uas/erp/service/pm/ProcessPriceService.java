package com.uas.erp.service.pm;

public interface ProcessPriceService {
	void saveProcessPrice(String formStore, String gridStore, String caller);

	void updateProcessPriceById(String formStore, String gridStore, String caller);

	void deleteProcessPrice(int pp_id, String caller);

	void printProcessPrice(int pp_id, String caller);

	void auditProcessPrice(int pp_id, String caller);

	void resAuditProcessPrice(int pp_id, String caller);

	void submitProcessPrice(int pp_id, String caller);

	void resSubmitProcessPrice(int pp_id, String caller);
}
