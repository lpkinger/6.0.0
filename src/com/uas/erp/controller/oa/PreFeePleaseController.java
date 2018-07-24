package com.uas.erp.controller.oa;

import java.util.HashMap;

import java.util.Map;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.oa.PreFeePleaseService;

@Controller
public class PreFeePleaseController extends BaseController {
	@Autowired
	private PreFeePleaseService preFeePleaseService;
	/**
	 * 保存
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/oa/fee/savePreFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preFeePleaseService.savePreFeePlease(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 */
	@RequestMapping("/oa/fee/deletePreFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preFeePleaseService.deletePreFeePlease(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/oa/fee/updatePreFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preFeePleaseService.updatePreFeePleaseById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/oa/fee/submitPreFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchase(int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preFeePleaseService.submitPreFeePlease(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/oa/fee/resSubmitPreFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preFeePleaseService.resSubmitPreFeePlease(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/oa/fee/auditPreFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preFeePleaseService.auditPreFeePlease(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditPreFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preFeePleaseService.resAuditPreFeePlease(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转费用报销单
	 */
	@RequestMapping("/oa/prefeeplease/turnFYBX.action")
	@ResponseBody
	public Map<String, Object> turnFYBX(int id, String caller) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int fpid = preFeePleaseService.turnFYBX(id, "FeePlease!FYBX");
		modelMap.put("id", fpid);
		modelMap.put("success", true);
		return modelMap;
	}
}

