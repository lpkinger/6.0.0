package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.uas.erp.model.CurNavigation;
import com.uas.erp.model.JSONTree;

public interface VisitERPService {
	
	public void updateVAM(String formStore, String param);
	
	public void updateCAM(String formStore, String param);
	
	public void updateAM(String formStore, String param);

	public Map<String, Object> getNameAndPwd(String vendorId);
	
	public Map<String, Object> getMasterAndEntpName(String master);
	
	public List<CurNavigation> getActorNavigation(String type);
	
	public List<Map<String, Object>> BomSync(String pr_code,String bomid,String data,String type) throws InterruptedException;
	
	public Map<String, Object> validCustomer(String username, String password,String cu_uu, String accesskey);
	
	public Set<Map<String, Object>> getGridStore(String caller, String bomid, String cu_uu);
	
	public void SaveGridStore(String data);
	
	public boolean TurnTemplate(HttpServletRequest request, String key, String data, String cu_uu, String type);
	
	public boolean specileTruenTemplate(HttpServletRequest request, String key, String data, String cu_uu, String type, String master);
	
	public boolean validConvertTurn(int bomid, int cu_uu);
	
	public Map<String, Object> TrunFormal(int bomId, String formStore, String gridStore);
	
	public List<JSONTree> getCNTree(int parentId, String condition);
	
	public void saveCurnavigation(String cn_title, String cn_url,String type);
	
	public void updateCurnavigation(String cn_id,String cn_title, String cn_url,String type);
	
	public void deleteCurnavigation(String cn_id,String type);
	
	public boolean BomEnable(String bo_mothercode,String ve_uu,String key,String master);
	
	public void bomCustomersync();

	public List<Map<String, Object>> getOrderProcess(String code);
	
	public Map<String, Object> turnPreproduct(String data);

}
