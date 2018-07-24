package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.VisitRecord4Service;

@Controller
public class VisitRecord4Controller {
	@Autowired
	private VisitRecord4Service record4Service;

	/**
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/customermgr/saveVisitRecord4.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param1,
			String param2, String param3, String param4, String param5,
			String param6, String param7, String param8, String param9,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param1, param2, param3, param4,
				param5, param6, param7, param8, param9 };
		record4Service.saveVisitRecord(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/customermgr/deleteVisitRecord4.action")
	@ResponseBody
	public Map<String, Object> deleteVisitRecord(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		record4Service.deleteVisitRecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/customermgr/updateVisitRecord4.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param1,
			String param2, String param3, String param4, String param5,
			String param6, String param7, String param8, String param9,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param1, param2, param3, param4,
				param5, param6, param7, param8, param9 };
		record4Service.updateVisitRecordById(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转差旅报销申请
	 */
	@RequestMapping("/crm/customermgr/turnFeePlease4.action")
	@ResponseBody
	public Map<String, Object> turnFeePlease(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = record4Service.turnFeePlease(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
}
