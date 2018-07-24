package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.CheckECRService;

@Controller
public class CheckECRController extends BaseController {
	@Autowired
	private CheckECRService checkECRService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveCheck.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.saveCheck(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteCheck.action")
	@ResponseBody
	public Map<String, Object> deleteCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.deleteCheck(id, caller);
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
	@RequestMapping("/pm/bom/updateCheck.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.updateCheckById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/bom/submitCheck.action")
	@ResponseBody
	public Map<String, Object> submitCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.submitCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/bom/resSubmitCheck.action")
	@ResponseBody
	public Map<String, Object> resSubmitCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.resSubmitCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/bom/auditCheck.action")
	@ResponseBody
	public Map<String, Object> auditCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.auditCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/bom/resAuditCheck.action")
	@ResponseBody
	public Map<String, Object> resAuditCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.resAuditCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转工程变更申请单
	 */
	@RequestMapping("/pm/bom/turnECN.action")
	@ResponseBody
	public Map<String, Object> turnPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		Map<String, Object> resM = new HashMap<String, Object>();
		resM = checkECRService.turnECN(id, caller);	
		if(resM.get("error")!=null && resM.get("error")!="" ){
		  modelMap.put("error", resM.get("error"));
		}
		modelMap.put("id", resM.get("ecnid"));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 结案
	 */
	@RequestMapping("/pm/bom/endCheck.action")
	@ResponseBody
	public Map<String, Object> endCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.endCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反结案
	 */
	@RequestMapping("/pm/bom/resEndCheck.action")
	@ResponseBody
	public Map<String, Object> resEndCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkECRService.resEndCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
