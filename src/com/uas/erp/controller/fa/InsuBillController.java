package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.InsuBillService;

@Controller
public class InsuBillController extends BaseController {
	@Autowired
	private InsuBillService insuBillService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fix/saveInsuBill.action")
	@ResponseBody
	public Map<String, Object> saveInsuBill(HttpSession session, String caller,
			String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		insuBillService.saveInsuBill(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 注意:
	 */
	@RequestMapping("/fa/fix/deleteInsuBill.action")
	@ResponseBody
	public Map<String, Object> deleteInsuBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		insuBillService.deleteInsuBill(caller, id);
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
	@RequestMapping("/fa/fix/updateInsuBill.action")
	@ResponseBody
	public Map<String, Object> updateInsuBill(HttpSession session,
			String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		insuBillService.updateInsuBillById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/fix/printInsuBill.action")
	@ResponseBody
	public Map<String, Object> printInsuBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		insuBillService.printInsuBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fix/submitInsuBill.action")
	@ResponseBody
	public Map<String, Object> submitInsuBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		insuBillService.submitInsuBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fix/resSubmitInsuBill.action")
	@ResponseBody
	public Map<String, Object> resSubmitInsuBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		insuBillService.resSubmitInsuBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/fix/auditInsuBill.action")
	@ResponseBody
	public Map<String, Object> auditInsuBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		insuBillService.auditInsuBill(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/fix/resAuditInsuBill.action")
	@ResponseBody
	public Map<String, Object> resAuditInsuBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		insuBillService.resAuditInsuBill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
