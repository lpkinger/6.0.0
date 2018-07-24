package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.RationService;

@Controller
public class RationController extends BaseController {

	@Autowired
	private RationService rationService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveRation.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		rationService.updateRation(formStore, param, caller);
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
	@RequestMapping("/pm/make/updateRation.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		rationService.updateRation(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/deleteRation.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		rationService.deleteRation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/submitRation.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		rationService.submitRation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/resSubmitRation.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		rationService.resSubmitRation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/auditRation.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		rationService.auditRation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/resAuditRation.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		rationService.resAuditRation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
