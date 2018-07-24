package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.MaterialPriceService;

@Controller
public class MaterialPriceContorller extends BaseController {
	@Autowired
	private MaterialPriceService materialPriceService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveMaterialPrice.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materialPriceService.saveMaterialPrice(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteMaterialPrice.action")  
	@ResponseBody 
	public Map<String, Object> deleteMaterialPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materialPriceService.deleteMaterialPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateMaterialPrice.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materialPriceService.updateMaterialPriceById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printMaterialPrice.action")  
	@ResponseBody 
	public Map<String, Object> printMaterialPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materialPriceService.printMaterialPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitMaterialPrice.action")  
	@ResponseBody 
	public Map<String, Object> submitMaterialPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materialPriceService.submitMaterialPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitMaterialPrice.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMaterialPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materialPriceService.resSubmitMaterialPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditMaterialPrice.action")  
	@ResponseBody 
	public Map<String, Object> auditMaterialPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materialPriceService.auditMaterialPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditMaterialPrice.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMaterialPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		materialPriceService.resAuditMaterialPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
}
