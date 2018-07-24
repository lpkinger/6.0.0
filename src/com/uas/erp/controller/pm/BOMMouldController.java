package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.BOMMouldService;

@Controller
public class BOMMouldController {
	
	
	@Autowired
	private BOMMouldService bomMouldService;
	
	
	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/saveBOMMould.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.saveBOMMould(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deleteBOMMould.action")
	@ResponseBody
	public Map<String, Object> deleteBOMMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.deleteBOMMould(id, caller);
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
	@RequestMapping("/pm/mould/updateBOMMould.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.updateBOMMouldById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printBOMMould.action")
	@ResponseBody
	public Map<String, Object> printBOMMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.printBOMMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitBOMMould.action")
	@ResponseBody
	public Map<String, Object> submitBOMMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.submitBOMMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitBOMMould.action")
	@ResponseBody
	public Map<String, Object> resSubmitBOMMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.resSubmitBOMMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditBOMMould.action")
	@ResponseBody
	public Map<String, Object> auditBOMMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.auditBOMMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditBOMMould.action")
	@ResponseBody
	public Map<String, Object> resAuditBOMMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.resAuditBOMMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新BOM模具明细的加工方式
	 */
	@RequestMapping("/pm/mould/updateBOMMouldProcessing.action")
	@ResponseBody
	public Map<String,Object> updateBOMMouldProcessing(String formStore, String param, String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomMouldService.updateBOMMouldProcessing(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
