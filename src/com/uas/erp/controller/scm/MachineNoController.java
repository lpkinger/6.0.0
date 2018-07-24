package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.MachineNoService;

@Controller
public class MachineNoController extends BaseController {
	@Autowired
	private MachineNoService machineNoService;

	@RequestMapping("/scm/sale/getProdioMachine.action")
	@ResponseBody
	public Map<String, Object> getProdioMachine(int piid, boolean iswcj) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", machineNoService.getProdioMachine(piid, iswcj));
		return modelMap;
	}

	@RequestMapping("/scm/sale/insertProdioMac.action")
	@ResponseBody
	public Map<String, Object> insertProdioMac(int piid, String inoutno, String machineno, String prcode, int qty) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		machineNoService.insertProdioMac(piid, inoutno, machineno, prcode, qty);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/sale/deleteProdioMac.action")
	@ResponseBody
	public Map<String, Object> deleteProdioMac(int piid, String inoutno, String machineno, String prcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		machineNoService.deleteProdioMac(piid, inoutno, machineno, prcode);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/sale/clearProdioMac.action")
	@ResponseBody
	public Map<String, Object> clearProdioMac(int piid, String prcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		machineNoService.clearProdioMac(piid, prcode);
		modelMap.put("success", true);
		return modelMap;
	}
}
