package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ReportFilesFGService;

@Controller
public class ReportFilesFGController {
	@Autowired
	private ReportFilesFGService ReportFilesFGService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fp/saveReportFilesFG.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReportFilesFGService.saveReportFilesFG(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/fp/deleteReportFilesFG.action")
	@ResponseBody
	public Map<String, Object> deleteReportFilesFG(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReportFilesFGService.deleteReportFilesFG(id, caller);
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
	@RequestMapping("/fa/fp/updateReportFilesFG.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReportFilesFGService.updateReportFilesFG(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
