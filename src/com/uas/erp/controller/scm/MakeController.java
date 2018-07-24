package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.MakeService;

@Controller
public class MakeController extends BaseController {
	@Autowired
	private MakeService makeService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveMake.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.saveMake(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteMake.action")  
	@ResponseBody 
	public Map<String, Object> deleteMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.deleteMake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateMake.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.updateMakeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printMake.action")  
	@ResponseBody 
	public Map<String, Object> printMake(String caller, int id,String reportName,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys=makeService.printMake(id, caller,reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitMake.action")  
	@ResponseBody 
	public Map<String, Object> submitMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.submitMake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitMake.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.resSubmitMake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditMake.action")  
	@ResponseBody 
	public Map<String, Object> auditMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.auditMake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditMake.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.resAuditMake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/purchase/bannedMake.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.bannedMake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/purchase/resBannedMake.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeService.resBannedMake(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
