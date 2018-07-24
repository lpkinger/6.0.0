package com.uas.erp.service.plm;

public interface ProjectReviewService {
	void loadKeyDevice(String producttype, int prid);

	void updateProjectReview(String formStore, String param1, String param2);

	void submitProjectReview(int id);

	void resSubmitProjectReview(int id);

	void auditProjectReview(int id);

	void resAuditProjectReview(int id);

	void planMainTask(int id);

	void reviewupdate(String reviewitem, String reviewresult, int id);

	void deleteProjectReview(int id);
}
