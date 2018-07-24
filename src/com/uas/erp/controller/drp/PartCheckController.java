package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.drp.PartCheckService;

@Controller
public class PartCheckController {
	@Autowired
	private PartCheckService partCheckService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("drp/aftersale/savePartCheck.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		partCheckService.savePartCheck(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deletePartCheck.action")
	@ResponseBody
	public Map<String, Object> deleteAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		partCheckService.deletePartCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/drp/aftersale/updatePartCheck.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		partCheckService.updatePartCheckById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitPartCheck.action")
	@ResponseBody
	public Map<String, Object> submitAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		partCheckService.submitPartCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitPartCheck.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		partCheckService.resSubmitPartCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditPartCheck.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		partCheckService.auditPartCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditPartCheck.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		partCheckService.resAuditPartCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转其它入库 分检信息单批量转其它入库
	 */
	@RequestMapping(value = "/drp/vastTurnOtherIn.action")
	@ResponseBody
	public Map<String, Object> vastTurnOtherIn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", partCheckService.batchTurnOtherIn(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量转其它入库 分检信息单批量转退货
	 */
	@RequestMapping(value = "/drp/vastTurnSaleReturn.action")
	@ResponseBody
	public Map<String, Object> vastTurnSaleReturn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", partCheckService.bathcTurnSaleReturn(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认申请
	 */
	@RequestMapping("/drp/confirmPartCheck.action")
	@ResponseBody
	public Map<String, Object> confirm(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		partCheckService.confirmPartCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
