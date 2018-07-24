package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeMaterialDeleteService;

@Controller
public class MakeMaterialDeleteController extends BaseController {
	@Autowired
	private MakeMaterialDeleteService makeMaterialDeleteService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeMaterialDelete.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialDeleteService.saveMakeMaterialDelete(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeMaterialDelete.action")
	@ResponseBody
	public Map<String, Object> deleteMakeMaterialDelete(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialDeleteService.deleteMakeMaterialDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeMaterialDelete.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialDeleteService.updateMakeMaterialDeleteById(formStore,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitMakeMaterialDelete.action")
	@ResponseBody
	public Map<String, Object> submitMakeMaterialDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialDeleteService.submitMakeMaterialDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitMakeMaterialDelete.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeMaterialDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialDeleteService.resSubmitMakeMaterialDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditMakeMaterialDelete.action")
	@ResponseBody
	public Map<String, Object> auditMakeMaterialDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialDeleteService.auditMakeMaterialDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditMakeMaterialDelete.action")
	@ResponseBody
	public Map<String, Object> resAuditMakeMaterialDelete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMaterialDeleteService.resAuditMakeMaterialDelete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
