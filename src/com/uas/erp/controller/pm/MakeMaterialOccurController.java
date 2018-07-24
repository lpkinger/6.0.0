package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeMaterialOccurService;

@Controller
public class MakeMaterialOccurController extends BaseController {
	@Autowired
	private MakeMaterialOccurService makeMaterialOccurService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeMaterialOccur.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialOccurService.saveMakeMaterialOccur(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeMaterialOccur.action")
	@ResponseBody
	public Map<String, Object> deleteMakeMaterialOccur(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialOccurService.deleteMakeMaterialOccur(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeMaterialOccur.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialOccurService.updateMakeMaterialOccurById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeMaterialOccur.action")
	@ResponseBody
	public Map<String, Object> submitMakeMaterialOccur(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialOccurService.submitMakeMaterialOccur(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeMaterialOccur.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeMaterialOccur(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialOccurService.resSubmitMakeMaterialOccur(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeMaterialOccur.action")
	@ResponseBody
	public Map<String, Object> auditMakeMaterialOccur(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialOccurService.auditMakeMaterialOccur(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeMaterialOccur.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeMaterialOccur(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialOccurService.resAuditMakeMaterialOccur(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
