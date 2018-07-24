package com.uas.erp.controller.plm;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.MileStoneChangeService;

@Controller
public class MileStoneChangeController extends BaseController {
	@Autowired
	private MileStoneChangeService mileStoneChangeService;
	/**
	 * 保存
	 */
	@RequestMapping("/plm/change/saveMileStoneChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneChangeService.saveMileStoneChange(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/plm/change/updateMileStoneChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneChangeService.updateMileStoneChangeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/plm/change/deleteMileStoneChange.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneChangeService.deleteMileStoneChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/plm/change/submitMileStoneChange.action")
	@ResponseBody
	public Map<String, Object> submitMileStoneChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneChangeService.submitMileStoneChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/plm/change/resSubmitMileStoneChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitMileStoneChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneChangeService.resSubmitMileStoneChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/plm/change/auditMileStoneChange.action")  
	@ResponseBody 
	public Map<String, Object> auditMileStoneChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneChangeService.auditMileStoneChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/plm/change/resAuditMileStoneChange.action")
	@ResponseBody
	public Map<String, Object> resAuditMileStoneChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mileStoneChangeService.resAuditMileStoneChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

