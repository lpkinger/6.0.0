package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.BOMCostService;

@Controller
public class BOMCostController extends BaseController {
	@Autowired
	private BOMCostService BOMCostService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/BOMCost/saveBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.saveBOMCost(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/BOMCost/deleteBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> deleteBOMCost(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.deleteBOMCost(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/BOMCost/updateBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.updateBOMCostById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/BOMCost/printBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> printBOMCost(int id,String reportName,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.printBOMCost(id,reportName,condition);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/BOMCost/submitBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> submitBOMCost(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.submitBOMCost(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/BOMCost/resSubmitBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitBOMCost(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.resSubmitBOMCost(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/BOMCost/auditBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> auditBOMCost(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.auditBOMCost(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/BOMCost/resAuditBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> resAuditBOMCost(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.resAuditBOMCost(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/sale/BOMCost/bannedBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> banned(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.bannedBOMCost(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/sale/BOMCost/resBannedBOMCost.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.resBannedBOMCost(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 导入建立BOM的产品
	 */
	@RequestMapping("/scm/sale/BOMCost/bominsert.action")  
	@ResponseBody 
	public Map<String, Object> bomInsert(int bc_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.bomInsert(bc_id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批量计算产品BOM成本
	 */
	@RequestMapping("/scm/sale/BOMCost/bomvastcost.action")  
	@ResponseBody 
	public Map<String, Object> bomVastCost(int bc_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BOMCostService.bomVastCost(bc_id);
		modelMap.put("success", true);
		return modelMap;
	}
}
