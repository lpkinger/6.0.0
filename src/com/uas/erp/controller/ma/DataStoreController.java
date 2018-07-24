package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.ma.DataStoreService;

@Controller
public class DataStoreController {
	
	@Autowired
	private DataStoreService dataStoreService;
	/**
	 * 保存
	 */
	@RequestMapping("/ma/saveDataStore.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore,  String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataStoreService.save(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/ma/deleteDataStore.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataStoreService.delete(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更新
	 * */
	@RequestMapping("/ma/updateDataStore.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session,String formStore,  String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dataStoreService.update(formStore,param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 根据dataStore名称 获得允许查看字段的
	 * */
	@RequestMapping("/ma/getFieldsByTable.action")
	@ResponseBody
	public Map<String,Object> getFieldsByTable(HttpSession session,int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", dataStoreService.getFieldsByTable(id));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("ma/getExcelFxsByTable.action")
	@ResponseBody
	public Map<String,Object> getExcelFxsByTable(HttpSession session,int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", dataStoreService.getExcelFxsByTable(id));
		modelMap.put("success", true);
		return modelMap;
	}
}
