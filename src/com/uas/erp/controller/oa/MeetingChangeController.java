package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.MeetingChangeService;
@Controller
public class MeetingChangeController {
	@Autowired
	private MeetingChangeService meetingChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/meeting/saveMeetingChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingChangeService.saveMeetingChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/oa/meeting/deleteMeetingChange.action")
	@ResponseBody
	public Map<String, Object> deleteMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingChangeService.deleteMeetingChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/meeting/updateMeetingChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingChangeService.updateMeetingChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/meeting/submitMeetingChange.action")
	@ResponseBody
	public Map<String, Object> submitMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingChangeService.submitMeetingChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/meeting/resSubmitMeetingChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingChangeService.resSubmitMeetingChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/meeting/auditMeetingChange.action")
	@ResponseBody
	public Map<String, Object> auditMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingChangeService.auditMeetingChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/meeting/resAuditMeetingChange.action")
	@ResponseBody
	public Map<String, Object> resAuditMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingChangeService.resAuditMeetingChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
