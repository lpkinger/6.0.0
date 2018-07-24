package com.uas.opensys.service;


public interface OrderDemandService {
	
	void saveOrderDemand(String formStore, String gridStore, String caller);

	void updateOrderDemandById(String formStore, String gridStore,
			String  caller);

	void deleteOrderDemand(int cd_id, String  caller);

	void auditOrderDemand(int cd_id, String  caller);

	void resAuditOrderDemand(int cd_id, String  caller);

	void submitOrderDemand(int cd_id, String  caller);

	void resSubmitOrderDemand(int cd_id, String  caller);

}
