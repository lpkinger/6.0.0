package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.CategoryBaseService;

@Controller
public class CategoryBaseControlller extends BaseController {
	@Autowired
	private CategoryBaseService categoryBaseService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/ars/saveCategoryBase.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.saveCategoryBase(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteCategoryBase.action")
	@ResponseBody
	public Map<String, Object> deleteCategoryBase(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.deleteCategoryBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/updateCategoryBase.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.updateCategoryBaseById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitCategoryBase.action")
	@ResponseBody
	public Map<String, Object> submitVoucher(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.submitCategory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitCategoryBase.action")
	@ResponseBody
	public Map<String, Object> resSubmitVoucher(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.resSubmitCategory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditCategoryBase.action")
	@ResponseBody
	public Map<String, Object> auditVoucher(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.auditCategory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditCategoryBase.action")
	@ResponseBody
	public Map<String, Object> resAuditVoucher(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.resAuditCategory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用
	 */
	@RequestMapping("/fa/ars/bannedCategoryBase.action")
	@ResponseBody
	public Map<String, Object> bannedCurrencys(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.bannedCategory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/fa/ars/resBannedCategoryBase.action")
	@ResponseBody
	public Map<String, Object> resBannedCurrencys(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryBaseService.resBannedCategory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取默认币别
	 */
	@RequestMapping("/fa/ars/getDefaultCurrency.action")
	@ResponseBody
	public Map<String, Object> getDefaultCurrency() {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("defaultCurrency", categoryBaseService.getDefaultCurrency());
		modelMap.put("success", true);
		return modelMap;
	}
}
