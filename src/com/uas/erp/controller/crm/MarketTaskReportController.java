package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.MarketTaskReportService;

@Controller
public class MarketTaskReportController {
	@Autowired
	private MarketTaskReportService marketTaskReportService;
	@Autowired
	private BaseDao baseDao;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveMarketTaskReport.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskReportService.saveMarketTaskReport(formStore, caller, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/crm/marketmgr/deleteMarketTaskReport.action")
	@ResponseBody
	public Map<String, Object> deleteMarketTaskReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskReportService.deleteMarketTaskReport(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateMarketTaskReport.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskReportService
				.updateMarketTaskReport(formStore, caller, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitMarketTaskReport.action")
	@ResponseBody
	public Map<String, Object> submitMarketTaskReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskReportService.submitMarketTaskReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitMarketTaskReport.action")
	@ResponseBody
	public Map<String, Object> resSubmitMarketTaskReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskReportService.resSubmitMarketTaskReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditMarketTaskReport.action")
	@ResponseBody
	public Map<String, Object> auditMarketTaskReport(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskReportService.auditMarketTaskReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditMarketTaskReport.action")
	@ResponseBody
	public Map<String, Object> resAuditMarketTaskReport(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketTaskReportService.resAuditMarketTaskReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取模板编号
	 */
	@RequestMapping("/crm/getReportCodeById.action")
	@ResponseBody
	public Map<String, Object> getReportCodeById(int mr_id) {
		Object mr_reportcode = baseDao.getFieldDataByCondition(
				"MarketTaskReport", "mr_reportcode", "mr_id=" + mr_id);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("mr_reportcode", mr_reportcode);
		modelMap.put("success", true);
		return modelMap;
	}
}
