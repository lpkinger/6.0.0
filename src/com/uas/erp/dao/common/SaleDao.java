package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface SaleDao {
	boolean deleteSaleforecastdetail(String sa_relativecode, int sa_id);

	String checkProdInOut(String sa_code);

	String checkQty(int sa_id);

	String turnProdInOutCustomer(String caller, List<Map<Object, Object>> maps);

	int turnSendNotify(int id);

	void udpatestatus(int sdid);

	int toAppointedSendNotify(String sn_code, int sd_id, double qty);

	JSONObject newSendNotifyWithCustomer(int custid, String custcode, String custname, String shcustCode, String shcustName,
			String currrency, Double rate, String kind, String address, String salemethod, String shipment, String apcustCode,
			String apcustName, String departcode, String departname, int cuaddressid, Object sa_id);

	JSONObject newSendNotifyWithSale(int sa_id);

	String newSendNotify();

	String getSNCodeBySourceCode(int id);

	void updateturnstatus(int sdid);

	void deleteSale(int id);

	StringBuffer turnMake(int said, Double qty);

	String getPrice(int sa_id);

	JSONObject getSalePrice(String custcode, String prodcode, String currency, String kind);

	JSONObject getSalePrice_N(String custcode, String sakind, String prodcode, String currency, String cukind, Object pricekind,
			Double sumqty, Double taxrate);

	JSONObject getSalePrice(String sa_code, int sd_detno);

	void updateSaleTotal(String sa_code);

	/**
	 * 销售单转入出货通知单之前，判断thisqty ≤ qty - yqty
	 */
	void checkAdYqty(List<Map<Object, Object>> datas);

	JSONObject getSalePuPrice(String valueOf, String valueOf2, String valueOf3, double parseDouble);

	/**
	 * 销售单转入出货单之前，判断thisqty ≤ qty - yqty
	 */
	void checkYqty(List<Map<Object, Object>> datas, String piclass);
}
