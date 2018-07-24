package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ChartMangService;
@Controller
public class ChartMangController {
	@Autowired
	private ChartMangService chartMangService;
	/**
	 * 保存
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/product/saveChartMang.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartMangService.saveChartMang(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改
	 */
	@RequestMapping("/scm/product/updateChartMang.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartMangService.updateChartMang(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteChartMang.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartMangService.deleteChartMang(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核AskLeave
	 */
	@RequestMapping("/scm/product/auditChartMang.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartMangService.auditChartMang(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditChartMang.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartMangService.resAuditChartMang(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitChartMang.action")  
	@ResponseBody 
	public Map<String, Object> submit(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartMangService.submitChartMang(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitChartMang.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chartMangService.resSubmitChartMang(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
