package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.Bench.BenchBusiness;
import com.uas.erp.model.Bench.BenchButton;
import com.uas.erp.model.Bench.BenchScene;
import com.uas.erp.model.Bench.BenchSceneGrid;
import com.uas.erp.model.Bench.SceneButton;
import com.uas.erp.model.Bench;
import com.uas.erp.model.Employee;


public interface BenchDao {
	Bench getBench(String bccode, String sob);
	
	List<BenchBusiness> getBenchBusinesses(String bccode, Employee employee);
	List<BenchBusiness> getHideBusinesses(String bccode, Employee employee);
	List<BenchBusiness> getBenchBusinessesByPower(String bccode, Employee employee);
	List<BenchBusiness> getHideBusinessesByPower(String bccode, Employee employee);
	List<BenchBusiness> getSelfBenchBusinesses(String bccode, Employee employee);
	List<BenchBusiness> getSelfBenchBusinessesByPower(String bccode, Employee employee);
	
	List<BenchScene> getBenchScenes(String bccode, String sob);
	List<BenchScene> getBenchScenes(String bccode, String bbcode, String sob);
	List<BenchScene> getBenchScenesByPower(String bccode, String bbcode, Employee employee);
	List<BenchScene> getSelfBenchScenes(String bccode, String bbcode, Employee employee);
	List<BenchScene> getSelfBenchScenesByPower(String bccode, String bbcode, Employee employee);
	
	List<BenchButton> getBenchButtons(String bccode);
	Map<String, List<BenchButton>> getBenchButtons(String bccode, String sob);
	Map<String, List<BenchButton>> getBenchButtonsByPower(String bccode, Employee employee);
	BenchScene getBenchScene(String bscode, String sob);
	
	boolean isSelfShow(String bccode, String bbcode, Employee employee);
	boolean isShow(String bccode, String bbcode, Employee employee);
	
	List<SceneButton> getSceneButtons(String bscode, String sob);
	List<SceneButton> getSceneButtonsByPower(String bscode, Employee employee);
	
	String getRelativesettings(String bscode, String kind, int emid);
	List<Map<String,Object>> getSceneGridData(BenchScene benchScene,String condition, Employee employee, Integer page, Integer pageSize, String orderby, Boolean jobemployee);
	List<Map<String, Object>> getSummaryData(BenchScene benchScene, String condition, Boolean jobemployee);
	
	List<Bench> getAllBenchs(String sob);
	List<Bench> getBenchList(String condition,Employee employee);
	
	Map<String, Object> copy(Integer id, String sob,  Object bccode, Object bbcode, Object bscode, String newtitle);
	
	List<BenchSceneGrid> getSetByCaller(String caller);
	
	List<JSONObject> getFlowchartConfig(String bccode);
	
	void deleteScene(String caller, Employee employee, Object bccode, Object bbcode, String scenecode);
	
	void clearScene(Employee employee, String bccode, String bbcode);
	
	String getSqlWithJobEmployee(Employee employee);
	
	String getCode(Object benchcode, String name);
	
	List<Map<String, Object>> searchBenchScene(String benchcode, Employee employee, String search, boolean noControl);
	
	List<Map<String, Object>> searchBenchButton(String benchcode, Employee employee, String search, boolean noControl);
	
	boolean isExist(boolean noControl, Employee employee, String bench, String business, String scene);
	
}
