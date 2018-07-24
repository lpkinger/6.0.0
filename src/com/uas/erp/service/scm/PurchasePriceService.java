package com.uas.erp.service.scm;

import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Workbook;

public interface PurchasePriceService {
	void savePurchasePrice(String formStore, String gridStore, String caller);

	void updatePurchasePriceById(String formStore, String gridStore, String caller);

	void deletePurchasePrice(int pp_id, String caller);

	void printPurchasePrice(int pp_id, String caller);

	void auditPurchasePrice(int pp_id, String caller);

	void resAuditPurchasePrice(int pp_id, String caller);

	void submitPurchasePrice(int pp_id, String caller);

	void resSubmitPurchasePrice(int pp_id, String caller);

	void bannedPurchasePrice(int pp_id, String caller);

	void resBannedPurchasePrice(int pp_id, String caller);

	void abatepurchasepricestatus(int ppd_id, String caller);

	void resabatepurchasepricestatus(int ppd_id, String caller);

	boolean ImportExcel(int id, Workbook wbs, String substring, String caller);

	/**
	 * 核价单复制
	 * 
	 * @param id
	 * @param employee
	 * @param language
	 * @return
	 */
	JSONObject copyPurchasePrice(int id, String caller);

	void appstatuspurchaseprice(int id, String caller);

	void resappstatuspurchaseprice(int id, String caller);
}
