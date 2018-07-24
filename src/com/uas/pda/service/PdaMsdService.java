package com.uas.pda.service;

import java.util.Map;

public interface PdaMsdService {

	Map<String,Object> getLog(String code);

	void confirmInOven(String data);

	Map<String,Object> getOvenTime(String code);

	Map<String,Object>  confirmOutOven(String code);

	Map<String,Object> loadMSDLog(String code);

}
