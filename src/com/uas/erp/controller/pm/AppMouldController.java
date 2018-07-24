package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.AppMouldService;

@Controller
public class AppMouldController extends BaseController {
	@Autowired
	private AppMouldService appMouldService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/saveAppMould.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.saveAppMould(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deleteAppMould.action")
	@ResponseBody
	public Map<String, Object> deleteAppMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.deleteAppMould(id, caller);
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
	@RequestMapping("/pm/mould/updateAppMould.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.updateAppMouldById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printAppMould.action")
	@ResponseBody
	public Map<String, Object> printAppMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.printAppMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitAppMould.action")
	@ResponseBody
	public Map<String, Object> submitAppMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.submitAppMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitAppMould.action")
	@ResponseBody
	public Map<String, Object> resSubmitAppMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.resSubmitAppMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditAppMould.action")
	@ResponseBody
	public Map<String, Object> auditAppMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.auditAppMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditAppMould.action")
	@ResponseBody
	public Map<String, Object> resAuditAppMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.resAuditAppMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转报价单
	 */
	@RequestMapping("/pm/mould/turnPriceMould.action")
	@ResponseBody
	public Map<String, Object> turnPriceMould(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", appMouldService.turnPriceMould(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转模具销售单
	 */
	@RequestMapping("/pm/mould/turnMouldSale.action")
	@ResponseBody
	public Map<String, Object> turnMouldSale(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", appMouldService.turnMouldSale(id));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转其它应收单
	 */
	@RequestMapping("/pm/mould/turnOtherBill.action")
	@ResponseBody
	public Map<String, Object> turnOtherBill(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.createARBill(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新是否报价
	 * */
	@RequestMapping("/pm/mould/updateIsOffer.action")
	@ResponseBody
	public Map<String, Object> updateReturnqty(HttpSession session, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		appMouldService.updateOffer(data);
		modelMap.put("success", true);
		return modelMap;
	}
}
