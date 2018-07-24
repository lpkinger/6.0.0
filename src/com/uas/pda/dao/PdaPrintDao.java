package com.uas.pda.dao;

import java.util.List;
import java.util.Map;

public interface PdaPrintDao {
	public String pdaPrint(List<Map<String, Object>> list, String printIp,String port,String dpi);
	
	public String printZpl(List<Map<String, Object>> list, String dpi, int width, int height);
}
