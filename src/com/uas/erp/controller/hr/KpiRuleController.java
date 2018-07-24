package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KpiRuleService;;;

@Controller
public class KpiRuleController {
	@Autowired
	private KpiRuleService KpiRuleService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/KpiRule/saveKpiRule.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpiRuleService.saveKpiRule(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/KpiRule/deleteKpiRule.action")  
	@ResponseBody 
	public Map<String, Object> deleteKpiRule(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpiRuleService.deleteKpiRule(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/KpiRule/updateKpiRule.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpiRuleService.updateKpiRule(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @param sql 待测试sql语句
	 */
	@RequestMapping("/hr/KpiRule/testSQL.action")  
	@ResponseBody 
	public Map<String, Object> testSQL(String sql,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpiRuleService.testSQL(sql,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
