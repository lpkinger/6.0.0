package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.VehiclereturnService;

@Controller
public class VehiclereturnController {
	@Autowired
	private VehiclereturnService vehiclereturnService;
	
	@RequestMapping("/oa/vehicle/saveVehiclereturn.action")  
	@ResponseBody 
	public Map<String,Object> saveAddressBookGroup(String caller,String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclereturnService.saveVehiclereturn(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/oa/vehicle/confirmVehiclereturn.action")  
	@ResponseBody 
	public Map<String,Object> confirmVehiclereturn(int id,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclereturnService.confirmVehiclereturn(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/oa/vehicle/resConfirmVehiclereturn.action")  
	@ResponseBody 
	public Map<String,Object> resConfirmVehiclereturn(int id,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vehiclereturnService.resConfirmVehiclereturn(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
