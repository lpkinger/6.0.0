package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaQCToPurcCheckinService {

	Map<String, Object> getDataByBar (String bar_code);
	
	Map<String, Object> turnPurcStorage (Integer ve_id,String okwh,String ngwh);
	
    List<Map<String, Object>> getHaveList (String caller,String code,Integer page,Integer pageSize);
	
}
