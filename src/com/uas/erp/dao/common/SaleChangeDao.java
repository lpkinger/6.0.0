package com.uas.erp.dao.common;

import java.util.List;

public interface SaleChangeDao {
	List<String> catchSale(int id);
	void updateSaleStatus(String sa_code);
}
