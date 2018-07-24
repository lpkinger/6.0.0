package com.uas.opensys.service;


public interface PrototypeDemandService {
	
	void savePrototypeDemand(String formStore, String gridStore, String caller);

	void updatePrototypeDemandById(String formStore, String gridStore,
			String  caller);

	void deletePrototypeDemand(int cd_id, String  caller);

	void auditPrototypeDemand(int cd_id, String  caller);

	void resAuditPrototypeDemand(int cd_id, String  caller);

	void submitPrototypeDemand(int cd_id, String  caller);

	void resSubmitPrototypeDemand(int cd_id, String  caller);

}
