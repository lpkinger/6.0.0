package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

public interface SubsService {
	public List<Map<String, Object>> getRealTimeSubs(int emid);
	public Map<Object,Object> mobileRealTimeCharts(int numId, int emid);
	public Map<String,Object> getSubsConditionsConfig(int numId, int emid);
	public Map<String,Object> updateSubsConditionsInstance(int numId, int emid,String data);
	public List<Object> getRelConfig(int numId);
	public List<Map<String, Object>> getComboData(String fieldName, String value, int numId);
	public Map<String, Object> getGridLinkedDate(String data, String formulaNum, String field);
}
