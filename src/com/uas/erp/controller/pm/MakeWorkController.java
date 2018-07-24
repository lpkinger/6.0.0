package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeWorkService;

@Controller
public class MakeWorkController extends BaseController {
	@Autowired
	private MakeWorkService makeWorkService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeWork.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeWorkService.saveMakeWork(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeWork.action")
	@ResponseBody
	public Map<String, Object> deleteMakeWork(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeWorkService.deleteMakeWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeWork.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeWorkService.updateMakeWorkById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeWork.action")
	@ResponseBody
	public Map<String, Object> submitMakeWork(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeWorkService.submitMakeWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeWork.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeWork(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeWorkService.resSubmitMakeWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeWork.action")
	@ResponseBody
	public Map<String, Object> auditMakeWork(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeWorkService.auditMakeWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeWork.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeWork(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeWorkService.resAuditMakeWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
