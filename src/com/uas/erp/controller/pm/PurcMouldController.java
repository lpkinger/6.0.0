package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.PurMouldService;

@Controller
public class PurcMouldController extends BaseController {
	@Autowired
	private PurMouldService purMouldService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/savePurcMould.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.savePurcMould(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deletePurcMould.action")
	@ResponseBody
	public Map<String, Object> deletePurcMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.deletePurcMould(id, caller);
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
	@RequestMapping("/pm/mould/updatePurcMould.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.updatePurcMouldById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printPurcMould.action")
	@ResponseBody
	public Map<String, Object> printPurcMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.printPurcMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitPurcMould.action")
	@ResponseBody
	public Map<String, Object> submitPurcMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.submitPurcMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitPurcMould.action")
	@ResponseBody
	public Map<String, Object> resSubmitPurcMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.resSubmitPurcMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditPurcMould.action")
	@ResponseBody
	public Map<String, Object> auditPurcMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.auditPurcMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditPurcMould.action")
	@ResponseBody
	public Map<String, Object> resAuditPurcMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.resAuditPurcMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转模具采购验收报告
	 */
	@RequestMapping("/pm/mould/turnYSReport.action")
	@ResponseBody
	public Map<String, Object> turnYSReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", purMouldService.turnYSReport(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新付款状态
	 */
	@RequestMapping("/pm/mould/purmould/updatepaystatus.action")
	@ResponseBody
	public Map<String, Object> updatepaystatus(HttpSession session, int id, String returnstatus, String returnremark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.updatepaystatus(id, returnstatus, returnremark);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转模具付款申请单
	 */
	@RequestMapping("/pm/mould/purcTurnFeePlease.action")
	@ResponseBody
	public Map<String, Object> turnFeePlease(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", purMouldService.turnFeePlease(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/savePurcMouldDet.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.savePurcMould(formStore, param, param2, caller);
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
	@RequestMapping("/pm/mould/updatePurcMouldDet.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purMouldService.updatePurcMouldById(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
