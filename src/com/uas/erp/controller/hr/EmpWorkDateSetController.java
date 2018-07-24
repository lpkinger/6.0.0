package com.uas.erp.controller.hr;

import com.uas.erp.model.JSONTree;
import com.uas.erp.service.hr.EmpWorkDateSetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

@Controller
public class EmpWorkDateSetController {

	@Autowired
	private EmpWorkDateSetService empWorkDateSetService;

	/**
	 * 保存设置
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/attendance/saveEmpWorkDateSet.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateSetService.saveEmpWorkDateSet(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 默认班次-人员
	 */
	@RequestMapping("hr/attendance/getWdTreeAndEmployees.action")
	@ResponseBody
	public Map<String, Object> getWdTreeAndEmployees(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree = empWorkDateSetService.getWdTreeAndEmployees(caller);
		modelMap.put("tree", tree);
		return modelMap;
	}

	/**
	 * 对象选择时筛选
	 * */
	@RequestMapping("/hr/attendance/search.action")  
	@ResponseBody 
	public Map<String, Object> search(HttpSession session,String likestring) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    modelMap.put("data",empWorkDateSetService.search(likestring));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 员工班次查询
	 */
	@RequestMapping(value = "hr/attendance/getDatas.action")
	@ResponseBody
	public Map<String, Object> getDatas(String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",empWorkDateSetService.getDatas( condition));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
 	 * 批量删除
	 */
	@RequestMapping("hr/attendance/cleanEmpworkdate.action")
	@ResponseBody
	public Map<String, Object> cleanEmpworkdate(String caller, String data){
		Map<String, Object> modelMap=new HashMap<String, Object>();
		String log=empWorkDateSetService.deleteEmpworkdate(caller,data);
		modelMap.put("log",log);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
