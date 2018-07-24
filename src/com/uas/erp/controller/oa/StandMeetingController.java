package com.uas.erp.controller.oa;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.StandMeetingService;

@Controller
public class StandMeetingController {
	@Autowired
	private StandMeetingService standMeetingService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/meeting/saveStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore,String caller) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.saveStandMeeting( formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除例会
	 */
	@RequestMapping("/oa/meeting/deleteStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> deleteMeetingroomapply(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.deleteStandMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/meeting/updateStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String caller) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.updateStandMeeting(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 生成会议申请
	 *
	 */
	@RequestMapping("oa/meeting/turnMeeting.action")  
	@ResponseBody 
	public Map<String, Object> turnEnd(String caller,  String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log=standMeetingService.turnMeeting(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/oa/meeting/banStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> banMeetingroomapply(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.banStandMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/oa/meeting/resBanStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> resBanMeetingroomapply(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.resBanStandMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/oa/meeting/submitStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> submitStandMeeting(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.submitStandMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提
	 */
	@RequestMapping("/oa/meeting/resSubmitStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitStandMeeting(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.resSubmitStandMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/oa/meeting/auditStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> auditStandMeeting(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.auditStandMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审
	 */
	@RequestMapping("/oa/meeting/resAuditStandMeeting.action")  
	@ResponseBody 
	public Map<String, Object> resAuditStandMeeting(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		standMeetingService.resAuditStandMeeting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
