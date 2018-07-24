package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ComplaintTypeService;

@Controller
public class ComplaintTypeController {
	@Autowired
	private ComplaintTypeService complaintTypeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("crm/aftersalemgr/saveComplaintType.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintTypeService.saveComplaintType(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 * 
	 */
	@RequestMapping("crm/aftersalemgr/deleteComplaintType.action")
	@ResponseBody
	public Map<String, Object> deleteChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintTypeService.deleteComplaintType(id, caller);
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
	@RequestMapping("crm/aftersalemgr/updateComplaintType.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		complaintTypeService.updateComplaintType(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
