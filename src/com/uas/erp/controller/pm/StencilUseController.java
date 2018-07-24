package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.StencilUseService;

@Controller
public class StencilUseController {

	@Autowired
	private StencilUseService stencilUseService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据	 
	 */
	@RequestMapping("/pm/mes/saveStencilUse.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stencilUseService.saveStencilUse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteStencilUse.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	stencilUseService.deleteStencilUse(id, caller);
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
	@RequestMapping("/pm/mes/updateStencilUse.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stencilUseService.updateStencilUse(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitStencilUse.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stencilUseService.submitStencilUse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitStencilUse.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stencilUseService.resSubmitStencilUse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditStencilUse.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stencilUseService.auditStencilUse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditStencilUse.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stencilUseService.resAuditStencilUse(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/pm/mes/backStencil.action")
	@ResponseBody
	public Map<String, Object> back(String caller, int id,String record,String location,String date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stencilUseService.backStencil(id,caller,record,location,date);
		modelMap.put("success", true);
		return modelMap;
	}

}
