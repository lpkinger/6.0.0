package com.uas.erp.service.bench;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Bench;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Bench.BenchButton;
import com.uas.erp.model.Bench.BenchSceneGrid;
import com.uas.erp.model.JSONTree;

public interface MABenchService {
	
	List<JSONTree> getBenchTree(Boolean isRoot);
	List<JSONTree> searchBenchTree(String search);
	List<Map<String,Object>> getActionsData(String caller, String alias);
	
	List<Bench> getBenchList(String condition);
	Map<String, Object> getBenchScenes(String bccode);
	
	void saveBench(String bench);
	void deleteBench(String benchcode);
	
	List<BenchButton> getBenchButtons(String benchcode);
	void saveBenchButtons(String benchButtons, String sob, String bccode);
	void deleteBenchButtons(String ids, String sob, String bccode);
	
	Map<String,Object> saveScene(String caller, Map<Object, Object> form, String param, String param1, String sob);
	Map<String,Object> updateScene(String caller,  Employee employee, Map<Object, Object> form, String param, String param1, String param2, String param3);
	void deleteScene(String caller, String scenecode);
	
	String resetCombo(String caller, String field, String sob);
	
	Map<String, Object> copy(Integer id, String newtitle);
	
	Map<String, Object> getSelfBusiness(String benchcode, boolean noControl);
	void saveSelfBusiness(String benchcode,String datas);
	
	Map<String, Object> getSelfScene(String benchcode, boolean noControl);
	void saveSelfScene(String benchcode, String datas);
	
	void selfReset(String benchcode, boolean isBusiness);
	
	List<BenchSceneGrid> getSetByCaller(String caller);
	
	void saveBusiness(String caller, String formStore);
	
	void updateBusiness(String caller, String formStore);
	
	void deleteBusiness(String caller, Integer id);
	
	void bannedBusiness(String caller, Integer id);
	
	void resBannedBusiness(String caller, Integer id);
	
}
