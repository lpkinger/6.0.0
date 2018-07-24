package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ResourceDistrService;

@Controller
public class ResourceDistrController {
	@Autowired
	private ResourceDistrService resourceDistrService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/resource/saveResourceDistr.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		resourceDistrService.saveResourceDistr(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/resource/deleteResourceDistr.action")
	@ResponseBody
	public Map<String, Object> deleteCustomerDistr(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		resourceDistrService.deleteResourceDistr(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/resource/updateResourceDistr.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		resourceDistrService.updateResourceDistr(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
