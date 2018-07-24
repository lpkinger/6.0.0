package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.SampleService;

@Controller
public class SampleController {
	@Autowired
	SampleService sampleService;
	
	
	@RequestMapping("/scm/qc/saveSample.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleService.saveSample(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/qc/deleteSample.action")  
	@ResponseBody 
	public Map<String, Object> deleteSample(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleService.deleteSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/qc/updateSample.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleService.updateSampleById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/qc/printSample.action")  
	@ResponseBody 
	public Map<String, Object> printSample(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sampleService.printSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 客户打样申请转送样单
	 */
	@RequestMapping("/b2b/product/turnSendSample.action")
	@ResponseBody
	public Map<String, Object> turnBuglist(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int buid = sampleService.turnSendSample(caller, id);
		modelMap.put("id", buid);
		modelMap.put("success", true);
		return modelMap;
	}
}
