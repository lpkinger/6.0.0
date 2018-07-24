package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.BillARChangeService;

@Controller
public class BillARChangeController extends BaseController {
	@Autowired
	private BillARChangeService billARChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/saveBillARChange.action")
	@ResponseBody
	public Map<String, Object> saveBillARChange(HttpSession session,
			String formStore, String param, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.saveBillARChange(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/gs/deleteBillARChange.action")
	@ResponseBody
	public Map<String, Object> deleteBillARChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.deleteBillARChange(id, caller);
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
	@RequestMapping("/fa/gs/updateBillARChange.action")
	@ResponseBody
	public Map<String, Object> updateBillARChange(HttpSession session,
			String formStore, String param, String param2, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.updateBillARChangeById(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/gs/printBillARChange.action")
	@ResponseBody
	public Map<String, Object> printBillARChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.printBillARChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/gs/submitBillARChange.action")
	@ResponseBody
	public Map<String, Object> submitBillARChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.submitBillARChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gs/resSubmitBillARChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitBillARChange(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.resSubmitBillARChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gs/auditBillARChange.action")
	@ResponseBody
	public Map<String, Object> auditBillARChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.auditBillARChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gs/resAuditBillARChange.action")
	@ResponseBody
	public Map<String, Object> resAuditBillARChange(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.resAuditBillARChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 记账
	 */
	@RequestMapping("/fa/gs/accountBillARChange.action")
	@ResponseBody
	public Map<String, Object> accountBillARChange(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.accountedBillARChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反记账
	 */
	@RequestMapping("/fa/gs/resAccountBillARChange.action")
	@ResponseBody
	public Map<String, Object> resAccountBillARChange(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		billARChangeService.resAccountedBillARChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
