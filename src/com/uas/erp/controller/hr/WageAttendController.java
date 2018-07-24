package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.hr.WageAttendService;
import com.uas.erp.service.hr.WageConfService;
import com.uas.erp.service.hr.WageItemService;


@Controller
public class WageAttendController extends BaseController {
	@Autowired
	private WageAttendService wageAttendService;
	//保存考勤数据
	
	@RequestMapping("/hr/wage/attend/save.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageAttendService.save(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//更新考勤数据
	@RequestMapping("/hr/wage/attend/update.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageAttendService.update(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//删除考勤数据
	@RequestMapping("/hr/wage/attend/delete.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageAttendService.delete(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//提交考勤数据
	@RequestMapping("/hr/wage/attend/submit.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageAttendService.submit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//反提交考勤数据
	@RequestMapping("/hr/wage/attend/resSubmit.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageAttendService.resSubmit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//审核考勤数据
	@RequestMapping("/hr/wage/attend/audit.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageAttendService.audit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//反审核考勤数据
	@RequestMapping("/hr/wage/attend/resAudit.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageAttendService.resAudit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	
}
