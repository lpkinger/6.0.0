package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KpidesigngradeService;;;

@Controller
public class KpidesigngradeController {
	@Autowired
	private KpidesigngradeService KpidesigngradeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/Kpidesigngrade/saveKpidesigngrade.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesigngradeService.saveKpidesigngrade(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/Kpidesigngrade/deleteKpidesigngrade.action")  
	@ResponseBody 
	public Map<String, Object> deleteKpidesigngrade(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesigngradeService.deleteKpidesigngrade(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/Kpidesigngrade/updateKpidesigngrade.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		KpidesigngradeService.updateKpidesigngrade(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
