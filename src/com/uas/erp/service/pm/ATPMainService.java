package com.uas.erp.service.pm;

public interface ATPMainService {
	void saveATPMain(String formStore, String gridStore,String caller);
	void updateATPMainById(String formStore, String gridStore,String caller);
	void deleteATPMain(int bi_id,String caller);
	void printATPMain(int bi_id,String caller);
	void auditATPMain(int bi_id,String caller);
	void resAuditATPMain(int bi_id,String caller);
	void submitATPMain(int bi_id,String caller);
	void resSubmitATPMain(int bi_id,String caller);
	void executeOperation(int bi_id,String caller);
	int runATPFromOther(String fromcode,String fromwhere,String caller);
	void loadSale(String caller, String data, int am_id);
	void loadAllSale(String caller, int am_id, String condition);
}
