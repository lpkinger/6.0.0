package com.uas.erp.controller.scm;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SaleForeCastChangeService;

@Controller
public class SaleForeCastChangeController extends BaseController {
	@Autowired
	private SaleForeCastChangeService saleForeCastChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/sale/saveSaleForeCastChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForeCastChangeService.saveSaleForeCastChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/sale/deleteSaleForeCastChange.action")
	@ResponseBody
	public Map<String, Object> deleteSaleForeCastChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForeCastChangeService.deleteSaleForeCastChange(id, caller);
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
	@RequestMapping("/scm/sale/updateSaleForeCastChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForeCastChangeService.updateSaleForeCastChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printSaleForeCastChange.action")
	@ResponseBody
	public Map<String, Object> printSaleForeCastChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForeCastChangeService.printSaleForeCastChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSaleForeCastChange.action")
	@ResponseBody
	public Map<String, Object> submitSaleForeCastChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForeCastChangeService.submitSaleForeCastChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSaleForeCastChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitSaleForeCastChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForeCastChangeService.resSubmitSaleForeCastChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 * 
	 * @throws UnknownHostException
	 */
	@RequestMapping("/scm/sale/auditSaleForeCastChange.action")
	@ResponseBody
	public ModelMap auditSaleForeCastChange(String caller, int id) {
		saleForeCastChangeService.auditSaleForeCastChange(id, caller);
		return success();
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSaleForeCastChange.action")
	@ResponseBody
	public Map<String, Object> resAuditSaleForeCastChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForeCastChangeService.resAuditSaleForeCastChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
