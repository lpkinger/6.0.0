package com.uas.erp.controller.oa;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.service.oa.MeetingService;

@Controller
public class MeetingController extends BaseController {
	@Autowired
	private MeetingService meetingService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/meeting/saveMeeting.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingService.saveMeeting(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除采购单数据 包括采购明细
	 */
	@RequestMapping("/oa/meeting/deleteMeeting.action")
	@ResponseBody
	public Map<String, Object> deletemeeting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingService.deleteMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("/oa/meeting/deleteDetail.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingService.deleteDetail(id, caller);
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
	@RequestMapping("/oa/meeting/updateMeeting.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingService.updateMeetingById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/meeting/submitMeeting.action")
	@ResponseBody
	public Map<String, Object> submitmeeting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingService.submitMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/meeting/resSubmitMeeting.action")
	@ResponseBody
	public Map<String, Object> resSubmitmeeting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingService.resSubmitMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/meeting/auditMeeting.action")
	@ResponseBody
	public Map<String, Object> auditmeeting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingService.auditMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/meeting/resAuditMeeting.action")
	@ResponseBody
	public Map<String, Object> resAuditmeeting(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingService.resAuditMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改参会状态
	 */
	@RequestMapping("/oa/meeting/changeMeetingStatus.action")
	public void changeStatus(String caller, HttpServletResponse response,
			HttpServletRequest request) {
		meetingService.changeMeetingStatus(
				Integer.parseInt(request.getParameter("id").toString()),
				SystemSession.getUser().getEm_code());
		try {
			PrintWriter pw = response.getWriter();
			pw.println("回复成功");
			pw.close();
		} catch (IOException e) {

		}
	}
}
