package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.CraftService;

@Controller
public class CraftController extends BaseController {
	@Autowired
	private CraftService craftService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveCraft.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.saveCraft(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除工艺资料数据 包括工艺资料明细
	 */
	@RequestMapping("/pm/mes/deleteCraft.action")
	@ResponseBody
	public Map<String, Object> deleteCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.deleteCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("/pm/mes/deleteDetail.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.deleteDetail(id, caller);
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
	@RequestMapping("/pm/mes/updateCraft.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.updateCraftById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交工艺资料
	 */
	@RequestMapping("/pm/mes/submitCraft.action")
	@ResponseBody
	public Map<String, Object> submitCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.submitCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交工艺资料
	 */
	@RequestMapping("/pm/mes/resSubmitCraft.action")
	@ResponseBody
	public Map<String, Object> resSubmitCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.resSubmitCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核工艺资料
	 */
	@RequestMapping("/pm/mes/auditCraft.action")
	@ResponseBody
	public Map<String, Object> auditCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.auditCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核工艺资料
	 */
	@RequestMapping("/pm/mes/resAuditCraft.action")
	@ResponseBody
	public Map<String, Object> resAuditCraft(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.resAuditCraft(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 工艺采集信息维护
	 */
	@RequestMapping("/pm/mes/saveStepCollection.action")
	@ResponseBody
	public Map<String, Object> saveStepCollection(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.saveStepCollection(caller,formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 刷新工艺路线
	 */
	@RequestMapping("/pm/mes/refreshCrafts.action")
	@ResponseBody
	public Map<String, Object> refreshCrafts(String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		craftService.refreshCrafts(code);
		modelMap.put("success", true);
		return modelMap;
	}
}
