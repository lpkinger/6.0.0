package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BarcodeSetService;

@Controller
public class BarcodeSetController {

	@Autowired 
	private BarcodeSetService  barcodeSetService;
	/**
	 * 保存Serial序列号生成规则
	 */
	@RequestMapping("/scm/reserve/saveSerail.action")  
	@ResponseBody 
	public Map<String, Object> saveSerail(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.saveSerail(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新Serial序列号生成规则
	 */
	@RequestMapping("/scm/reserve/updateSerail.action")  
	@ResponseBody 
	public Map<String, Object> updateSerail(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.updateSerail(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	

	@RequestMapping("/scm/reserve/deleteSerail.action")  
	@ResponseBody 
	public Map<String, Object> deleteSerail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.deleteSerail(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/auditSerail.action")  
	@ResponseBody 
	public Map<String, Object> auditSerail(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.auditSerail(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/reserve/resAuditSerail.action")  
	@ResponseBody 
	public Map<String, Object> resAuditSerail(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.resAuditSerail(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/reserve/bannedSerail.action")  
	@ResponseBody 
	public Map<String, Object> bannedSerail(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.bannedSerial(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/resBannedSerail.action")  
	@ResponseBody 
	public Map<String, Object> resBannedSerail(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.resBannedSerail(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/submitSerail.action")  
	@ResponseBody 
	public Map<String, Object> submitSerail(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.submitSerail(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/reserve/resSubmitSerail.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitSerail(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barcodeSetService.resSubmitSerail(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}

}
