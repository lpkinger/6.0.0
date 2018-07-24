package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PackingService;

@Controller
public class PackingController extends BaseController {
	@Autowired
	private PackingService packingService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/reserve/savePacking.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packingService.savePacking(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/reserve/deletePacking.action")  
	@ResponseBody 
	public Map<String, Object> deletePacking(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packingService.deletePacking(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/reserve/updatePacking.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packingService.updatePackingById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/reserve/printPacking.action")  
	@ResponseBody 
	public Map<String, Object> printPacking(String caller, int id,String reportName,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys=packingService.printPacking(id, caller,reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/reserve/submitPacking.action")  
	@ResponseBody 
	public Map<String, Object> submitPacking(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packingService.submitPacking(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/reserve/resSubmitPacking.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPacking(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packingService.resSubmitPacking(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditPacking.action")  
	@ResponseBody 
	public Map<String, Object> auditPacking(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packingService.auditPacking(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditPacking.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPacking(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packingService.resAuditPacking(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 成本计算
	 */
	@RequestMapping("/scm/reserve/updateMadeIn.action")  
	@ResponseBody 
	public Map<String, Object> updateMadeIn(String caller, int pi_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		packingService.updateMadeIn(pi_id);
		modelMap.put("success", true);
		return modelMap;
	}
}
