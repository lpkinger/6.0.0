package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.fa.FeeCategorySetService;

@Controller
public class FeeCategorySetController {
	@Autowired
	private FeeCategorySetService feeCategorySetService;
	/**
	 * 保存FeeCategorySet
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/fa/fp/saveFeeCategorySet.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeCategorySetService.saveFeeCategorySet(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateFeeCategorySet.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeCategorySetService.updateFeeCategorySetById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteFeeCategorySet.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feeCategorySetService.deleteFeeCategorySet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
