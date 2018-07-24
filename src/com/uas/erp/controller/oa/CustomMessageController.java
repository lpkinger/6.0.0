package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.CustomMessageService;

@Controller
public class CustomMessageController {
	@Autowired
	private CustomMessageService customMessageService;
	
	/*
	 * 保存客户消息设置
	 */
	@RequestMapping(value = "/custommessage/save.action")
	@ResponseBody
	public Map<String,Object> save(String formStore,String gridStore) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		customMessageService.save(formStore,gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 获取客户消息设置
	 */
	@RequestMapping(value = "/custommessage/getTree.action")
	@ResponseBody
	public Map<String,Object> getTree(String module,String caller) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap = customMessageService.getModule(module,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
