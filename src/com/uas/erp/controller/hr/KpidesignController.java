package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KpidesignService;;;

@Controller
public class KpidesignController {
	@Autowired
	private KpidesignService KpidesignService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据       
	 */
	@RequestMapping("hr/Kpi/saveKpidesign.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.saveKpidesign(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("hr/Kpi/deleteKpidesign.action")  
	@ResponseBody 
	public Map<String, Object> deleteKpiRule(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.deleteKpidesign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("hr/Kpi/updateKpidesign.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.updateKpidesign(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("hr/kpi/submitKpidesign.action")
	@ResponseBody
	public Map<String, Object> submitKpidesign(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.submitKpidesign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("hr/kpi/resSubmitKpidesign.action")
	@ResponseBody
	public Map<String, Object> resSubmitKpidesign(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.resSubmitKpidesign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("hr/kpi/auditKpidesign.action")
	@ResponseBody
	public Map<String, Object> auditKpidesign(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.auditKpidesign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("hr/kpi/resAuditKpidesign.action")
	@ResponseBody
	public Map<String, Object> resAuditKpidesign(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.resAuditKpidesign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 保存kpi明细
	 */
	@RequestMapping("hr/kpi/saveDetail.action")
	@ResponseBody
	public Map<String, Object> saveDetail(String caller,  String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", KpidesignService.saveDetail(caller, formStore,param));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改明细
	 */
	@RequestMapping("hr/kpi/updateDetail.action")
	@ResponseBody
	public Map<String, Object> updateDetail(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.updateDetail(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除明细
	 */
	@RequestMapping("hr/kpi/deleteDetail.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesignService.deleteDetail(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 发起考核
	 */
	@RequestMapping("hr/kpi/vastKpidesignLaunch.action")  
	@ResponseBody 
	public Map<String, Object> kpidesignLaunch(String caller, String data,String time_from,String time_to,String period){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = KpidesignService.kpidesignLaunch(caller, data,time_from,time_to,period);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 成绩汇总
	 */
	@RequestMapping("hr/kpi/vastKpiSummary.action")  
	@ResponseBody 
	public Map<String, Object> kpidSummary(String caller, String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = KpidesignService.kpidSummary(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
