package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SendNotifyChangeService;

@Controller
public class SendNotifyChangeController extends BaseController {
	@Autowired
	private SendNotifyChangeService sendNotifyChangeService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveSendNotifyChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyChangeService.saveSendNotifyChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteSendNotifyChange.action")  
	@ResponseBody 
	public Map<String, Object> deletePurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyChangeService.deleteSendNotifyChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateSendNotifyChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyChangeService.updateSendNotifyChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printSendNotifyChange.action")  
	@ResponseBody 
	public Map<String, Object> printPurchaseChange(String caller, int id,String reportName,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys=sendNotifyChangeService.printSendNotifyChange(id, caller,reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSendNotifyChange.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyChangeService.submitSendNotifyChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSendNotifyChange.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyChangeService.resSubmitSendNotifyChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditSendNotifyChange.action")  
	@ResponseBody 
	public Map<String, Object> auditPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyChangeService.auditSendNotifyChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSendNotifyChange.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendNotifyChangeService.resAuditSendNotifyChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
