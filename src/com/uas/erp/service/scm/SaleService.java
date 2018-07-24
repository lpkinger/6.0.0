package com.uas.erp.service.scm;

import java.util.Map;

import net.sf.json.JSONObject;

public interface SaleService {
	void deleteSale(int sa_id, String caller);

	void saveSale(String formStore, String gridStore, String caller);

	int saveCustomerSimple(String formStore);

	String updateSale(String formStore, String gridStore, String caller);

	void auditSale(int sa_id, String caller);

	void resAuditSale(int sa_id, String caller);

	void submitSale(int sa_id, String caller);

	void resSubmitSale(int sa_id, String caller);

	String[] printSale(int sa_id, String reportName, String condition, String caller);

	int turnSendNotify(int sa_id, String caller);

	void getPrice(int sa_id);

	void submitTurnSale(int id);

	void resSubmitTurnSale(int id);

	void turnNormalSale(int id);

	void splitSale(String formdata, String data);

	void updatePMC(String data);

	/**
	 * 订单复制
	 * 
	 * @param id
	 * @param employee
	 * @param language
	 * @return
	 */
	JSONObject copySale(String caller, int id);

	/**
	 * 修改比例
	 * 
	 * @param caller
	 * @param id
	 * @param data
	 * @param withOth
	 */
	void updateDiscount(String caller, int id, String data, Boolean withOth);

	String getCodeString(String caller, String table, int type, String conKind);

	void updateSalePayment(String formStore);

	void saleMrpOpen(int id, String caller);

	void saleMrpClose(int id, String caller);

	void getFittingData(String caller, String pr_code, String qty, String sa_code, String detno);

	void endSale(String data, String caller);

	void calBOMCost(int sa_id, String caller);

	void confirmAgree(int sa_id, String caller);

	String turnB2CSaleOut(int sa_id, String caller);

	void UpdateLD(int sd_id, String LDCode, String Caller);
	
	String saleturnPurc(int id, String caller);
	
	Map<String, Object> chargerCalc(String data,String pickdate, int sa_deposit);
	
	void updateSaleStatus(String caller, String value,int id);
	
	void recheck(String caller,int id);
	
	void recheckAudit(int id,String caller);
	
	void resRecheck(String caller,int id);
	
	void turnPage(int id,String caller,String data);
}
