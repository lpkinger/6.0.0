package com.uas.erp.service.drp;



public interface TerminalService {
	
	void saveTerminal(String formStore,  String caller);
	
	void updateTerminalById(String formStore,  String caller);
	
	void deleteTerminal(int pp_id,  String caller);
	
	void auditTerminal(int pp_id,  String caller);
	
	void resAuditTerminal(int pp_id,  String caller);
	
	void submitTerminal(int pp_id,  String caller);
	
	void resSubmitTerminal(int pp_id,  String caller);
	
}
