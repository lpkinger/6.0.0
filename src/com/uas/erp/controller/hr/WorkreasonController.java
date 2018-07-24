package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.WorkreasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WorkreasonController {

	@Autowired
	private WorkreasonService workreasonService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/attendance/saveWorkreason.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workreasonService.saveWorkreason(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/attendance/updateWorkreason.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workreasonService.updateWorkreasonById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteWorkreason.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workreasonService.deleteWorkreason(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
