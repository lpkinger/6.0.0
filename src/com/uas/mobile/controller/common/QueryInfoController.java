package com.uas.mobile.controller.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.mobile.service.QueryInfoService;



@Controller
public class QueryInfoController {

	@Autowired
	private QueryInfoService queryInfoService;
	
	@RequestMapping("/mobile/qry/getReport.action")
	@ResponseBody
	public Map<String,Object> getInfoByCode(String emcode,String condition){
		Map<String, Object> data = queryInfoService.getInfoByCode(emcode,condition);
		return data;
	}
	
	@RequestMapping("/mobile/qry/reportCondition.action")
	@ResponseBody
	public Map<String,Object> getReportCondition(String caller,String title){
		Map<String, Object> condition = queryInfoService.getReportCondition(caller,title);
		return condition;
	}
	
	@RequestMapping("/mobile/qry/queryJsp.action")
	@ResponseBody
	public Map<String,Object> getQueryJsp(String emcode){
		Map<String, Object> map = queryInfoService.getQueryJsp(emcode);
		return map;
		
	}
	
	@RequestMapping("/mobile/qry/schemeCondition.action")
	@ResponseBody
	public Map<String,Object> getSchemeCondition(String caller,String id){
		Map<String,Object> map=queryInfoService.getSchemeConditin(caller, id);
		return map;
	}
	
	@RequestMapping("/mobile/qry/schemeResult.action")
	@ResponseBody
	public Map<String,Object>getSchemeResult(String caller,Integer id,int pageIndex,int pageSize,String condition){
			return queryInfoService.getSchemeResult(caller,id,pageIndex,pageSize,condition);
	}
	
}

