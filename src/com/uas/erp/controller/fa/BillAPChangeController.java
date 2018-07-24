package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.BillAPChangeService;

@Controller
public class BillAPChangeController extends BaseController {
	@Autowired
	private BillAPChangeService billAPChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/saveBillAPChange.action")
	@ResponseBody
	public Map<String, Object> saveBillAPChange(HttpSession session,
			String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.saveBillAPChange(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/gs/deleteBillAPChange.action")
	@ResponseBody
	public Map<String, Object> deleteBillAPChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.deleteBillAPChange(id, caller);
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
	@RequestMapping("/fa/gs/updateBillAPChange.action")
	@ResponseBody
	public Map<String, Object> updateBillAPChange(HttpSession session,
			String formStore, String param, String param2, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.updateBillAPChangeById(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/gs/printBillAPChange.action")
	@ResponseBody
	public Map<String, Object> printBillAPChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.printBillAPChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/gs/submitBillAPChange.action")
	@ResponseBody
	public Map<String, Object> submitBillAPChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.submitBillAPChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gs/resSubmitBillAPChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitBillAPChange(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.resSubmitBillAPChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gs/auditBillAPChange.action")
	@ResponseBody
	public Map<String, Object> auditBillAPChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.auditBillAPChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gs/resAuditBillAPChange.action")
	@ResponseBody
	public Map<String, Object> resAuditBillAPChange(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.resAuditBillAPChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 记账
	 */
	@RequestMapping("/fa/gs/accountBillAPChange.action")
	@ResponseBody
	public Map<String, Object> accountBillAPChange(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.accountedBillAPChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反记账
	 */
	@RequestMapping("/fa/gs/resAccountBillAPChange.action")
	@ResponseBody
	public Map<String, Object> resAccountBillAPChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billAPChangeService.resAccountedBillAPChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
