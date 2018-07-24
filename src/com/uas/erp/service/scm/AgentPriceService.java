package com.uas.erp.service.scm;

public interface AgentPriceService {
	void saveAgentPrice(String formStore, String gridStore, String caller);
	void updateAgentPriceById(String formStore, String gridStore, String caller);
	void deleteAgentPrice(int ap_id, String caller);
	void printAgentPrice(int ap_id, String caller);
	void auditAgentPrice(int ap_id, String caller);
	void resAuditAgentPrice(int ap_id,String caller );
	void submitAgentPrice(int ap_id, String caller);
	void resSubmitAgentPrice(int ap_id, String caller);
}
