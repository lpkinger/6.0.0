package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ARBadDebtsService;

@Controller
public class ARBadDebtsController {
	@Autowired
	private ARBadDebtsService arBadDebtsService;

	/**
	 * 保存ARBadDebts
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/fa/ars/saveARBadDebts.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.saveARBadDebts(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/ars/updateARBadDebts.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.updateARBadDebtsById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteARBadDebts.action")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.deleteARBadDebts(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitARBadDebts.action")
	@ResponseBody
	public Map<String, Object> submitARBadDebts(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.submitARBadDebts(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitARBadDebts.action")
	@ResponseBody
	public Map<String, Object> resSubmitARBadDebts(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.resSubmitARBadDebts(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditARBadDebts.action")
	@ResponseBody
	public Map<String, Object> auditARBadDebts(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.auditARBadDebts(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditARBadDebts.action")
	@ResponseBody
	public Map<String, Object> resAuditARBadDebts(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.resAuditARBadDebts(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/ars/postARBadDebts.action")
	@ResponseBody
	public Map<String, Object> postARBadDebts(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.postARBadDebts(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/ars/resPostARBadDebts.action")
	@ResponseBody
	public Map<String, Object> resPostARBadDebts(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsService.resPostARBadDebts(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
