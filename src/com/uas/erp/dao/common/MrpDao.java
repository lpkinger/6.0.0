package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

public interface MrpDao {

	String ThrowApplication(List<Map<Object, Object>> list);

	JSONObject ThrowMakeTask(int mdid);

	JSONObject ThrowToMakeTask(int mdid);

	String ThrowPurchaseChange(List<Map<Object, Object>> list);

	JSONObject ThrowMakeChange(int mdid);
	
	

}
