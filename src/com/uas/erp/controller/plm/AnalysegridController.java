package com.uas.erp.controller.plm;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.GridPanel;
import com.uas.erp.service.plm.AnalysegridService;
@Controller
public class AnalysegridController {
 @Autowired
 private AnalysegridService analysegridService;
 @RequestMapping("plm/resource/Analysegrid.action")
 @ResponseBody
 public Map<String,Object> DataAndFields(String condition) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("data", analysegridService.getData(condition));
	 GridPanel panel=analysegridService.getGridPanel("Staresource");
	 modelMap.put("fields", panel.getGridFields());
	 
	 modelMap.put("columns",panel.getGridColumns());	 
	 modelMap.put("success",true);
	 return modelMap;
 }
 @RequestMapping("plm/log/Loggrid.action")
 @ResponseBody
 public Map<String,Object> Log(String condition,String startdate,String enddate) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("data", analysegridService.getLogData(condition,startdate,enddate));
	 GridPanel panel=analysegridService.getGridPanel("ProjectLog");
	 modelMap.put("fields", panel.getGridFields());
	 modelMap.put("columns",panel.getGridColumns());
	 modelMap.put("success",true);
	 return modelMap;
 }
 @RequestMapping("plm/log/Smallgrid.action")
 @ResponseBody
 public Map<String,Object> getData(String startdate,String enddate,int emid,String recorder) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("data", analysegridService.getEmData(startdate, enddate, emid,recorder));
	 modelMap.put("success",true);
	 return modelMap;
 }
 @RequestMapping("plm/test/testgrid.action")
 @ResponseBody
 public Map<String,Object> test(String condition,String startdate,String enddate) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("data", analysegridService.getTestData(condition,startdate,enddate));
	 GridPanel panel=analysegridService.getGridPanel("BugTest");
	 modelMap.put("fields", panel.getGridFields());
	 modelMap.put("columns",panel.getGridColumns());
	 modelMap.put("success",true);
	 return modelMap;
 }
 @RequestMapping("plm/test/handgrid.action")
 @ResponseBody
 public Map<String,Object>hand(String condition,String startdate,String enddate) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("data", analysegridService.getHandData(condition,startdate,enddate));
	 GridPanel panel=analysegridService.getGridPanel("BugHand");
	 modelMap.put("fields", panel.getGridFields());
	 modelMap.put("columns",panel.getGridColumns());
	 modelMap.put("success",true);
	 return modelMap;
 }
 @RequestMapping("plm/test/getTestData.action")
 @ResponseBody
 public Map<String,Object>getTest(String startdate,String enddate,int emid) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("data", analysegridService.getSingleTestData(emid,startdate, enddate));
	 modelMap.put("success", true);
	 return modelMap;
 }
 @RequestMapping("plm/test/getHandData.action")
 @ResponseBody
 public Map<String,Object>getHand(String startdate,String enddate,int emid) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("data", analysegridService.getSingleHandData(emid,startdate, enddate, emid));
	 modelMap.put("success", true);
	 return modelMap;
 }
}
