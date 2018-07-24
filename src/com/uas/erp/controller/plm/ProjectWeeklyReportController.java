package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectWeeklyReportService;

@Controller
public class ProjectWeeklyReportController extends BaseController{
	
	@Autowired
	private ProjectWeeklyReportService projectWeeklyReportService;
	
	/**
	 * 自动抓取本周的项目
	 */
	@RequestMapping("/plm/task/autoGetGridData.action")
	@ResponseBody
	public Map<String, Object> autoGetGridData(String man,String prjcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",projectWeeklyReportService.autoGetGridData(man,prjcode));
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
	@RequestMapping("/plm/task/savePrjWkReport.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectWeeklyReportService.savePrjWkReport(formStore, param, caller);
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
	@RequestMapping("/plm/task/updatePrjWkReport.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectWeeklyReportService.updatePrjWkReport(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除项目周报数据 包括采购明细
	 */
	@RequestMapping("/plm/task/deletePrjWkReport.action")
	@ResponseBody
	public Map<String, Object> deletePrjWkReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectWeeklyReportService.deletePrjWkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交项目周报
	 */
	@RequestMapping("/plm/task/submitPrjWkReport.action")
	@ResponseBody
	public Map<String, Object> submitPrjWkReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectWeeklyReportService.submitPrjWkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交项目周报
	 */
	@RequestMapping("/plm/task/resSubmitPrjWkReport.action")
	@ResponseBody
	public Map<String, Object> resSubmitPrjWkReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectWeeklyReportService.resSubmitPrjWkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核项目周报
	 */
	@RequestMapping("/plm/task/auditPrjWkReport.action")
	@ResponseBody
	public Map<String, Object> auditPrjWkReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectWeeklyReportService.auditPrjWkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核项目周报
	 */
	@RequestMapping("/plm/task/resAuditPrjWkReport.action")
	@ResponseBody
	public Map<String, Object> resAuditPrjWkReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectWeeklyReportService.resAuditPrjWkReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
