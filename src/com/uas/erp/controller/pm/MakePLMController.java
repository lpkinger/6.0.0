package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakePLMService;

@Controller
public class MakePLMController extends BaseController {
	@Autowired
	private MakePLMService makePLMService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/plm/saveMake.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.saveMakeBase(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/plm/deleteMake.action")
	@ResponseBody
	public Map<String, Object> deleteMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.deleteMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/plm/updateMake.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.updateMakeBaseById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/plm/submitMake.action")
	@ResponseBody
	public Map<String, Object> submitMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.submitMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/plm/resSubmitMake.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.resSubmitMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/plm/auditMake.action")
	@ResponseBody
	public Map<String, Object> auditMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.auditMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/plm/resAuditMake.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.resAuditMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批准
	 */
	@RequestMapping("/pm/plm/checkMake.action")
	@ResponseBody
	public Map<String, Object> aproveMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.approveMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反批准
	 */
	@RequestMapping("/pm/plm/resCheckMake.action")
	@ResponseBody
	public Map<String, Object> resAproveMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.resApproveMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/pm/plm/endMake.action")
	@ResponseBody
	public Map<String, Object> endMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.endMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/pm/plm/resEndMake.action")
	@ResponseBody
	public Map<String, Object> resEndMakeBase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makePLMService.resEndMakeBase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
