package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.WorkPlan;
import com.uas.erp.model.WorkPlanDetail;
import com.uas.erp.service.oa.WorkPlanService;

@Controller
public class WorkPlanController {
	@Autowired
	private WorkPlanService workPlanService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/persontask/workPlan/saveWorkPlan.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanService.saveWorkPlan(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/persontask/workPlan/updateWorkPlan.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanService.updateWorkPlan(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/persontask/workPlan/deleteWorkPlan.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanService.deleteWorkPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/persontask/workPlan/saveWorkPlanDetail.action")
	@ResponseBody
	public Map<String, Object> saveWorkPlanDetail(String caller,
    		String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanService.saveWorkPlanDetail(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/persontask/workPlan/updateWorkPlanDetail.action")
	@ResponseBody
	public Map<String, Object> updateWorkPlanDetail(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanService.updateWorkPlanDetail(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/persontask/workPlan/deleteWorkPlanDetail.action")
	@ResponseBody
	public Map<String, Object> deleteWorkPlanDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanService.deleteWorkPlanDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取
	 */
	@RequestMapping("/oa/persontask/workPlan/getWorkPlan.action")
	@ResponseBody
	public Map<String, Object> getWorkPlan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkPlan wp = workPlanService.getWorkPlan(id, caller);
		modelMap.put("success", true);
		modelMap.put("workplan", wp);
		List<WorkPlanDetail> list = workPlanService.getWorkPlanDetailList(id,
				caller);
		modelMap.put("workplandetaillist", list);
		return modelMap;
	}

	/**
	 * 获取
	 */
	@RequestMapping("/oa/persontask/workPlan/queryWorkPlan.action")
	@ResponseBody
	public Map<String, Object> queryWorkPlan(String caller, String title) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		WorkPlan wp = workPlanService.queryWorkPlan(title, caller);
		modelMap.put("success", true);
		modelMap.put("workplan", wp);
		return modelMap;
	}

	/**
	 * 获取
	 */
	@RequestMapping("/oa/persontask/workPlan/getWorkPlanDetail.action")
	@ResponseBody
	public Map<String, Object> getWorkPlanDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<WorkPlanDetail> list = workPlanService.getWorkPlanDetailList(id,
				caller);
		modelMap.put("success", true);
		modelMap.put("workplandetaillist", list);
		return modelMap;
	}
}
