package com.uas.erp.service.pm;

public interface PurMouldService {
	void savePurcMould(String formStore, String gridStore, String caller);

	void updatePurcMouldById(String formStore, String gridStore, String caller);

	void deletePurcMould(int pm_id, String caller);

	void printPurcMould(int pm_id, String caller);

	void auditPurcMould(int pm_id, String caller);

	void resAuditPurcMould(int pm_id, String caller);

	void submitPurcMould(int pm_id, String caller);

	void resSubmitPurcMould(int pm_id, String caller);

	int turnYSReport(int pm_id, String caller);

	void updatepaystatus(int pm_id, String paystatus, String payremark);

	int turnFeePlease(int pm_id, String caller);

	void savePurcMould(String formStore, String gridStore, String gridStore2, String caller);

	void updatePurcMouldById(String formStore, String gridStore, String gridStore2, String caller);

}
