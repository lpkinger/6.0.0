package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.PreProductBOMService;

@Controller
public class PreProductBOMController extends BaseController {
	@Autowired
	private PreProductBOMService preProductBOMService;

	/**
	 * 保存BOMChange
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/bom/savePreProductBOM.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
		String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBOMService.savePreProductBOM(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/bom/deletePreProductBOM.action")
	@ResponseBody
	public Map<String, Object> deletePreProductBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBOMService.deletePreProductBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/bom/updatePreProductBOM.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBOMService.updatePreProductBOMById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交作业资料
	 */
	@RequestMapping("/pm/bom/submitPreProductBOM.action")
	@ResponseBody
	public Map<String, Object> submitPreProductBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBOMService.submitPreProductBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交作业资料
	 */
	@RequestMapping("/pm/bom/resSubmitPreProductBOM.action")
	@ResponseBody
	public Map<String, Object> resSubmitPreProductBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBOMService.resSubmitPreProductBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核作业资料
	 */
	@RequestMapping("/pm/bom/auditPreProductBOM.action")
	@ResponseBody
	public Map<String, Object> auditPreProductBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBOMService.auditPreProductBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核作业资料
	 */
	@RequestMapping("/pm/bom/resAuditPreProductBOM.action")
	@ResponseBody
	public Map<String, Object> resAuditPreProductBOM(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProductBOMService.resAuditPreProductBOM(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
