package com.uas.erp.service.scm;

public interface InventoryService {
	String inventory(String method, String whcode);
	String inventoryByCondition(String method, String whcode,String condition);
}
