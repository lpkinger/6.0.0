package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeDeleteService;

@Controller
public class MakeDeleteController extends BaseController {
	@Autowired
	private MakeDeleteService makeDeleteService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeDelete.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeDeleteService.saveMakeDelete(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeDelete.action")
	@ResponseBody
	public Map<String, Object> deleteMakeDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeDeleteService.deleteMakeDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeDelete.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeDeleteService.updateMakeDeleteById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeDelete.action")
	@ResponseBody
	public Map<String, Object> submitMakeDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeDeleteService.submitMakeDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeDelete.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeDeleteService.resSubmitMakeDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeDelete.action")
	@ResponseBody
	public Map<String, Object> auditMakeDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeDeleteService.auditMakeDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeDelete.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeDeleteService.resAuditMakeDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
