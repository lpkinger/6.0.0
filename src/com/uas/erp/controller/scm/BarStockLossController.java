package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BarStockLossService;

@Controller
public class BarStockLossController {

	@Autowired 
	private BarStockLossService  barStockLossService;

	/**
	 * 保存
	 */
	@RequestMapping("/scm/barStockLoss/saveLoss.action")  
	@ResponseBody 
	public Map<String, Object> saveLoss(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockLossService.saveLoss(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 
	 */
	@RequestMapping("/scm/barStockLoss/deleteLoss.action")  
	@ResponseBody 
	public Map<String, Object> deleteLoss(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockLossService.deleteLoss(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/barStockLoss/updateLoss.action")  
	@ResponseBody 
	public Map<String, Object> updateLoss(String caller,  String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockLossService.updateLoss(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/barStockLoss/auditLoss.action")  
	@ResponseBody 
	public Map<String, Object> auditLoss(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockLossService.auditLoss(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/barStockLoss/resAduitLoss.action")  
	@ResponseBody 
	public Map<String, Object> resAduitLoss(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockLossService.resAduitLoss(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/barStockLoss/submitLoss.action")  
	@ResponseBody 
	public Map<String, Object> submitLoss(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockLossService.submitLoss(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/barStockLoss/resSubmitLoss.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitLoss(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockLossService.resSubmitLoss(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
