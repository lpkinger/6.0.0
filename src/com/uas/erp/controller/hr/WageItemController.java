package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.hr.WageItemService;


@Controller
public class WageItemController extends BaseController {
	@Autowired
	private WageItemService wageItemService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/hr/wage/saveWageItem.action")
	@ResponseBody
	public Map<String, Object> saveWageItem(HttpSession session,
			String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageItemService.saveWageItem(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/hr/wage/deleteWageItem.action")
	@ResponseBody
	public Map<String, Object> deleteWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageItemService.deleteWageItem(id, caller);
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
	@RequestMapping("/hr/wage/updateWageItem.action")
	@ResponseBody
	public Map<String, Object> updateWageItem(HttpSession session,
			String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageItemService.updateWageItemById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/wage/submitWageItem.action")
	@ResponseBody
	public Map<String, Object> submitWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageItemService.submitWageItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/wage/resSubmitWageItem.action")
	@ResponseBody
	public Map<String, Object> resSubmitWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageItemService.resSubmitWageItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/wage/auditWageItem.action")
	@ResponseBody
	public Map<String, Object> auditWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageItemService.auditWageItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/wage/resAuditWageItem.action")
	@ResponseBody
	public Map<String, Object> resAuditWageItem(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageItemService.resAuditWageItem(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/hr/wage/getWageItems.action")
	@ResponseBody
	public Map<String, Object> getWageItems() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", wageItemService.getWageItems());
		modelMap.put("success", true);
		return modelMap;
	}
}
