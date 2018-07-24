package com.uas.erp.service.scm;

public interface PurcRemarkService {
	void savePurcRemark(String formStore, String gridStore, String caller);
	void updatePurcRemarkById(String formStore, String gridStore, String caller);
	void deletePurcRemark(int pr_id, String caller);
	void bannedPurcRemark(int pr_id, String caller);
	void resBannedPurcRemark(int pr_id, String caller);
}
