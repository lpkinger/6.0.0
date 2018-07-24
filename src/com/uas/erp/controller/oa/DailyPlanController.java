package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.oa.DailyPlanService;

@Controller
public class DailyPlanController extends BaseController {
	@Autowired
	private DailyPlanService DailyPlanService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/DailyPlan/saveDailyPlan.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.saveDailyPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除采购单数据 包括采购明细
	 */
	@RequestMapping("/oa/DailyPlan/deleteDailyPlan.action")
	@ResponseBody
	public Map<String, Object> deleteDailyPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.deleteDailyPlan(id, caller);
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
	@RequestMapping("/oa/DailyPlan/updateDailyPlan.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.updateDailyPlanById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印采购单
	 */
	@RequestMapping("/oa/DailyPlan/printDailyPlan.action")
	@ResponseBody
	public Map<String, Object> printDailyPlan(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = DailyPlanService.printDailyPlan(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交采购单
	 */
	@RequestMapping("/oa/DailyPlan/submitDailyPlan.action")
	@ResponseBody
	public Map<String, Object> submitDailyPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.submitDailyPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交采购单
	 */
	@RequestMapping("/oa/DailyPlan/resSubmitDailyPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitDailyPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.resSubmitDailyPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核采购单
	 */
	@RequestMapping("/oa/DailyPlan/auditDailyPlan.action")
	@ResponseBody
	public Map<String, Object> auditDailyPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.auditDailyPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核采购单
	 */
	@RequestMapping("/oa/DailyPlan/resAuditDailyPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditDailyPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.resAuditDailyPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/oa/DailyPlan/endDailyPlan.action")
	@ResponseBody
	public Map<String, Object> endDailyPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.endDailyPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/oa/DailyPlan/resEndDailyPlan.action")
	@ResponseBody
	public Map<String, Object> resEndDailyPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.resEndDailyPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/DailyPlan/getPrice.action")
	@ResponseBody
	public Map<String, Object> getPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.getPrice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/DailyPlan/getStandardPrice.action")
	@ResponseBody
	public Map<String, Object> getStandardPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.getStandardPrice(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 已结案已作废采购单批量删除
	 */
	@RequestMapping("/oa/DailyPlan/vastDeleteDailyPlan.action")
	@ResponseBody
	public Map<String, Object> vastDeleteDailyPlan(String caller, int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.vastDeleteDailyPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制采购单
	 * 
	 * @param session
	 * @param id
	 * @param caller
	 * @return
	 */
	@RequestMapping("/oa/DailyPlan/copyDailyPlan.action")
	@ResponseBody
	public Map<String, Object> copyDailyPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", DailyPlanService.copyDailyPlan(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改供应商回复信息
	 * */
	@RequestMapping("oa/DailyPlan/updateVendorBackInfo.action")
	@ResponseBody
	public Map<String, Object> updateVendorBackInfo(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.updateVendorBackInfo(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * /** 万利达，采购单同步到香港系统
	 */
	@RequestMapping("/oa/DailyPlan/synctohk.action")
	@ResponseBody
	public Map<String, Object> syncDailyPlan(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.syncDailyPlan(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 万利达，采购单刷新同步状态
	 */
	@RequestMapping("/oa/DailyPlan/syncstatus.action")
	@ResponseBody
	public Map<String, Object> resetSyncStatus(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DailyPlanService.resetSyncStatus(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

}
