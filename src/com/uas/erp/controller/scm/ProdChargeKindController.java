package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProdChargeKindService;

@Controller
public class ProdChargeKindController {
	@Autowired
	private ProdChargeKindService prodChargeKindService;

	@RequestMapping("/scm/reserve/saveProdChargeKind.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChargeKindService.saveProdChargeKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/reserve/deleteProdChargeKind.action")
	@ResponseBody
	public Map<String, Object> deleteProdChargeKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChargeKindService.deleteProdChargeKind(id, caller);
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
	@RequestMapping("/scm/reserve/updateProdChargeKind.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChargeKindService.updateProdChargeKindById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/reserve/submitProdChargeKind.action")
	@ResponseBody
	public Map<String, Object> submitProdChargeKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChargeKindService.submitProdChargeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/reserve/resSubmitProdChargeKind.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdChargeKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChargeKindService.resSubmitProdChargeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/reserve/auditProdChargeKind.action")
	@ResponseBody
	public Map<String, Object> auditProdChargeKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChargeKindService.auditProdChargeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/reserve/resAuditProdChargeKind.action")
	@ResponseBody
	public Map<String, Object> resAuditProdChargeKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChargeKindService.resAuditProdChargeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
