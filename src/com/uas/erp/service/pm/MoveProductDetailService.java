package com.uas.erp.service.pm;

public interface MoveProductDetailService {
	String saveMoveProductDetail(String formStore, String gridStore, String caller);
	String updateMoveProductDetailById(String formStore, String gridStore, String caller);
	void deleteMoveProductDetail(int mp_id, String caller);
	void auditMoveProductDetail(int mp_id, String caller);
	void resAuditMoveProductDetail(int mp_id, String caller);
	void submitMoveProductDetail(int mp_id, String caller);
	void resSubmitMoveProductDetail(int mp_id, String caller);
	int moveProduct(String formStore, String caller);
}
