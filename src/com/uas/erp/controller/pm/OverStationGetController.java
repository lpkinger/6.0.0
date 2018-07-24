package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.OverStationGetService;

@Controller
public class OverStationGetController {
     @Autowired
     private OverStationGetService overStationGetService;
	
	 @RequestMapping("/pm/mes/getOverStationStore.action")
 	 @ResponseBody
 	 public Map<String, Object> getOverStationStore(String scCode, String mcCode) {
 		Map<String, Object> modelMap = new HashMap<String, Object>();
 		modelMap.put("datas", overStationGetService.getOverStationStore(scCode, mcCode));
 		modelMap.put("success", true);
 		return modelMap;
 	 }
	 @RequestMapping("/pm/mes/confirmSnCodeGet.action")
 	 @ResponseBody
 	 public Map<String, Object> confirmSnCodeGet(String sc_code, String mc_code,String sn_code,String st_code,boolean combineChecked) {
 		Map<String, Object> modelMap = new HashMap<String, Object>();
 		modelMap.put("datas", overStationGetService.confirmSnCodeGet(sc_code, mc_code,sn_code,st_code,combineChecked));
 		modelMap.put("success", true);
 		return modelMap;
 	 }
}
