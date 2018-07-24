package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ARBillService {
	void saveARBill(String caller, String formStore, String gridStore, String param2, String param3);

	void updateARBillById(String caller, String formStore, String gridStore, String param2, String param3);

	void deleteARBill(int pu_id, String caller);

	String[] printARBill(String caller, int pu_id, String reportName, String condition);

	void auditARBill(int pu_id, String caller);

	void resAuditARBill(int pu_id, String caller);

	void submitARBill(int pu_id, String caller);

	void resSubmitARBill(int pu_id, String caller);

	void postARBill(int pu_id, String caller);

	void resPostARBill(int pu_id, String caller);

	String vastPostARBill(String caller, String data);

	void createVoucherARO(String abcode, String abdate, String caller);

	String confirmXJSK(int ab_id, String catecode, String caller);

	String confirmYJSK(int ab_id, String caller);

	String cancelXJSK(int ab_id, String caller);

	String[] printVoucherCodeARBill(String caller, int ab_id, String reportName, String condition);

	void updateTaxcode(String caller, int ab_id, String ab_refno, String ab_remark);

	JSONObject copyARBill(int id, String caller);

	String getOrderType(String caller, int id, String code);

	List<Map<String, Object>> findAss(int ab_id, String type);

	String vastCheckARBill(String caller, String data);

	String vastResCheckARBill(String caller, String data);
}
