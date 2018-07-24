package com.uas.erp.dao.common;

public interface PurchaseChangeDao {
	String turnPurchase(int id);
	
	void updatePurchaseStatus(String pu_code);
}
