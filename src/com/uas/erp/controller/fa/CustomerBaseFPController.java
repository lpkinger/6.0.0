package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CustomerBaseFPService;

@Controller
public class CustomerBaseFPController {
	@Autowired
	private CustomerBaseFPService customerBaseFPService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.saveCustomerFP(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 判断customer是否已在客户列表
	 */
	@RequestMapping("/fa/fp/checkCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> checkCustomer(HttpSession session,
			int cu_otherenid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int cu_enid = (Integer) session.getAttribute("en_uu");
		modelMap.put("success", true);
		if (!customerBaseFPService.checkCustomerFPByEnId(cu_enid, cu_otherenid)) {
			modelMap.put("success", false);
		}
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/fp/updateCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.updateCustomerFP(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改CustomerCreditSet
	 */
	@RequestMapping("/fa/fp/updateCustomerFPCreditSet.action")
	@ResponseBody
	public Map<String, Object> updateCustomerFPCreditSet(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.updateCustomerFPCreditSet(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.deleteCustomerFP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.auditCustomerFP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.resAuditCustomerFP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.submitCustomerFP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.resSubmitCustomerFP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用
	 */
	@RequestMapping("/fa/fp/bannedCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.bannedCustomerFP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/fa/fp/resBannedCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.resBannedCustomerFP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 解挂
	 * */
	@RequestMapping("fa/fp/submitHandleHangCustomerBaseFP.action")
	@ResponseBody
	public Map<String, Object> HandleHangCustomerBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerBaseFPService.submitHandleHangCustomerBaseFP(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
