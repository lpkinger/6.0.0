package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.NGReasonService;

@Controller
public class NGReasonController {
	@Autowired
	NGReasonService ngReasonService;
	
	
	@RequestMapping("/scm/qc/saveNGReason.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ngReasonService.saveNGReason(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/qc/deleteNGReason.action")  
	@ResponseBody 
	public Map<String, Object> deleteNGReason(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ngReasonService.deleteNGReason(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/qc/updateNGReason.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ngReasonService.updateNGReasonById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/qc/printNGReason.action")  
	@ResponseBody 
	public Map<String, Object> printNGReason(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ngReasonService.printNGReason(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
