package com.uas.erp.controller.ma;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.DataLimit;
import com.uas.erp.model.DataLimitDetail;
import com.uas.erp.model.DataLimitInstace;
import com.uas.erp.service.ma.DataLimitService;
@Controller
@RequestMapping(value="/ma/datalimit")
public class DataLimitController {
	@Autowired
	private DataLimitService dataLimitService;

	@RequestMapping(value = "/getDataLimits.action", method = RequestMethod.GET)
	@ResponseBody
	public List<DataLimit> getDataLimits(Integer all_) {
		return dataLimitService.getDataLimits(all_);
	}
	@RequestMapping(value ="/getDataLimitInstance.action",method=RequestMethod.GET)
	@ResponseBody
	public DataLimitInstace getDataLimitInstance(Integer empid_,Integer jobid_,Integer limitid_){
		return  dataLimitService.getDataLimitInstace(empid_,jobid_,limitid_);
	}
	@RequestMapping(value ="/InstanceDataLimit.action")
	@ResponseBody
	public Map<String,Object> InstanceDataLimit(String formData,String updates,String inserts){
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id", dataLimitService.InstanceDataLimit(formData,updates,inserts));
		map.put("success",true);
		return map;
	}
	@RequestMapping(value="/getSourceData.action",method=RequestMethod.GET)
	@ResponseBody
	public List<Map<String,Object>> getSourceData(Integer limitId_,String condition){
		return dataLimitService.getSourceData(limitId_,condition);
	} 
	@RequestMapping(value="/getLimitDetails.action",method=RequestMethod.GET)
	@ResponseBody
	public List<DataLimitDetail> getDetails(Integer InstanceId_){
		return dataLimitService.getLimitDetails(InstanceId_);
	}
	@RequestMapping(value="/CopyLimitPower.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> CopyLimitPower(String data){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		dataLimitService.CopyLimitPower(data);
		modelMap.put("success",true);
		modelMap.put("log","处理成功");
		return modelMap;

	}
	@RequestMapping(value="/deleteLimitPower.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> deleteLimitPower(String data){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		dataLimitService.deleteLimitPower(data);
		modelMap.put("success",true);
		return modelMap;

	}
}
