package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.BusinessChanceQueryService;
@Controller
@RequestMapping("/crm/business")
public class BusinessChanceQueryController {
	@Autowired
	private BusinessChanceQueryService businessChanceQueryService;
	@RequestMapping(value="/getProcessInfoByCondition.action" ,method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getProcessInfoByCondition(String condition,Integer start,
			Integer end){
		Map<String,Object> modelMap=businessChanceQueryService.getBusinessChanceQueryConfigs(condition,start,end);
	    modelMap.put("sucess",true);
		return modelMap;
	}
	@RequestMapping(value="/getProcessDataByCondition.action" ,method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getProcessDataByCondition(String condition){
		Map<String,Object> map=new HashMap<String,Object>();
		String data=businessChanceQueryService.getProcessDataByCondition(condition);
		map.put("data",data);
		return  map;
	}
	@RequestMapping(value="/getHopperByCondition.action" ,method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getHopperByCondition(String condition){
		Map<String,Object> map=new HashMap<String,Object>();
		List<JSONObject> objs=businessChanceQueryService.getHopperByCondition(condition);
		map.put("counts",objs);
		return  map;
	}
	@RequestMapping(value="/getChanceDatas.action" ,method= RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getChanceDatasById(int id){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("datas",businessChanceQueryService.getChanceDatasById(id));
		return  map;
	}
}
