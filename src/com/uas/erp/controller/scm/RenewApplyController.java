package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.RenewApplyService;

@Controller
public class RenewApplyController extends BaseController {
	@Autowired
	private RenewApplyService renewApplyService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveRenewApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		renewApplyService.saveRenewApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteRenewApply.action")  
	@ResponseBody 
	public Map<String, Object> deleteRenewApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		renewApplyService.deleteRenewApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateRenewApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		renewApplyService.updateRenewApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printRenewApply.action")  
	@ResponseBody 
	public Map<String, Object> printRenewApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		renewApplyService.printRenewApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitRenewApply.action")  
	@ResponseBody 
	public Map<String, Object> submitRenewApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		renewApplyService.submitRenewApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitRenewApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitRenewApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		renewApplyService.resSubmitRenewApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditRenewApply.action")  
	@ResponseBody 
	public Map<String, Object> auditRenewApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		renewApplyService.auditRenewApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditRenewApply.action")  
	@ResponseBody 
	public Map<String, Object> resAuditRenewApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		renewApplyService.resAuditRenewApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
