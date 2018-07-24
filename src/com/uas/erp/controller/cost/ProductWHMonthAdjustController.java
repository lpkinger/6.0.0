package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.cost.ProductWHMonthAdjustService;

@Controller
public class ProductWHMonthAdjustController extends BaseController {
	@Autowired
	private ProductWHMonthAdjustService productWHMonthAdjustService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/co/inventory/saveProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.saveProductWHMonthAdjust(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/co/inventory/deleteProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> deleteProductWHMonthAdjust(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.deleteProductWHMonthAdjust(id);
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
	@RequestMapping("/co/inventory/updateProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.updateProductWHMonthAdjustById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/co/inventory/printProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> printProductWHMonthAdjust(HttpSession session, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.printProductWHMonthAdjust(id, reportName, condition);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/co/inventory/submitProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> submitProductWHMonthAdjust(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.submitProductWHMonthAdjust(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/co/inventory/resSubmitProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> resSubmitProductWHMonthAdjust(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.resSubmitProductWHMonthAdjust(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/co/inventory/auditProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> auditProductWHMonthAdjust(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.auditProductWHMonthAdjust(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/co/inventory/resAuditProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> resAuditProductWHMonthAdjust(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.resAuditProductWHMonthAdjust(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/co/inventory/postProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> postProductWHMonthAdjust(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.postProductWHMonthAdjust(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/co/inventory/resPostProductWHMonthAdjust.action")
	@ResponseBody
	public Map<String, Object> resPostProductWHMonthAdjust(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productWHMonthAdjustService.resPostProductWHMonthAdjust(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
