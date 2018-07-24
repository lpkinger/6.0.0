package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.ma.SysCheckService;

@Controller
public class SysCheckController {
	@Autowired
	private SysCheckService sysCheckService;

	@RequestMapping("ma/saveSysCheckFormula.action")
	@ResponseBody
	public Map<String, Object> saveSysCheckFormula(
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sysCheckService.saveSysCheckFormula(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("ma/updateSysCheckFormula.action")
	@ResponseBody
	public Map<String, Object> updateSysCheckFormula(
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sysCheckService.updateSysCheckFormula(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("ma/deleteSysCheckFormula.action")
	@ResponseBody
	public Map<String, Object> deleteSysCheckFormula( int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sysCheckService.deleteSysCheckFormula(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("ma/SysCheck/getAllHrTree.action")
	@ResponseBody
	public Map<String,Object> getHrJobTree(HttpSession session){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		List<JSONTree> jsontree=sysCheckService.getAllHrTree();
		modelMap.put("tree", jsontree);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("ma/SysCheck/getDataByOrg.action")
	@ResponseBody
	public Map<String,Object> getDataByOrg(int parentid,String type){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		String data=sysCheckService.getDataByOrg(parentid,type);
		modelMap.put("data",data);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("ma/SysCheck/TurnReandpunish.action")
	@ResponseBody
	public Map<String,Object> TurnReandpunish(String data){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		sysCheckService.TurnReandpunish(data);
		modelMap.put("success",true);
		return modelMap;
	}
	@RequestMapping("ma/SysCheck/RunCheck.action")
	@ResponseBody
	public Map<String,Object> RunCheck(HttpSession session){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		sysCheckService.RunCheck();
		modelMap.put("success",true);
		return modelMap;
	}
	@RequestMapping("ma/SysCheck/getTreeData.action")
	@ResponseBody
	public Map<String,Object> getTreeData(String condition){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("data", sysCheckService.getTreeData(condition));
		modelMap.put("success",true);
		return modelMap;
	}
	@RequestMapping("ma/vastUpdateSysCheckFormula.action")
	@ResponseBody
	public Map<String,Object> vastUpdateSysCheckFormula(String data){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		sysCheckService.vastUpdateSysCheckFormula(data);
		modelMap.put("success",true);
		return modelMap;
	}
}
