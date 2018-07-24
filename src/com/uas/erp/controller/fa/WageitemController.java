package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.WageitemService;

@Controller
public class WageitemController extends BaseController {
	@Autowired
	private WageitemService WageItemService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/wg/saveWageItem.action")
	@ResponseBody
	public Map<String, Object> saveWageItem(HttpSession session,
			String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		WageItemService.saveWageItem(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/wg/deleteWageItem.action")
	@ResponseBody
	public Map<String, Object> deleteWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		WageItemService.deleteWageItem(id, caller);
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
	@RequestMapping("/fa/wg/updateWageItem.action")
	@ResponseBody
	public Map<String, Object> updateWageItem(HttpSession session,
			String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		WageItemService.updateWageItemById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/wg/submitWageItem.action")
	@ResponseBody
	public Map<String, Object> submitWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		WageItemService.submitWageItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/wg/resSubmitWageItem.action")
	@ResponseBody
	public Map<String, Object> resSubmitWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		WageItemService.resSubmitWageItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/wg/auditWageItem.action")
	@ResponseBody
	public Map<String, Object> auditWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		WageItemService.auditWageItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/wg/resAuditWageItem.action")
	@ResponseBody
	public Map<String, Object> resAuditWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		WageItemService.resAuditWageItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
