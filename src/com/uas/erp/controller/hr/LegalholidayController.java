package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.LegalholidayService;

@Controller
public class LegalholidayController {

	@Autowired
	private LegalholidayService legalholidayService;

	/**
	 * 保存legalholiday
	 */
	@RequestMapping("/hr/check/saveLegalholiday.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		legalholidayService.saveLegalholiday(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/check/updateLegalholiday.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		legalholidayService.updateLegalholidayById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/check/deleteLegalholiday.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		legalholidayService.deleteLegalholiday(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
