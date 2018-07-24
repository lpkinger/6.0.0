package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.InvoiceService;

@Controller
public class InvoiceController extends BaseController {
	@Autowired
	private InvoiceService invoiceService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/reserve/saveInvoice.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.saveInvoice(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/reserve/deleteInvoice.action")
	@ResponseBody
	public Map<String, Object> deleteInvoice(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.deleteInvoice(id);
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
	@RequestMapping("/scm/reserve/updateInvoice.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.updateInvoiceById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/reserve/printInvoice.action")
	@ResponseBody
	public Map<String, Object> printInvoice(int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = invoiceService.printInvoice(id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/reserve/submitInvoice.action")
	@ResponseBody
	public Map<String, Object> submitInvoice(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.submitInvoice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/reserve/resSubmitInvoice.action")
	@ResponseBody
	public Map<String, Object> resSubmitInvoice(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.resSubmitInvoice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditInvoice.action")
	@ResponseBody
	public Map<String, Object> auditInvoice(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.auditInvoice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditInvoice.action")
	@ResponseBody
	public Map<String, Object> resAuditInvoice(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.resAuditInvoice(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 信扬获取单价
	 * //scm/reserve/getSalePrice.action
	 */
	@RequestMapping("/scm/reserve/getSalePrice.action")
	@ResponseBody
	public Map<String,Object> getSalePrice(int in_id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.getSalePrice(in_id);
		modelMap.put("success", true);
		return modelMap;
	}//scm/reserve/savePreInvoice.action
	/**
	 * 信扬
	 */
	@RequestMapping("/scm/reserve/savePreInvoice.action")
	@ResponseBody
	public Map<String,Object> savePreInvoice(String gridStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		invoiceService.savePreInvoice(gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
}
