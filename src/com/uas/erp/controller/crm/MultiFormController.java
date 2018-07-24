package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.crm.MultiFormService;

@Controller
public class MultiFormController {
	@Autowired
	private MultiFormService multiFormService;

	@RequestMapping("/crm/updateMultiForm.action")  
	@ResponseBody 
	public Map<String, Object> mupdate(HttpSession session, String formStore, String param, String param2, String param3,
			String param4,  String param5,  String param6,String param7) {//param7是type
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		multiFormService.update(formStore, param, param2, param3, language, employee,param7);
		multiFormService.updateDetailGrid(param4, param5, param6, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/crm/saveMultiForm.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param, String param2,String type) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap=multiFormService.add(formStore, param, param2,language, employee,type);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/crm/deleteMultiForm.action")  
	@ResponseBody 
	public Map<String, Object> mdelete(HttpSession session, int id,String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		multiFormService.mdelete(id, language, employee,type);
		modelMap.put("success", true);
		return modelMap;
	}
	/*自定义按钮分组*/
	@RequestMapping("/crm/deleteButtonGroup.action")  
	@ResponseBody 
	public Map<String, Object> deleteButtonGroup(HttpSession session,String caller) {
		Map<String, Object> modelMap=new HashMap<String, Object>();
		multiFormService.deleteButtonGroup(caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 *  保存按钮排序
	 */
	@RequestMapping("/crm/saveButtonGroup.action")  
	@ResponseBody 
	public Map<String, Object> saveButtonGroup(HttpSession session, String jsonstr, String caller) {
		Map<String, Object> modelMap=new HashMap<String, Object>();
		multiFormService.saveButtonGroup(jsonstr, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 *  保存单个按钮文字自定义
	 */
	@RequestMapping("/crm/updateButton.action")  
	@ResponseBody 
	public Map<String, Object> updateButton(HttpSession session,String caller,String groupid,String oldText,String newText) {
		Map<String, Object> modelMap=new HashMap<String, Object>();
		multiFormService.updateButton(caller, groupid, oldText, newText);
		modelMap.put("groupid", groupid);
		modelMap.put("newText", newText);
		modelMap.put("oldText", oldText);
		modelMap.put("success", true);
		return modelMap;
	}
}
