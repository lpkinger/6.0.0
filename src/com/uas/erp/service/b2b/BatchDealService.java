package com.uas.erp.service.b2b;

import java.util.Map;

public interface BatchDealService {

	/**
	 * 客户送货提醒转发货（通知）
	 * 
	 * @param caller
	 * @param data
	 * @return
	 */
	String onSaleNotifyDownSend(String caller, String data);

	String vastOpenVendorUU();

	String vastOpenCustomerUU();

	Map<String, Object> vastCheckUU();

	String vastCountVendorUU();

	String vastCountCustomerUU();

	Map<String, Object> vastCountUU();

}
