package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ARBillService;

@Controller
public class ARBillController extends BaseController {
	@Autowired
	private ARBillService ARBillService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveARBill.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ARBillService.saveARBill(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteARBill.action")  
	@ResponseBody 
	public Map<String, Object> deleteARBill(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ARBillService.deleteARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateARBill.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ARBillService.updateARBillById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printARBill.action")  
	@ResponseBody 
	public Map<String, Object> printARBill(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ARBillService.printARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitARBill.action")  
	@ResponseBody 
	public Map<String, Object> submitARBill(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ARBillService.submitARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitARBill.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitARBill(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ARBillService.resSubmitARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditARBill.action")  
	@ResponseBody 
	public Map<String, Object> auditARBill(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ARBillService.auditARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditARBill.action")  
	@ResponseBody 
	public Map<String, Object> resAuditARBill(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ARBillService.resAuditARBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
