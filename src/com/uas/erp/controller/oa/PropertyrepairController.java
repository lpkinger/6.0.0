package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.PropertyrepairService;

@Controller
public class PropertyrepairController {

	@Autowired
	private PropertyrepairService propertyrepairService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/storage/savePropertyrepair.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyrepairService.savePropertyrepair(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/storage/updatePropertyrepair.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyrepairService
				.updatePropertyrepairById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/storage/deletePropertyrepair.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyrepairService.deletePropertyrepair(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/storage/submitPropertyrepair.action")
	@ResponseBody
	public Map<String, Object> submitPropertyrepair(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyrepairService.submitPropertyrepair(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/storage/resSubmitPropertyrepair.action")
	@ResponseBody
	public Map<String, Object> resSubmitPropertyrepair(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyrepairService.resSubmitPropertyrepair(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/storage/auditPropertyrepair.action")
	@ResponseBody
	public Map<String, Object> auditPropertyrepair(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyrepairService.auditPropertyrepair(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/storage/resAuditPropertyrepair.action")
	@ResponseBody
	public Map<String, Object> resAuditPropertyrepair(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyrepairService.resAuditPropertyrepair(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转维修记录
	 * 
	 * @param session
	 * @param id
	 * @return
	 */
	@RequestMapping("/oa/storage/Propertyrepair.action")
	@ResponseBody
	public Map<String, Object> turnRepairRecords(String caller, int id,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		propertyrepairService.turnRepairRecords(id, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
