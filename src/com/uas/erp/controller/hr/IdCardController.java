package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.IdCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class IdCardController {

	@Autowired
	private IdCardService idCardService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/attendance/saveIdCard.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		idCardService.saveIdCard(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/attendance/updateIdCard.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		idCardService.updateIdCardById(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteIdCard.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		idCardService.deleteIdCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
