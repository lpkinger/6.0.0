package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.FeederService;

@Controller
public class FeederController {

	@Autowired
	private FeederService feederService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveFeeder.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.saveFeeder(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteFeeder.action")
	@ResponseBody
	public Map<String, Object> deleteFeederio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	feederService.deleteFeeder(id, caller);
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
	@RequestMapping("/pm/mes/updateFeeder.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.updateFeederById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitFeeder.action")
	@ResponseBody
	public Map<String, Object> submitFeederio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.submitFeeder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitFeeder.action")
	@ResponseBody
	public Map<String, Object> resSubmitFeederio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.resSubmitFeeder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditFeeder.action")
	@ResponseBody
	public Map<String, Object> auditFeederio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.auditFeeder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditFeeder.action")
	@ResponseBody
	public Map<String, Object> resAuditFeederio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.resAuditFeeder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	  
	/**
	 * 保存飞达维修登记
	 * @param caller
	 * @param fe_id
	 * @param remark
	 * @param ifclear
	 * @return
	 */
	@RequestMapping("/pm/mes/saveFeederRepairLog.action")
	@ResponseBody
	public Map<String, Object> saveFeederRepairLog(String caller, int fe_id,String remark,boolean ifclear) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.saveFeederRepairLog(caller,fe_id,remark,ifclear);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存飞达报废记录
	 * @param caller
	 * @param fe_id
	 * @param remark
	 * @return
	 */
	@RequestMapping("/pm/mes/saveFeederScrapLog.action")
	@ResponseBody
	public Map<String, Object> saveFeederScrapLog(String caller, int fe_id,String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.saveFeederScrapLog(caller,fe_id,remark);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 飞达批量转保养
	 * @param caller
	 * @param data
	 * @return
	 */
	@RequestMapping("/pm/mes/vastTurnMaintain.action")
	@ResponseBody
	public Map<String, Object> vastTurnMaintain(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederService.vastTurnMaintain(caller,data);
		modelMap.put("success", true);
		return modelMap;
	}
}
