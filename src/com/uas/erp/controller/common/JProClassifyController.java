package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.common.JProClassifyService;

@Controller
public class JProClassifyController {
	@Autowired
	JProClassifyService jProClassifyService;
	
	@RequestMapping("/common/getJProClassifies.action")  
	@ResponseBody 
	public Map<String, Object> getJProClassifies(int start,int limit) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		modelMap.put("proclassify",jProClassifyService.getJProClassifies(start,limit));
		return modelMap;
	}
    
	@RequestMapping("/common/saveJProClassify.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session,String formStore, String param) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProClassifyService.saveJProClassify(formStore, param, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/common/deleteJProClassify.action")  
	@ResponseBody 
	public Map<String, Object> deleteJProClassify(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProClassifyService.deleteJProClassify(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改
	 */
	@RequestMapping("/common/updateJProClassify.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session,String formStore, String param) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProClassifyService.updateJProClassifyById(formStore, param, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
    /**
     * 查找所有分类
     * */
	@RequestMapping("/common/getAllJProClassify.action")
	@ResponseBody
	public Map<String,Object> getAllJProClassify(HttpSession session){
		Map<String,Object> modelMap=new HashMap<String,Object>();
   		String language=(String)session.getAttribute("language");
   		Employee employee=(Employee)session.getAttribute("employee");	
   		modelMap.put("data", jProClassifyService.getAllJProClassify(language,employee));
   		return modelMap;
	}
	@RequestMapping("/common/removeToOtherClassify.action")  
	@ResponseBody
	public Map<String,Object> removeToOtherGroup(HttpSession session,int id ,String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String language = (String)session.getAttribute("language");
		Employee employee=(Employee)session.getAttribute("employee");
		jProClassifyService.removeToOtherClassify(id,data,language,employee);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/common/getAllProcessInfo.action")  
	@ResponseBody
	public Map<String,Object> getAllProcessInfo(HttpSession session){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<Object,Object> info=jProClassifyService.getAllJprocessDeployInfo();
		modelMap.put("deploy", info.get("deploy"));
		modelMap.put("classify", info.get("classify"));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/common/getProcessInfoByCondition.action")
	@ResponseBody
	public Map<String,Object> getProcessInfoByCondition(HttpSession session,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", jProClassifyService.getProcessInfoByCondition(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("custom/orderByJprocess.action")
	@ResponseBody
	public Map<String,Object> orderByJprocess(HttpSession session,String data){
		String language=(String)session.getAttribute("language");
		Employee employee=(Employee) session.getAttribute("employee");
		Map<String,Object> modelMap=new HashMap<String,Object>();
		jProClassifyService.orderByJprocess(data,language,employee);
		modelMap.put("success",true);
		return modelMap;
	}
	
	/**
	 * SAAS初始化获得流程树
	 */
	@RequestMapping(value = "/common/getJpTree.action")
	@ResponseBody
	public Map<String, Object> getJpTree(HttpSession session,String condition) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", jProClassifyService.getJpTree(condition));
		return modelMap;
	}

	
}
