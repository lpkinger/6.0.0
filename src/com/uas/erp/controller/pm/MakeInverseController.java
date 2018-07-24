package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeInverseService;

@Controller
public class MakeInverseController extends BaseController {
	@Autowired
	private MakeInverseService makeInverseService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeInverse.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeInverseService.saveMakeInverse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeInverse.action")
	@ResponseBody
	public Map<String, Object> deleteMakeInverse(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeInverseService.deleteMakeInverse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeInverse.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeInverseService.updateMakeInverseById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeInverse.action")
	@ResponseBody
	public Map<String, Object> submitMakeInverse(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeInverseService.submitMakeInverse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeInverse.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeInverse(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeInverseService.resSubmitMakeInverse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeInverse.action")
	@ResponseBody
	public Map<String, Object> auditMakeInverse(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeInverseService.auditMakeInverse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeInverse.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeInverse(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeInverseService.resAuditMakeInverse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
