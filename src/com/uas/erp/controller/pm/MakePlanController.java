package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.MakePlanService;

@Controller
public class MakePlanController {
	@Autowired
	private MakePlanService MakePlanService;

	//保存数据
	@RequestMapping("/pm/make/makeplan/save.action")
	@ResponseBody
	public Map<String, Object> save(String caller,  String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();//创建一个集合，创建的那个map对象叫modelMap
		MakePlanService.save(caller, formStore, param);//save是在serviceImpl当中实现的方法
		modelMap.put("success", true);
		return modelMap;
	}

	//删除
	@RequestMapping("/pm/make/makeplan/delete.action")
	@ResponseBody
	public Map<String, Object> delete(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakePlanService.delete(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/makeplan/update.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakePlanService.update(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/makeplan/submit.action")
	@ResponseBody
	public Map<String, Object> submit(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakePlanService.submit(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/makeplan/resSubmit.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakePlanService.resSubmit(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/makeplan/audit.action")
	@ResponseBody
	public Map<String, Object> audit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakePlanService.audit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/makeplan/resAudit.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MakePlanService.resAudit(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}
