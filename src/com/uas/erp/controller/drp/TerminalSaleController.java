package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.TerminalSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TerminalSaleController {

	@Autowired
	private TerminalSaleService terminalSaleService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveTerminalSale.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalSaleService.saveTerminalSale(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateTerminalSale.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalSaleService.updateTerminalSaleById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteTerminalSale.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalSaleService.deleteTerminalSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitTerminalSale.action")
	@ResponseBody
	public Map<String, Object> submitTerminalSale(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalSaleService.submitTerminalSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitTerminalSale.action")
	@ResponseBody
	public Map<String, Object> resSubmitTerminalSale(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalSaleService.resSubmitTerminalSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditTerminalSale.action")
	@ResponseBody
	public Map<String, Object> auditTerminalSale(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalSaleService.auditTerminalSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditTerminalSale.action")
	@ResponseBody
	public Map<String, Object> resAuditTerminalSale(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalSaleService.resAuditTerminalSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
