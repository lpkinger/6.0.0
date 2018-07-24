package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.FARepSetService;

@Controller
public class FARepSetController extends BaseController {
	@Autowired
	private FARepSetService fARepSetService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/ars/saveFARepSet.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		fARepSetService.saveFARepSet(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/ars/deleteFARepSet.action")
	@ResponseBody
	public Map<String, Object> deleteFARepSet(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		fARepSetService.deleteFARepSet(id, caller);
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
	@RequestMapping("/fa/ars/updateFARepSet.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		fARepSetService.updateFARepSetById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitFARepSet.action")
	@ResponseBody
	public Map<String, Object> submitFARepSet(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		fARepSetService.submitFARepSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitFARepSet.action")
	@ResponseBody
	public Map<String, Object> resSubmitFARepSet(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		fARepSetService.resSubmitFARepSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditFARepSet.action")
	@ResponseBody
	public Map<String, Object> auditFARepSet(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		fARepSetService.auditFARepSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditFARepSet.action")
	@ResponseBody
	public Map<String, Object> resAuditFARepSet(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		fARepSetService.resAuditFARepSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/ars/copyFARepSet.action")
	@ResponseBody
	public Map<String, Object> copyVoucher(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("farepset", fARepSetService.copyFARepSet(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
