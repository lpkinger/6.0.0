package com.uas.erp.service.pm;

public interface MakeOrderService {
	void saveMakeOrder(String formStore, String caller);
	void updateMakeOrderById(String formStore, String caller);
	void deleteMakeOrder(int ma_id, String caller);
	void auditMakeOrder(int ma_id, String caller);
	void resAuditMakeOrder(int ma_id, String caller);
	void submitMakeOrder(int ma_id, String caller);
	void resSubmitMakeOrder(int ma_id, String caller);
}
