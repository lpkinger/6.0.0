package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.OATaskService;

@Controller
public class OATaskController {
	@Autowired
	private OATaskService oaTaskService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/myProcess/saveOATask.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param1, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaTaskService.saveOATask(formStore, param1, param2, param3, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/myProcess/deleteOATask.action")
	@ResponseBody
	public Map<String, Object> deleteMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaTaskService.deleteOATask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/myProcess/updateOATask.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param1, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaTaskService.updateOATask(formStore, param1, param2, param3, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
