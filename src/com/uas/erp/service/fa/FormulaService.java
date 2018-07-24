package com.uas.erp.service.fa;



public interface FormulaService {
	void saveFormula(String formStore, String caller);
	void updateFormulaById(String formStore, String caller);
	void deleteFormula(int fo_id, String caller);
	void auditFormula(int fo_id, String caller);
	void resAuditFormula(int fo_id, String caller);
	void submitFormula(int fo_id, String caller);
	void resSubmitFormula(int fo_id, String caller);
}
