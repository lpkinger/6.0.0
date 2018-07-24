package com.uas.erp.service.pm;

import java.util.List;
import java.util.Map;

public interface BomCheckService {

	 public  List<Map<String, Object>>  getItems(String caller);	 
	 public String bomCheck(String bomId,String bomMotherCode,String gridStore);
	 public  List<Map<String, Object>> getBomMessage(String bomId,String value);
}
