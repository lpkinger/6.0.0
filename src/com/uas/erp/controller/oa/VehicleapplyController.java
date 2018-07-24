package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.VehicleapplyService;

@Controller
public class VehicleapplyController {

	@Autowired
	private VehicleapplyService vehicleapplyService;

	/**
	 * 保存oaOrg
	 */
	@RequestMapping("/oa/vehicle/saveVehicleapply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.saveVehicleapply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/oa/vehicle/updateVehicleapply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.updateVehicleapplyById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/vehicle/deleteVehicleapply.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.deleteVehicleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/vehicle/submitVehicleapply.action")
	@ResponseBody
	public Map<String, Object> submitVehicleapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.submitVehicleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/vehicle/resSubmitVehicleapply.action")
	@ResponseBody
	public Map<String, Object> resSubmitVehicleapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.resSubmitVehicleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/vehicle/auditVehicleapply.action")
	@ResponseBody
	public Map<String, Object> auditVehicleapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.auditVehicleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/vehicle/resAuditVehicleapply.action")
	@ResponseBody
	public Map<String, Object> resAuditVehicleapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.resAuditVehicleapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/vehicle/turnVehicle.action")
	@ResponseBody
	public Map<String, Object> turnVehicle(String caller, JSONObject formJson) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.turnVehicle(formJson, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/oa/vastTurnVehiclereturn.action")
	@ResponseBody
	public Map<String, Object> turnReturnVehicle(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", vehicleapplyService.turnReturnVehicle(caller,data));		
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	@RequestMapping("/oa/vehicle/backUpdateVehicle.action")
	@ResponseBody
	public Map<String, Object> backUpdateVehicle(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.backUpdateVehicle(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 派车申请单打印
	 */
	@RequestMapping("/oa/vehicle/printVehicleapply.action")
	@ResponseBody
	public Map<String, Object> print(HttpSession session, int id,
			String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = vehicleapplyService.printVehicleapply(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	
	/**
	 * 派车单打印
	 */
	@RequestMapping("/oa/vehicle/printVehiclereturn.action")
	@ResponseBody
	public Map<String, Object> printVehiclereturn(HttpSession session, int id,
			String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = vehicleapplyService.printVehiclereturn(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	
	/**
	 * 派车单刷新时间
	 */
	@RequestMapping("/oa/vehicle/refreshSendTime.action")
	@ResponseBody
	public Map<String, Object> refreshSendTime( String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehicleapplyService.refreshSendTime(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
}
