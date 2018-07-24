package com.uas.erp.service.fs;

public interface FaItemsFormulaService {
	void saveFaItemsFormula(String formStore, String caller);

	void updateFaItemsFormula(String formStore, String caller);

	void deleteFaItemsFormula(int id, String caller);
}
