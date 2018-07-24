package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.CheckListService;

@Controller
public class CheckListController extends BaseController {
	@Autowired
	private CheckListService checkListService;

	@RequestMapping("/plm/check/saveCheckList.action")
	@ResponseBody
	public Map<String, Object> saveCheckList(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListService.saveCheckList(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/deleteCheckList.action")
	@ResponseBody
	public Map<String, Object> deleteCheckList(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListService.deleteCheckList(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/updateCheckList.action")
	@ResponseBody
	public Map<String, Object> updateCheckList(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListService.updateCheckList(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/check/submitCheckList.action")
	@ResponseBody
	public Map<String, Object> submitCheckList(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListService.submitCheckList(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/check/resSubmitCheckList.action")
	@ResponseBody
	public Map<String, Object> resSubmitCheckList(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListService.reSubmitCheckList(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/check/auditCheckList.action")
	@ResponseBody
	public Map<String, Object> auditCheckList(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListService.auditCheckList(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/check/resAuditCheckList.action")
	@ResponseBody
	public Map<String, Object> resAuditCheckList(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		checkListService.resAuditCheckList(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
