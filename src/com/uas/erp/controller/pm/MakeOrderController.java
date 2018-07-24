package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeOrderService;

@Controller
public class MakeOrderController extends BaseController {
	@Autowired
	private MakeOrderService makeOrderService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeOrder.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeOrderService.saveMakeOrder(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeOrder.action")
	@ResponseBody
	public Map<String, Object> deleteMakeOrder(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeOrderService.deleteMakeOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeOrder.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeOrderService.updateMakeOrderById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeOrder.action")
	@ResponseBody
	public Map<String, Object> submitMakeOrder(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeOrderService.submitMakeOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeOrder.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeOrder(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeOrderService.resSubmitMakeOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeOrder.action")
	@ResponseBody
	public Map<String, Object> auditMakeOrder(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeOrderService.auditMakeOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeOrder.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeOrder(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeOrderService.resAuditMakeOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
