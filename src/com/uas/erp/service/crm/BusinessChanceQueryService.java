package com.uas.erp.service.crm;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

public interface BusinessChanceQueryService {
 Map<String,Object> getBusinessChanceQueryConfigs(String condition, Integer start, Integer end);
 String getProcessDataByCondition(String condition);
 List<JSONObject> getHopperByCondition(String condition);
 List<Map<String,Object>> getChanceDatasById(int id);
}
