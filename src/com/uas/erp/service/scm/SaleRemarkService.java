package com.uas.erp.service.scm;

public interface SaleRemarkService {
	void saveSaleRemark(String formStore, String gridStore, String caller);
	void updateSaleRemarkById(String formStore, String gridStore, String caller);
	void deleteSaleRemark(int sr_id, String caller);
}
