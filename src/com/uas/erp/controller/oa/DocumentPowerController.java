package com.uas.erp.controller.oa;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.DocumentPowerService;
@Controller
public class DocumentPowerController {
   @Autowired
   private DocumentPowerService documentPowerService;
   @RequestMapping("/doc/docmentPowerSet.action")
   @ResponseBody
   public Map<String ,Object> DocumentPowerSet(String folderId,String  powers,String objects, int sub){
	   Map<String,Object> map=new HashMap<String, Object>();
	   documentPowerService.setDocPower(folderId, powers, objects, sub);
	   map.put("success",true);
	   return map;
   }
   @RequestMapping("/doc/updatePowerSet.action")
   @ResponseBody
   public Map<String ,Object> updatePowerSet(String  param){
	   Map<String,Object> map=new HashMap<String, Object>();
	   documentPowerService.updatePowerSet(param);
	   map.put("success",true);
	   return map;
   }
   @RequestMapping("/doc/deletePowerSet.action")
   @ResponseBody
   public Map<String ,Object> deletePowerSet(String param){
	   Map<String,Object> map=new HashMap<String, Object>();
	   documentPowerService.deletePowerSet(param);
	   map.put("success",true);
	   return map;
   }
   @RequestMapping("/doc/CheckPowerByFolderId.action")
   @ResponseBody
   public Map<String ,Object> CheckPowerByFolderId(int folderId,String type){
	   Map<String,Object> map=new HashMap<String, Object>();
	   boolean bool=documentPowerService.CheckPowerByFolderId(folderId,type);
	   map.put("bool",bool);
	   return map;
   }
}
