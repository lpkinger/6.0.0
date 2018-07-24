package com.uas.erp.controller.bench;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.interceptor.InterceptorUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.bench.MABenchService;

@Controller
public class MABenchController {
	@Autowired
	MABenchService maBenchService;
	
	//是否需要权限控制
	private boolean noControl(HttpServletRequest req){
		Employee employee = (Employee) req.getSession().getAttribute("employee");
		if (InterceptorUtil.noControl(req)||"admin".equals(employee.getEm_type())){
			return true;
		}
		return false;
	}
	
	@RequestMapping(value = "/bench/ma/getBenchTree.action")
	@ResponseBody
	public Map<String,Object> getBenchTree(Boolean isRoot){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = maBenchService.getBenchTree(isRoot);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value = "/bench/ma/searchBenchTree.action")
	@ResponseBody
	public Map<String,Object> searchBenchTree(String search){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = maBenchService.searchBenchTree(search);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value = "/bench/ma/getActionsData.action")
	@ResponseBody
	public Map<String,Object> getActionsData(String caller, String alias){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", maBenchService.getActionsData(caller,alias));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value = "/bench/ma/getBenchList.action")
	@ResponseBody
	public Map<String,Object> getBenchList(String condition){		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("benches", maBenchService.getBenchList(condition));
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/getBenchScenes.action")
	@ResponseBody
	public Map<String,Object> getBenchScenes(String bccode){	
		Map<String,Object> map = maBenchService.getBenchScenes(bccode);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/saveBench.action")
	@ResponseBody
	public Map<String,Object> saveBench(String bench){	
		Map<String,Object> map = new HashMap<String,Object>();
		maBenchService.saveBench(bench);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/deleteBench.action")
	@ResponseBody
	public Map<String,Object> deleteBench(String benchcode){	
		Map<String,Object> map = new HashMap<String,Object>();
		maBenchService.deleteBench(benchcode);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/getBenchButtons.action")
	@ResponseBody
	public Map<String,Object> getBenchButtons(String benchcode){	
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("buttons",maBenchService.getBenchButtons(benchcode));
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/saveBenchButtons.action")
	@ResponseBody
	public Map<String,Object> saveBenchButtons(String benchButtons,String bccode){	
		Map<String,Object> map = new HashMap<String,Object>();
		maBenchService.saveBenchButtons(benchButtons,SystemSession.getUser().getEm_master(),bccode);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/deleteBenchButtons.action")
	@ResponseBody
	public Map<String,Object> deleteBenchButtons(String ids,String bccode){	
		Map<String,Object> map = new HashMap<String,Object>();
		maBenchService.deleteBenchButtons(ids,SystemSession.getUser().getEm_master(),bccode);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/saveScene.action")
	@ResponseBody
	public Map<String,Object> saveScene(String caller, String formStore,String param,String param1){	
		Map<Object, Object> form = BaseUtil.parseFormStoreToMap(formStore);
		String sob = SystemSession.getUser().getEm_master();
		Map<String,Object> map = maBenchService.saveScene(caller, form, param, param1,sob);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/updateScene.action")
	@ResponseBody
	public Map<String,Object> updateScene(String caller,String formStore,String param,String param1,String param2,String param3){
		Employee employee = SystemSession.getUser();
		Map<Object, Object> form = BaseUtil.parseFormStoreToMap(formStore);
		Map<String,Object> map = maBenchService.updateScene(caller, employee, form, param, param1, param2, param3);
		map.put("success", true);
		return map;
	}
	
	@RequestMapping(value = "/bench/ma/deleteScene.action")
	@ResponseBody
	public Map<String,Object> deleteScene(String caller,String scenecode){	
		Map<String,Object> map = new HashMap<String,Object>();
		maBenchService.deleteScene(caller, scenecode);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 场景列表维护--重置下拉框
	 */
	@RequestMapping(value = "/bench/ma/resetCombo.action")
	@ResponseBody
	public Map<String, Object> resetCombo(String caller, String field) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String sob = SystemSession.getUser().getEm_master();
		modelMap.put("error", maBenchService.resetCombo(caller, field, sob));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 复制列表
	 */
	@RequestMapping(value = "/bench/ma/copyDataList.action")
	@ResponseBody
	public Map<String, Object> copyDataList(Integer id, String newtitle) {
		Map<String, Object> modelMap = maBenchService.copy(id, newtitle);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取个人业务设置
	 */
	@RequestMapping(value = "/bench/ma/getSelfBusiness.action")
	@ResponseBody
	public Map<String, Object> getSelfBusiness(HttpServletRequest req, String benchcode) {
		boolean noControl = noControl(req);
		Map<String, Object> modelMap = maBenchService.getSelfBusiness(benchcode, noControl);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存个人业务设置
	 */
	@RequestMapping(value = "/bench/ma/saveSelfBusiness.action")
	@ResponseBody
	public Map<String, Object> saveSelfBusiness(String benchcode, String datas) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maBenchService.saveSelfBusiness(benchcode, datas);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取个人场景设置
	 */
	@RequestMapping(value = "/bench/ma/getSelfScene.action")
	@ResponseBody
	public Map<String, Object> getSelfScene(HttpServletRequest req, String benchcode) {
		boolean noControl = noControl(req);
		Map<String, Object> modelMap = maBenchService.getSelfScene(benchcode, noControl);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存个人场景设置
	 */
	@RequestMapping(value = "/bench/ma/saveSelfScene.action")
	@ResponseBody
	public Map<String, Object> saveSelfScene(String benchcode,String datas) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maBenchService.saveSelfScene(benchcode,datas);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 重置个人设置
	 */
	@RequestMapping(value = "/bench/ma/selfReset.action")
	@ResponseBody
	public Map<String, Object> selfReset(String benchcode,boolean isBusiness) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maBenchService.selfReset(benchcode,isBusiness);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 根据caller获取默认列表配置
	 */
	@RequestMapping(value = "/bench/ma/getSetByCaller.action")
	@ResponseBody
	public Map<String, Object> getSetByCaller(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",maBenchService.getSetByCaller(caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存工作台业务
	 */
	@RequestMapping(value = "/bench/ma/saveBusiness.action")
	@ResponseBody
	public Map<String,Object> saveBusiness(String caller, String formStore){	
		Map<String,Object> modelMap = new HashMap<String,Object>();
		maBenchService.saveBusiness(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新工作台业务
	 */
	@RequestMapping(value = "/bench/ma/updateBusiness.action")
	@ResponseBody
	public Map<String,Object> updateBusiness(String caller, String formStore){	
		Map<String,Object> modelMap = new HashMap<String,Object>();
		maBenchService.updateBusiness(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除工作台业务
	 */
	@RequestMapping(value = "/bench/ma/deleteBusiness.action")
	@ResponseBody
	public Map<String,Object> deleteBusiness(String caller, Integer id){	
		Map<String,Object> modelMap = new HashMap<String,Object>();
		maBenchService.deleteBusiness(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 禁用工作台业务
	 */
	@RequestMapping(value = "/bench/ma/bannedBusiness.action")
	@ResponseBody
	public Map<String,Object> bannedBusiness(String caller, Integer id){	
		Map<String,Object> modelMap = new HashMap<String,Object>();
		maBenchService.bannedBusiness(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反禁用工作台业务
	 */
	@RequestMapping(value = "/bench/ma/resBannedBusiness.action")
	@ResponseBody
	public Map<String,Object> resBannedBusiness(String caller, Integer id){	
		Map<String,Object> modelMap = new HashMap<String,Object>();
		maBenchService.resBannedBusiness(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
