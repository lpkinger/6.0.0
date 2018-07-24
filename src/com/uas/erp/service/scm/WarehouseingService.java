package com.uas.erp.service.scm;

import java.util.List;

public interface WarehouseingService {
	String createWarehouseing(String whi_clientcode,String whi_clientname,int whi_amount, String whi_freefix,String caller);

	List<?> getWarehouseingLog(String whi_code);

	void updateWarehouseing(String whi_code, String whi_status, String whi_text);
}
