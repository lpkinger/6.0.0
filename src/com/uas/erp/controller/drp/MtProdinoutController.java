package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.MtProdinoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MtProdinoutController {

	@Autowired
	private MtProdinoutService mtProdinoutService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveMtProdinout.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		mtProdinoutService.saveMtProdinout(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateMtProdinout.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		mtProdinoutService.updateMtProdinoutById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteMtProdinout.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		mtProdinoutService.deleteMtProdinout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitMtProdinout.action")
	@ResponseBody
	public Map<String, Object> submitMtProdinout(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		mtProdinoutService.submitMtProdinout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitMtProdinout.action")
	@ResponseBody
	public Map<String, Object> resSubmitMtProdinout(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		mtProdinoutService.resSubmitMtProdinout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditMtProdinout.action")
	@ResponseBody
	public Map<String, Object> auditMtProdinout(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		mtProdinoutService.auditMtProdinout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditMtProdinout.action")
	@ResponseBody
	public Map<String, Object> resAuditMtProdinout(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		mtProdinoutService.resAuditMtProdinout(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 维修入库转出库
	 */
	@RequestMapping(value = "/drp/aftersale/mainTainInToOut.action")
	@ResponseBody
	public Map<String, Object> maintainInToOut(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = mtProdinoutService.maintainInToOut(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
