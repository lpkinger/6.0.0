package com.uas.erp.service.bench;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.Bench;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Bench.BenchScene;

public interface BenchService {
	
	Bench getBench(Employee employee, String bccode, boolean isCloud, boolean noControl, String condition);
	String appendCondition(BenchScene benchScene, String condition, Employee employee);
	boolean isJobEmployee(String caller, Employee employee);
	String appendPowerCondition(BenchScene benchScene, Employee employee, String condition,Boolean self, Boolean _jobemployee, Boolean isCount);
	String getCondition(BenchScene benchScene,Employee employee,String condition);
	Map<String,Object> getBenchSceneConfig(Employee employee, String bscode, String condition, Integer page, Integer pageSize, boolean isCloud,boolean noControl);
	Map<String,Object> getBenchSceneGridData(Employee employee, String bscode, String condition, Integer page, Integer pageSize, String orderby,  boolean isCloud,boolean noControl,boolean fromHeader);
	JSONObject getFlowchartConfig(String bccode);
	
	List<Map<String, Object>> searchBench(String benchcode, boolean isCloud, boolean noControl, String search);
	
	boolean isExist(boolean isCloud, boolean noControl, Employee employee, String bench, String business, String scene);
}
