package com.uas.erp.service.scm;

public interface SaleForecastKindService {
	void saveSaleForecastKind(String formStore, String caller);
	void updateSaleForecastKindById(String formStore, String caller);
	void deleteSaleForecastKind(int sf_id, String caller);
}
