package com.uas.erp.service.scm;

public interface ProductReserveService {
	void saveProductReserve(String formStore, String caller);
	void updateProductReserveById(String formStore, String caller);
	void RefreshProdMonthNew(String currentMonth, String caller);
	String turnProductWHMonthAdjust(String currentMonth, String caller);
}
