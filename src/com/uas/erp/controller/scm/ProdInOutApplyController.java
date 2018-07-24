package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ProdInOutApplyService;

@Controller
public class ProdInOutApplyController extends BaseController {
	@Autowired
	private ProdInOutApplyService prodInOutApplyService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/reserve/saveProdInOutApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutApplyService.saveProdInOutApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/reserve/deleteProdInOutApply.action")  
	@ResponseBody 
	public Map<String, Object> deleteProdInOutApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutApplyService.deleteProdInOutApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/reserve/updateProdInOutApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutApplyService.updateProdInOutApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/reserve/submitProdInOutApply.action")  
	@ResponseBody 
	public Map<String, Object> submitProdInOutApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutApplyService.submitProdInOutApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/reserve/resSubmitProdInOutApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProdInOutApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutApplyService.resSubmitProdInOutApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditProdInOutApply.action")  
	@ResponseBody 
	public Map<String, Object> auditProdInOutApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutApplyService.auditProdInOutApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditProdInOutApply.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProdInOutApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutApplyService.resAuditProdInOutApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转采购验收单
	 */
	@RequestMapping("/scm/reserve/applyTurnProdIO.action")
	@ResponseBody
	public Map<String, Object> applyTurnProdIO(String data, String caller, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodInOutApplyService.applyTurnProdIO(caller, data, type);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
