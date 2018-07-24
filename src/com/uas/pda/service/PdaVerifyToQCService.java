package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaVerifyToQCService {

	Map<String, Object> getDataByBar (String bar_code);
	
    List<Map<String, Object>> getHaveList (String caller,String code,Integer page,Integer pageSize);
	
}
