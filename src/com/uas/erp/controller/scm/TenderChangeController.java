package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.TenderChangeService;
@Controller
public class TenderChangeController {

	@Autowired
	TenderChangeService tenderChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/saveTenderChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderChangeService.saveTenderChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteTenderChange.action")
	@ResponseBody
	public Map<String, Object> deleteTenderChangeio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	tenderChangeService.deleteTenderChange(id, caller);
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
	@RequestMapping("/scm/purchase/updateTenderChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderChangeService.updateTenderChangeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitTenderChange.action")
	@ResponseBody
	public Map<String, Object> submitTenderChangeio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderChangeService.submitTenderChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitTenderChange.action")
	@ResponseBody     
	public Map<String, Object> resSubmitTenderChangeio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderChangeService.resSubmitTenderChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditTenderChange.action")
	@ResponseBody
	public Map<String, Object> auditTenderChangeio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tenderChangeService.auditTenderChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
