package com.uas.erp.controller.oa;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.MeetingDocService;

@Controller
public class MeetingDocController {
	@Autowired
	private MeetingDocService MeetingDocService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/meeting/saveMeetingDoc.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request,String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MeetingDocService.saveMeetingDoc(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 修改
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/meeting/updateMeetingDoc.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MeetingDocService.updateMeetingDoc(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/meeting/deleteMeetingDoc.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
	    Map<String, Object> modelMap = new HashMap<String, Object>();
		MeetingDocService.deleteMeetingDoc(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核MeetingDoc
	 */
	@RequestMapping("/oa/meeting/auditMeetingDoc.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MeetingDocService.auditMeetingDoc(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/meeting/resAuditMeetingDoc.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MeetingDocService.resAuditMeetingDoc(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/meeting/submitMeetingDoc.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MeetingDocService.submitMeetingDoc(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/meeting/resSubmitMeetingDoc.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MeetingDocService.resSubmitMeetingDoc(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 会议签到
	 */
	@RequestMapping("/oa/meeting/meetingSignIn.action")
	@ResponseBody
	public Map<String, Object> meetingSignIn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MeetingDocService.meetingSign(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
