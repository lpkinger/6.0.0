package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.b2b.model.PurchaseReply;
import com.uas.erp.model.Employee;

public interface PurchaseService {
	void savePurchase(String formStore, String gridStore, String caller);

	void updatePurchaseById(String formStore, String gridStore, String caller);

	void deletePurchase(int pu_id, String caller);

	void auditPurchase(int pu_id, String caller);

	void resAuditPurchase(int pu_id, String caller);

	void submitPurchase(int pu_id, String caller);

	void resSubmitPurchase(int pu_id, String caller);

	String[] printPurchase(int pu_id, String caller, String reportName, String condition);

	void getPrice(int pu_id);

	void getStandardPrice(int pu_id);

	void vastDeletePurc(int[] id, String caller);

	JSONObject copyPurchase(int id, String caller);

	void getMakeVendorPrice(int ma_id, String caller);

	void getLastMakePrice(int ma_id, String caller ,String prodcode);

	void getVendorPrice(int ma_id, String vendcode, String curr, String caller);

	void b2bPurchase(int pu_id, String caller);
	

	/**
	 * 同步到香港万利达
	 * 
	 * @param caller
	 * @param data
	 * @param language
	 * @param employee
	 */
	void syncPurc(String caller, String data);

	/**
	 * 刷新同步状态
	 * 
	 * @param caller
	 * @param id
	 */
	void resetSyncStatus(String caller, Integer id);

	void updateVendorBackInfo(String data, String caller);
	
	void updateGridDetailReplyDate(String id, String date);

	void refreshqty(Integer id, String caller);

	void purchasedataupdate(int id, String caller);

	void splitPurchase(String formdata, String data, String caller);

	/**
	 * 查找回复记录
	 * 
	 * @param id
	 * @return
	 */
	List<PurchaseReply> findReplyByPuid(int id);

	void vastClosePurchaseDetail(String language, Employee employee,
			String caller, String data);
	
	void endPurchase(String data, String caller);
	
	void turnBankRegister(int id);
	
	String confirmTurnBankRegister(String formStore,String gridStore);
	
	void turnToPayPlease(int pu_id);

	void dataReply(String pucode, String detno, String qty, String data);
	
	Map<String, Object> getContractProcess(int id);
}
