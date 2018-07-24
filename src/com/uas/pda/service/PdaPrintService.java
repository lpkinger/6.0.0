package com.uas.pda.service;

import java.util.Map;

public interface PdaPrintService {

	String labelPrint(String data,int em_id);

	void setDefaultPrint(String data, int em_id);

	Map<String, Object> getDefaultPrint(int em_id);

	String zplPrint(String caller, String dpi, String data);
	
	Map<String, Object> vendorZplPrint(String caller, String data);

}
