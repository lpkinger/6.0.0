package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BarOrPackReportService;


@Controller
public class BarOrPackReportController {
	@Autowired
	private BarOrPackReportService barOrPackReportService;


	@RequestMapping("/scm/reserve/deleteReportFile.action")
	@ResponseBody
	public Map<String, Object> deleteReportFilesFG(String callers, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barOrPackReportService.deleteReportFile(callers, caller);
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
	@RequestMapping("/scm/reserve/updateReportFile.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barOrPackReportService.updateReportFile(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
