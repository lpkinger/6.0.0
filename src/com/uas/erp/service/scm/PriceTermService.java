package com.uas.erp.service.scm;

public interface PriceTermService {
	void savePriceTerm(String formStore, String caller);
	void updatePriceTermById(String formStore, String caller);
	void deletePriceTerm(int prt_id, String caller);
}
