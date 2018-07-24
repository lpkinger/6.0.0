package com.uas.erp.service.scm;

public interface FittingBomService {
	void saveFittingBom(String formStore, String gridStore);
	void updateFittingBomById(String formStore, String gridStore);
	void deleteFittingBom(int fb_id);
	void auditFittingBom(int fb_id);
	void resAuditFittingBom(int fb_id);
	void submitFittingBom(int fb_id);
	void resSubmitFittingBom(int fb_id);
}
