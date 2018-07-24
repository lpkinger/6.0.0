package com.uas.erp.service.crm;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface BusinessChanceService {

	void saveBusinessChance(String formStore, String caller);

	void deleteBusinessChance(int bc_id, String caller);

	void updateBusinessChance(String formStore, String caller);

	void submitBusinessChance(int bc_id, String caller);

	void resSubmitBusinessChance(int bc_id, String caller);

	void auditBusinessChance(int bc_id, String caller);

	void resAuditBusinessChance(int bc_id, String caller);

	String SendSample(int bc_id, String caller);

	String Quote(int bc_id, String caller);

	String PlaceOrder(int bc_id, String caller);

	String Shipment(int bc_id, String caller);

	void endBusinessChance(int bc_id, String caller);

	void resEndBusinessChance(int bc_id, String caller);

	void callBack(String ids,String caller,String bcdids);

	void busDistribute(String ids, String em_code, String em_name,String caller,String bcdids);

	void transfer(String ids, String bd_name, String caller,String bcdids);

	void deleteBusinessDataBase(int id, String caller);

	Map<String,Object> getPoint(String parameters);
	
	Map<String,Object> getPointAndData(String parameters);
	
	void isBusinesslimit(String bc_doman);
	 
	Map<String,Object> turnCustomer(int cu_id);

	Map<String, Double> getLngAndLat(String address);

	String loadJSON(String url);

	void businessChanceLock(String ids, String caller);

	void businessChanceRestart(String ids, String caller);
	
	Map<String, Object> getBBClist(String condition,int page,int pageSize);
	
	String TrunQuotationDown(String formstore);
	
	String chooseBusinessChance(String stores,String type);
	
	void DescriptionLimit(Map<Object, Object> store);
	
}
