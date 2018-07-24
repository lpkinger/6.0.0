package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
 







import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.service.common.JasperReportPrintService;
import com.uas.erp.service.pm.StepioService;

@Controller
public class StepioController extends BaseController {
	@Autowired
	private StepioService StepioService;
	
	@Autowired
	private JasperReportPrintService jasperReportPrintService;
	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/make/saveStepio.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.saveStepio(formStore, caller,param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteStepio.action")
	@ResponseBody
	public Map<String, Object> deleteStepio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.deleteStepio(id, caller);
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
	@RequestMapping("/pm/make/updateStepio.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param,String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.updateStepioById(formStore, caller,param,param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitStepio.action")
	@ResponseBody
	public Map<String, Object> submitStepio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.submitStepio(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批量提交make/batchSumbitStepio.action
	 */
	@RequestMapping("/pm/make/batchSumbitStepio.action")
	@ResponseBody
	public Map<String, Object> batchSumbitStepio(String datas, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.batchSumbitStepio(datas, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批量提交make/batchSumbitStepio.action
	 */
	@RequestMapping("/pm/make/batchPostStepio.action")
	@ResponseBody
	public Map<String, Object> batchPostStepio(String datas, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.batchPostStepio(datas, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 批量打印
	 */
	@RequestMapping("/pm/make/batchPrintStepio.action")
	@ResponseBody
	public Map<String, Object> batchPrintStepio(HttpServletRequest request,String datas, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<Object,Object>> list = BaseUtil.parseGridStoreToMaps(datas);
		String ids = null;
		for(Map<Object,Object> map : list){
			Object id = map.get("si_id");
			if(ids==null){
				ids = id.toString();
			}else{
				ids = ids + ","+id.toString();
			};
		}
		jasperReportPrintService.batchPrint(ids,caller+"!BatchPrint",null,request);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitStepio.action")
	@ResponseBody
	public Map<String, Object> resSubmitStepio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.resSubmitStepio(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/make/getClashInfo.action")
	@ResponseBody
	public Map<String, Object> getClashInfo(String caller, String con) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("info", StepioService.getClashInfo(caller,con));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 过账
	 */
	@RequestMapping("/pm/make/postStepIO.action")
	@ResponseBody
	public Map<String, Object> postStepIO(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.postStepIO(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/pm/make/resPostStepIO.action")
	@ResponseBody
	public Map<String, Object> resPostStepIO(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.resPostStepIO(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 设置冲减
	 */
	@RequestMapping("/pm/make/setclash.action")
	@ResponseBody
	public Map<String, Object> setclash(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.setclash(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 确定冲减
	 */
	@RequestMapping("/pm/make/saveclash.action")
	@ResponseBody
	public Map<String, Object> saveclash(String caller,String data, int id,int clashqty) { 
		Map<String, Object> modelMap = new HashMap<String, Object>();
		StepioService.saveclash( caller,data,id,clashqty);
		modelMap.put("success", true);
		return modelMap;
	}
}
