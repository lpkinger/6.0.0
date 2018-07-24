package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.BarPrintService;

@Controller
public class BarPrintController extends BaseController {
	@Autowired
	private BarPrintService barPrintService;
	/**
	 * 保存
	 */
	@RequestMapping("/scm/reserve/saveBarPrint.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barPrintService.saveBarPrint(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改
	 */
	@RequestMapping("/scm/reserve/updateBarPrint.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barPrintService.updateBarPrint(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 分装单据
	 */
	@RequestMapping("/scm/reserve/BarPrint/Subpackage.action")  
	@ResponseBody 
	public Map<String, Object> Subpackage(String caller, int id, double tqty) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = barPrintService.Subpackage(id, tqty, caller);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}
	/**
	 * 清除分装明细
	 */
	@RequestMapping("/scm/reserve/BarPrint/ClearSubpackage.action")  
	@ResponseBody 
	public Map<String, Object> ClearSubpackage(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String logString = barPrintService.ClearSubpackage(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", logString);
		return modelMap;
	}
	/**
	 * 条码打印
	 */
	@RequestMapping("/scm/reserve/barPrint.action")  
	@ResponseBody 
	public Map<String, Object> printBar(String caller, int id,String reportName,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = barPrintService.printBar(id, caller, reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}
}
