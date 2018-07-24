package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BarStockProfitService;

@Controller
public class BarStockProfitController {

	@Autowired 
	private BarStockProfitService  barStockProfitService;

	/**
	 * 保存
	 */
	@RequestMapping("/scm/barStockProfit/saveProfit.action")  
	@ResponseBody 
	public Map<String, Object> saveProfit(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockProfitService.saveProfit(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 
	 */
	@RequestMapping("/scm/barStockProfit/deleteProfit.action")  
	@ResponseBody 
	public Map<String, Object> deleteProfit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockProfitService.deleteProfit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/barStockProfit/updateProfit.action")  
	@ResponseBody 
	public Map<String, Object> updateProfit(String caller,  String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockProfitService.updateProfit(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/barStockProfit/auditProfit.action")  
	@ResponseBody 
	public Map<String, Object> auditProfit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockProfitService.auditProfit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/barStockProfit/resAduitProfit.action")  
	@ResponseBody 
	public Map<String, Object> resAduitProfit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockProfitService.resAduitProfit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/barStockProfit/submitProfit.action")  
	@ResponseBody 
	public Map<String, Object> submitProfit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockProfitService.submitProfit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/scm/barStockProfit/resSubmitProfit.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProfit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockProfitService.resSubmitProfit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 根据批号批量生成条码，一个批号生成一个条码，初始化
	 * @param caller
	 * @param id
	 * @return
	 */
	
	@RequestMapping("/scm/reserve/barStockProfit/batchGenBarcode.action")  
	@ResponseBody 
	public Map<String, Object> batchGenBarcode(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		barStockProfitService.batchGenBarcode(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	
}
