package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.RepairrecordService;

@Controller
public class RepairrecordController {

	@Autowired
	private RepairrecordService repairrecordService;

	/**
	 * 删除
	 */
	@RequestMapping("/oa/storage/deleteRepairrecord.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairrecordService.deleteRepairrecord(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
