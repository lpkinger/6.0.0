package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.PhaseChangeService;



@Controller
public class PhaseChangeController {
	@Autowired
	private PhaseChangeService phaseChangeService;
	
	/*
	 * 保存
	 */
	@RequestMapping(value = "/plm/request/savePhaseChange.action")
	@ResponseBody
	public Map<String,Object> savePhaseChange(String caller,String formStore,String param) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		phaseChangeService.savePhaseChange(caller,formStore,param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 更新
	 */
	@RequestMapping(value = "/plm/request/updatePhaseChange.action")
	@ResponseBody
	public Map<String,Object> updatePhaseChange(String caller,String formStore,String param) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		phaseChangeService.updatePhaseChange(caller,formStore,param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/plm/request/deletePhaseChange.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		phaseChangeService.deletePhaseChange(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/plm/request/submitPhaseChange.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		phaseChangeService.submitPhaseChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/request/resSubmitPhaseChange.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		phaseChangeService.resSubmitPhaseChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/plm/request/auditPhaseChange.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		phaseChangeService.auditPhaseChange(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 加载未完成的阶段
	 */
	 @RequestMapping("plm/request/loadPhase.action")
	 @ResponseBody
	 public Map<String,Object> loadPhase(String prj_code){
		 Map<String,Object> modelMap=new HashMap<String,Object>();
		 List<Map<String, Object>> phases = phaseChangeService.loadPhase(prj_code);
		 modelMap.put("data", phases);
		 modelMap.put("success",true);
		 return modelMap;
	 }
}
