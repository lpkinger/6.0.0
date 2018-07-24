package com.uas.erp.controller.plm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.FormItems;
import com.uas.erp.model.FormPanel;
import com.uas.erp.service.plm.CheckService;

@Controller
public class CheckController extends BaseController {
	@Autowired
	private CheckService checkService;

	@RequestMapping(value = "/plm/CheckFormItemsAndData.action")
	@ResponseBody
	public Map<String, Object> getFormItems(HttpSession session, String caller, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FormPanel panel = checkService.getFormItemsByCaller(caller, condition);
		for (FormItems item : panel.getItems()) {
			Object value = decodeDefaultValue(session, item.getValue());
			item.setValue(value);
		}
		modelMap.put("data", panel.getData());
		modelMap.put("items", panel.getItems());
		modelMap.put("buttons", panel.getButtons());
		return modelMap;
	}

	public Object decodeDefaultValue(HttpSession session, Object value) {
		if (value.toString().contains("getCurrentDate()")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(new Date());
		} else if (value.toString().contains("session:")) {
			Object obj = session.getAttribute(value.toString().trim().split(":")[1]);
			return (obj == null) ? "" : obj;
		}
		return (value == null || value.equals("null") ? "" : value);
	}

	@RequestMapping(value = "/plm/check/saveCheck.action")
	@ResponseBody
	public Map<String, Object> saveCheck(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.saveCheck(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/plm/check/updateCheck.action")
	@ResponseBody
	public Map<String, Object> updateCheck(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.updateCheck(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/plm/check/deleteCheck.action")
	@ResponseBody
	public Map<String, Object> deleteCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.deleteCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/submitCheck.action")
	@ResponseBody
	public Map<String, Object> submitCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.submitCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/ressubmitCheck.action")
	@ResponseBody
	public Map<String, Object> ressubmitCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.resubmitCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/auditCheck.action")
	@ResponseBody
	public Map<String, Object> auditCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.auditCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/resauditCheck.action")
	@ResponseBody
	public Map<String, Object> resauditCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.reauditCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/changeBugStatus.action")
	@ResponseBody
	public Map<String, Object> changeBugStatus(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.changeBugStatus(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/changeHandler.action")
	@ResponseBody
	public Map<String, Object> changeHandler(int id, int oldemid, int newemid, String description) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.changeHandler(id, oldemid, newemid, description);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/confirm.action")
	@ResponseBody
	public Map<String, Object> confirm(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkService.confirm(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
