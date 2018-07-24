package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.ObjectExplainService;



@Controller
public class ObjectExplainController {
	@Autowired
	private ObjectExplainService objectExplainService;

	/*
	 * 获取objectexplain
	 */
	@RequestMapping(value = "/ma/objectexplain/getData.action")
	@ResponseBody
	public Map<String,Object> getData(String condition) {
		Map<String,Object> modelMap;
		modelMap = objectExplainService.getData(condition);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 保存和更新objectexplain
	 */
	@RequestMapping(value = "/ma/objectexplain/save.action")
	@ResponseBody
	public Map<String,Object> saveObjectExplain(String formStore,String param) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		objectExplainService.saveObjectExplain(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
}
