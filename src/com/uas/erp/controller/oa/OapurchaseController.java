package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.OapurchaseService;

@Controller
public class OapurchaseController {

	@Autowired
	private OapurchaseService oapurchaseService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/appliance/saveOapurchase.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.saveOapurchase(formStore, param,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/appliance/updateOapurchase.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.updateOapurchaseById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/appliance/deleteOapurchase.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.deleteOapurchase(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/appliance/submitOapurchase.action")
	@ResponseBody
	public Map<String, Object> submitOapurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.submitOapurchase(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/appliance/resSubmitOapurchase.action")
	@ResponseBody
	public Map<String, Object> resSubmitOapurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.resSubmitOapurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/appliance/auditOapurchase.action")
	@ResponseBody
	public Map<String, Object> auditOapurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.auditOapurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/appliance/resAuditOapurchase.action")
	@ResponseBody
	public Map<String, Object> resAuditOapurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.resAuditOapurchase(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/appliance/turnOaacceptance.action")
	@ResponseBody
	public Map<String, Object> turnOaacceptance(String caller, String formdata,
			String griddata) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.turnOaacceptance(formdata, griddata, caller);
		modelMap.put("success", true);
		return modelMap;

	}

	/**
	 * 批量转收料单
	 */
	@RequestMapping(value = "/oa/appliance/vastTurnAccept.action")
	@ResponseBody
	public Map<String, Object> vastTurnAccept(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = oapurchaseService.beatchturnOaacceptance( caller,data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/appliance/printOapurchase.action")
	@ResponseBody
	public Map<String, Object> printOaPurchase(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = oapurchaseService.printoaPurchase(id,  caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	
	/**
	 * 结案
	 */
	@RequestMapping("/oa/appliance/endOapurchase.action")
	@ResponseBody
	public Map<String, Object> endOapurchase(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.endOapurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反结案
	 */
	@RequestMapping("/oa/appliance/resEndOapurchase.action")
	@ResponseBody
	public Map<String, Object> resEndOapurchase(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseService.resEndOapurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
