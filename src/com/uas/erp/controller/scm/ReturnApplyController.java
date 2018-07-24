package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ReturnApplyService;

@Controller
public class ReturnApplyController extends BaseController {
	@Autowired
	private ReturnApplyService returnApplyService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveReturnApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		returnApplyService.saveReturnApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteReturnApply.action")  
	@ResponseBody 
	public Map<String, Object> deleteReturnApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		returnApplyService.deleteReturnApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateReturnApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		returnApplyService.updateReturnApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printReturnApply.action")  
	@ResponseBody 
	public Map<String, Object> printReturnApply(int id, String reportName, String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = returnApplyService.printReturnApply(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitReturnApply.action")  
	@ResponseBody 
	public Map<String, Object> submitReturnApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		returnApplyService.submitReturnApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitReturnApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitReturnApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		returnApplyService.resSubmitReturnApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditReturnApply.action")  
	@ResponseBody 
	public Map<String, Object> auditReturnApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		returnApplyService.auditReturnApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditReturnApply.action")  
	@ResponseBody 
	public Map<String, Object> resAuditReturnApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		returnApplyService.resAuditReturnApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转退货单
	 */
	@RequestMapping("/scm/sale/turnReturn.action")  
	@ResponseBody 
	public Map<String, Object> turnPurchase(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = returnApplyService.turnReturn(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
