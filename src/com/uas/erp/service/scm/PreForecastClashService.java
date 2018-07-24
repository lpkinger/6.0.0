package com.uas.erp.service.scm;

public interface PreForecastClashService {
	void savePreForecastClash(String caller, String formStore, String gridStore);

	void updatePreForecastClashById(String caller, String formStore, String gridStore);

	void deletePreForecastClash(String caller, int pfc_id);

	void auditPreForecastClash(int pfc_id,String caller);

	void resAuditPreForecastClash(String caller, int pfc_id);

	void submitPreForecastClash(String caller, int pfc_id);

	void resSubmitPreForecastClash(String caller, int pfc_id);
}
