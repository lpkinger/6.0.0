package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.SendSampleService;

@Controller
public class SendSampleController {

	@Autowired
	private SendSampleService sendSampleService;

	@RequestMapping("/scm/product/turnProductApproval.action")
	@ResponseBody
	public Map<String, Object> turnProductApproval(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log=sendSampleService.turnProductApproval(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}

	@RequestMapping("/scm/product/saveSendSample.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sendSampleService.saveSendSample(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/product/SendToProdInout.action")
	@ResponseBody
	public Map<String, Object> sendToProdInout(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pi_id = sendSampleService.sendToProdInout(formStore, param, caller);
		modelMap.put("success", true);
		modelMap.put("id", pi_id);
		return modelMap;
	}

	@RequestMapping("/scm/product/SendToPurInout.action")
	@ResponseBody
	public Map<String, Object> sendToPurInout(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pi_id = sendSampleService.sendToPurInout(formStore, param, caller);
		modelMap.put("success", true);
		modelMap.put("id", pi_id);
		return modelMap;
	}
}
