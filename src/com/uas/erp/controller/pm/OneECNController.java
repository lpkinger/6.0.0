package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.OneECNService;

@Controller
public class OneECNController extends BaseController {
	@Autowired
	private OneECNService oneECNService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveOneECN.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oneECNService.saveOneECN(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/pm/bom/deleteOneECN.action")
	@ResponseBody
	public Map<String, Object> deleteOneECN(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oneECNService.deleteOneECN(id, caller);
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
	@RequestMapping("/pm/bom/updateOneECN.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oneECNService.updateOneECNById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交ECN
	 */
	@RequestMapping("/pm/bom/submitOneECN.action")
	@ResponseBody
	public Map<String, Object> submitOneECN(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oneECNService.submitOneECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交ECN
	 */
	@RequestMapping("/pm/bom/resSubmitOneECN.action")
	@ResponseBody
	public Map<String, Object> resSubmitOneECN(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oneECNService.resSubmitOneECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核ECN
	 */
	@RequestMapping("/pm/bom/auditOneECN.action")
	@ResponseBody
	public Map<String, Object> auditOneECN(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oneECNService.auditOneECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核ECN
	 */
	@RequestMapping("/pm/bom/resAuditOneECN.action")
	@ResponseBody
	public Map<String, Object> resAuditOneECN(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		oneECNService.resAuditOneECN(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
