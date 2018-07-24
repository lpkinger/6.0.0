package com.uas.erp.service.crm;



public interface SellerSaleReportService {

	void saveSellerSaleReport(String formStore, String param, 
			String caller);

	void deleteSellerSaleReport(int id,String caller);

	void updateSellerSaleReport(String formStore, String param,
			 String caller);

}
