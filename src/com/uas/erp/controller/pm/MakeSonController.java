package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeSonService;

@Controller
public class MakeSonController extends BaseController {
	@Autowired
	private MakeSonService makeSonService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeSon.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSonService.saveMakeSon(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeSon.action")
	@ResponseBody
	public Map<String, Object> deleteMakeSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSonService.deleteMakeSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeSon.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSonService.updateMakeSonById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeSon.action")
	@ResponseBody
	public Map<String, Object> submitMakeSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSonService.submitMakeSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeSon.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSonService.resSubmitMakeSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeSon.action")
	@ResponseBody
	public Map<String, Object> auditMakeSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSonService.auditMakeSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeSon.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeSon(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeSonService.resAuditMakeSon(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
