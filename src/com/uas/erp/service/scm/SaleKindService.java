package com.uas.erp.service.scm;

public interface SaleKindService {
	void saveSaleKind(String formStore, String caller);
	void updateSaleKindById(String formStore, String caller);
	void deleteSaleKind(int sk_id, String caller);
}
