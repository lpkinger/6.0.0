package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

public interface BOMService {
	void saveBOM(String formStore, String gridStore, String caller); 
	void updateBOMById(String formStore, String gridStore, String caller);
	void deleteBOM(int bo_id, String caller);
	void deleteDetail(int bo_id, String caller);
	void auditBOM(int bo_id, String caller);
	void resAuditBOM(int bo_id, String caller);
	void submitBOM(int bo_id, String caller);
	void resSubmitBOM(int bo_id, String caller);
	 List<Map<String, Object>>  getProductCount(String codes,String caller);
	
	/**
	 * 成本计算
	 * @param bo_id
	 * @param pr_code
	 */
	JSONObject calBOMCost(int bo_id, String pr_code,String caller);
	String zhangling(String todate,String caller);
	String[] printBomCost(int bo_id,String caller,String reportName,String condition,String prodcode);
	String[] printsingleBom(int bo_id,String caller,String reportName,String condition);
	void bomcopy(int id, String formStore, String param,String caller);
	void turnBOM(String data, String caller); 
	void bannedBOM(String data, String caller);
	void resBannedBOM(String data, String caller);
	void updateBomPast(int bo_id,String value, String caller);
	String confirmECN(String caller, String data);
	String cancelECN(String caller,String data);
	String loadRelation(String caller,  String data);
	void BOMStructPrintAll();
	JSONObject calBOMPeriodCost(int bo_id, String bv_bomversionid ,String fromdate, String todate);
	JSONObject bomCostCustom(int bo_id, String bv_bomversionid, String fromdate, String todate, String data);
	Object getProductMaster(String codes, String master);
}
