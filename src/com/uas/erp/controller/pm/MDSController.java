package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.MDSService;

@Controller
public class MDSController {
	@Autowired
	private MDSService mDSService;

	@RequestMapping("pm/mds/saveMDS.action")
	@ResponseBody
	public Map<String, Object> saveProjectPlan(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mDSService.saveMDS(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mds/updateMDS.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mDSService.updateMDSById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mds/deleteMDS.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mDSService.deleteMDS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("pm/mds/auditMDS.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mDSService.auditMDS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("pm/mds/resAuditMDS.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mDSService.resAuditMDS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("pm/mds/submitMDS.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mDSService.submitMDS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("pm/mds/resSubmitMDS.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mDSService.resSubmitMDS(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("pm/mds/deleteAllDetails.action")
	@ResponseBody
	public Map<String, Object> deleteAllDetails(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mDSService.deleteAllDetails(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
