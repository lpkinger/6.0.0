package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.PrePaidService;

@Controller
public class PrePaidController extends BaseController {
	@Autowired
	private PrePaidService prePaidService;

	@RequestMapping("/fa/gla/savePrePaid.action")
	@ResponseBody
	public Map<String, Object> saveAssetsLocation(HttpSession session,
			String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePaidService.savePrePaid(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/gla/deletePrePaid.action")
	@ResponseBody
	public Map<String, Object> deletePrePaid(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePaidService.deletePrePaid(id, caller);
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
	@RequestMapping("/fa/gla/updatePrePaid.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePaidService.updatePrePaidById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gla/auditPrePaid.action")
	@ResponseBody
	public Map<String, Object> auditPrePaid(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePaidService.auditPrePaid(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gla/resAuditPrePaid.action")
	@ResponseBody
	public Map<String, Object> resAuditPrePaid(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePaidService.resAuditPrePaid(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/gla/postPrePaid.action")
	@ResponseBody
	public Map<String, Object> postPrePaid(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePaidService.postPrePaid(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/gla/resPostPrePaid.action")
	@ResponseBody
	public Map<String, Object> resPostPrePaid(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePaidService.resPostPrePaid(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
