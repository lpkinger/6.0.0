package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.JobDocumentListPowerService;

@Controller
public class JobDocumentListPowerController {

	@Autowired
	private JobDocumentListPowerService jobDocumentListPowerService;

	// @Autowired
	// private DocumentListPowerService documentListPowerService;
	/**
	 * 保存
	 */
	@RequestMapping("/hr/employee/updateJobDocumentListPower.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String update) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobDocumentListPowerService.update(update, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/employee/getJobDocumentListPower.action")
	@ResponseBody
	public Map<String, Object> get(String caller, int dcl_id, int em_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("documentlistpower",
				jobDocumentListPowerService.getDLPByDclIdAndJoID(dcl_id, em_id));
		modelMap.put("success", true);
		return modelMap;
	}
}
