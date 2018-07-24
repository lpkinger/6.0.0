package com.uas.erp.service.pm;

public interface ProductReviewService {
	void saveProductReview(String formStore, String gridStore, String caller);
	void updateProductReviewById(String formStore, String gridStore, String caller);
	void deleteProductReview(int pv_id, String caller);
	void auditProductReview(int pv_id, String caller);
	void resAuditProductReview(int pv_id, String caller);
	void submitProductReview(int pv_id, String caller);
	void resSubmitProductReview(int pv_id, String caller);
	void setNeedSpec(String formStore, String gridStore, String caller);
	void deleteNeedSpec(int pvd_id, String caller);
}
