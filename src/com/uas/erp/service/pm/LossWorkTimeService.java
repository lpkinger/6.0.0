package com.uas.erp.service.pm;

public interface LossWorkTimeService {
    void saveLossWorkTime(String formStore, String param, String caller);
    void updateLossWorkTimeById(String formStore, String param, String caller);
    void deleteLossWorkTime(int id, String caller);
    void auditLossWorkTime(int lw_id, String caller);
	void resAuditLossWorkTime(int lw_id, String caller);
	void submitLossWorkTime(int lw_id, String caller);
	void resSubmitLossWorkTime(int lw_id, String caller);
	int copyLossWorkTime(int id, String caller);
}
