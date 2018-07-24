package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeStepChangeService;

@Controller
public class MakeStepChangeController extends BaseController {
	@Autowired
	private MakeStepChangeService makeStepChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveMakeStepChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeStepChangeService.saveMakeStepChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除makeSTEP数据 
	 */
	@RequestMapping("/pm/make/deleteMakeStepChange.action")
	@ResponseBody
	public Map<String, Object> deleteMakeStepChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeStepChangeService.deleteMakeStepChange(id, caller);
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
	@RequestMapping("/pm/make/updateMakeStepChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeStepChangeService.updateById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交makeStep
	 */
	@RequestMapping("/pm/make/submitMakeStepChange.action")
	@ResponseBody
	public Map<String, Object> submitMakeStepChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeStepChangeService.submitMakeStepChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交makeStep
	 */
	@RequestMapping("/pm/make/resSubmitMakeStepChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitMakeStepChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeStepChangeService.resSubmitMakeStepChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核makeStep
	 */
	@RequestMapping("/pm/make/auditMakeStepChange.action")
	@ResponseBody
	public Map<String, Object> auditMakeStepChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeStepChangeService.auditMakeStepChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转执行
	 * @param id 明细ID
	 */
	@RequestMapping("/pm/make/MakeStepChangeOpenDet.action")
	@ResponseBody
	public Map<String, Object> MakeStepChangeOpenDet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeStepChangeService.MakeStepChangeOpenDet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 取消执行
	 * @param　id 明细ID
	 */
	@RequestMapping("/pm/make/MakeStepChangeCloseDet.action")
	@ResponseBody
	public Map<String, Object> MakeStepChangeCloseDet(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeStepChangeService.MakeStepChangeCloseDet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
