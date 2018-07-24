package com.uas.sysmng.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.sysmng.service.SysmngUpgradeService;

@Controller
@RequestMapping("/upgrade")
public class SysmngUpgradeController {
	
	@Autowired
	private SysmngUpgradeService sysmngUpgradeService;	
	
	
	@RequestMapping(value = "/lazyTree.action")
	@ResponseBody
	public Map<String, Object> lazyTree(int parentId,String condition) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		
		List<JSONTree> tree  =sysmngUpgradeService.getJSONTreeByParentId(parentId,condition);

		modelMap.put("tree", tree);
		return modelMap;
	}
	@RequestMapping("/saveVersionLog.action")  
	@ResponseBody 
	public Map<String, Object> saveVersionLog(HttpSession session,String id,String numid,String version,String remark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		String name=employee.getEm_name();		
		sysmngUpgradeService.updateVersionLog(id,numid,version,remark,name);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/getLog.action")  
	@ResponseBody 
	public Map<String, Object> getLog(String id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(id != null && !id.equals("")){
			
			List<Map<String,Object>> log=sysmngUpgradeService.searchLog(id);
			modelMap.put("log", log);
		}
		return modelMap;
	}
	
	@RequestMapping("/getUpgradeSqlCount.action")
	@ResponseBody
	public Map<String,Object> getUpgradeSqlCount(String condition) {
		Map<String,Object> modelMap= new HashMap<String, Object>();
		modelMap.put("count",sysmngUpgradeService.getUpgradeSqlCount(condition));
		modelMap.put("success",true);
		return modelMap;
	}
	
	@RequestMapping("/getUpgradeSqlData.action")
	@ResponseBody
	public Map<String,Object> getUpgradeSqlData(String condition,int page,int pageSize) {
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("data", sysmngUpgradeService.getUpgradeSqlData(condition,page,pageSize));
		modelMap.put("success",true);
		return modelMap;
	}
	
	@RequestMapping("/getUpgradeSql.action")
	@ResponseBody
	public Map<String,Object> getUpgradeSql(String condition) {
		Map<String,Object> modelMap=sysmngUpgradeService.getUpgradeSql(condition);
		return modelMap;
	}
	
	@RequestMapping("/saveUpgradeSql.action")
	@ResponseBody
	public Map<String,Object> saveUpgradeSql(String formStore) {
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("data",sysmngUpgradeService.saveUpgradeSql(formStore));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/updateUpgradeSql.action")
	@ResponseBody
	public Map<String,Object> updateUpgradeSql(String formStore) {
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("data",sysmngUpgradeService.updateUpgradeSql(formStore));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/deleteUpgradeSql.action")
	@ResponseBody
	public Map<String,Object> deleteUpgradeSqlByID(int id) {
		Map<String,Object> modelMap=new HashMap<String, Object>();
		sysmngUpgradeService.deleteUpgradeSqlByID(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/checksql.action")
	@ResponseBody
	public Map<String,Object> checkSql(HttpSession session,int id,String sqls) {
		Map<String,Object> map = sysmngUpgradeService.checkSqls(id,sqls);
		map.put("success", true);
		return map;
	}
	
}
