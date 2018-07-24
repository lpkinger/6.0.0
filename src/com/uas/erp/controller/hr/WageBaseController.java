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
import com.uas.erp.service.hr.WageBaseService;
import com.uas.erp.service.hr.WageConfService;
import com.uas.erp.service.hr.WageItemService;


@Controller
public class WageBaseController extends BaseController {
	
	@Autowired
	private WageBaseService wageBaseService;
	//保存考勤数据
	
	@RequestMapping("/hr/wage/base/save.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageBaseService.save(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//更新考勤数据
	@RequestMapping("/hr/wage/base/update.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageBaseService.update(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//删除考勤数据
	@RequestMapping("/hr/wage/base/delete.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageBaseService.delete(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//提交考勤数据
	@RequestMapping("/hr/wage/base/submit.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageBaseService.submit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//反提交考勤数据
	@RequestMapping("/hr/wage/base/resSubmit.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageBaseService.resSubmit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//审核考勤数据
	@RequestMapping("/hr/wage/base/audit.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageBaseService.audit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//反审核考勤数据
	@RequestMapping("/hr/wage/base/resAudit.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageBaseService.resAudit(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	
}
