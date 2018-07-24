package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.DormitoryService;

@Controller
public class DormitoryController {
	@Autowired
	private DormitoryService dormitoryService;

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/updateDormitory.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryService.update(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细时，空床位数也同时改变
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/updateBednull.action")
	@ResponseBody
	public Map<String, Object> updateBednull(String caller, String condition,
			int bednull) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryService.updateBednull(condition, bednull, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除批量明细
	 */
	@RequestMapping("/oa/publicAdmin/dormitory/Dormitory/VastDeal.action")
	@ResponseBody
	public Map<String, Object> VastDeal(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		dormitoryService.VastDeal(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
