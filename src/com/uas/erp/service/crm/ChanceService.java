package com.uas.erp.service.crm;

import java.util.Map;



public interface ChanceService {

	void saveChance(String formStore,String caller);
	void deleteChance(int ch_id,String caller);
	void updateChance(String formStore,String caller);
	void turnStatus(String gridStore,String caller);
	void turnEnd(String gridStore,String caller);
	Map<String, Object> getFunnelData(String condition,String caller);
	void submitChance(int ch_id,String caller);
	void resSubmitChance(int ch_id,String caller);
	void auditChance(int ch_id,String caller);
	void resAuditChance(int ch_id,String caller);
	String haveAllChancestatus(int ch_id,String caller);
	String haveAllstatus(String gridStore,String caller);
}
