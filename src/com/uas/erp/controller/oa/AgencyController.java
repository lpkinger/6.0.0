package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.AgencyService;

@Controller
public class AgencyController {
	@Autowired
	private AgencyService agencyService;

	/**
	 * 保存
	 */
	@RequestMapping("/oa/officialDocument/fileManagement/agency/saveAgency.action")
	@ResponseBody
	public Map<String, Object> save(String caller, HttpServletRequest request,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agencyService.save(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/officialDocument/fileManagement/agency/updateAgency.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agencyService.updateAgency(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/officialDocument/fileManagement/agency/deleteAgency.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agencyService.deleteAgency(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
