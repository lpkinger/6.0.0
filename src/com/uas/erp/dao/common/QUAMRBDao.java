package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

/**
 * @author madan
 * 
 */
public interface QUAMRBDao {
 
	JSONObject turnProdioPurc(JSONObject j);
	
	JSONObject turnProdioMake(JSONObject j);
	
	List<JSONObject> detailTurnDefectOut(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok);
	
	List<JSONObject> detailTurnDefectOut2(String caller, String piclass, List<Map<Object, Object>> maps, boolean isok);

	void deleteMRB(int id);

	String checkqtyCheck(int id);
}
