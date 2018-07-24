package com.uas.erp.service.pm;

public interface MouldDeliveryOrderService {
	void saveMouldDeliveryOrder(String formStore, String gridStore, String caller);
	void updateMouldDeliveryOrderById(String formStore, String gridStore, String caller);
	void deleteMouldDeliveryOrder(int md_id, String caller);
	void printMouldDeliveryOrder(int md_id, String caller);
	void auditMouldDeliveryOrder(int md_id, String caller);
	void resAuditMouldDeliveryOrder(int md_id, String caller);
	void submitMouldDeliveryOrder(int md_id, String caller);
	void resSubmitMouldDeliveryOrder(int md_id, String caller);
	void postMouldDeliveryOrder(int md_id, String caller);
	void resPostMouldDeliveryOrder(int md_id, String caller);
}
