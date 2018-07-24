package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeNoticeService;

@Controller
public class MakeNoticeController extends BaseController {
	@Autowired
	private MakeNoticeService makeNoticeService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeNotice.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeNoticeService.saveMakeNotice(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeNotice.action")
	@ResponseBody
	public Map<String, Object> deleteMakeNotice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeNoticeService.deleteMakeNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeNotice.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeNoticeService.updateMakeNoticeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeNotice.action")
	@ResponseBody
	public Map<String, Object> submitMakeNotice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeNoticeService.submitMakeNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeNotice.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeNotice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeNoticeService.resSubmitMakeNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeNotice.action")
	@ResponseBody
	public Map<String, Object> auditMakeNotice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeNoticeService.auditMakeNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeNotice.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeNotice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeNoticeService.resAuditMakeNotice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
