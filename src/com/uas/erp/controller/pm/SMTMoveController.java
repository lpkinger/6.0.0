package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.SMTMoveService;

@Controller
public class SMTMoveController extends BaseController{
     @Autowired 
     private SMTMoveService SMTMoveService;
     
     @RequestMapping("/pm/mes/loadSMTMoveStore.action")
 	 @ResponseBody
 	 public Map<String, Object> loadSMTMoveStore(String de_oldCode, String mc_code) {
 		Map<String, Object> modelMap = new HashMap<String, Object>();
 		modelMap.put("datas", SMTMoveService.loadSMTMoveStore(de_oldCode, mc_code));
 		modelMap.put("success", true);
 		return modelMap;
 	 }
     
     @RequestMapping("/pm/mes/comfirmSMTMove.action")
 	 @ResponseBody
 	 public Map<String, Object> comfirmSMTMove(String de_oldCode, String mc_code,String de_newCode) {
 		Map<String, Object> modelMap = new HashMap<String, Object>();
 		SMTMoveService.comfirmSMTMove(de_oldCode, mc_code,de_newCode);
 		modelMap.put("success", true);
 		return modelMap;
 	 }
}
