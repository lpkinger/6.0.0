package com.uas.erp.controller.oa;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.exception.SystemException;
import com.uas.erp.service.oa.MeetingroomapplyService;

@Controller
public class MeetingroomapplyController {
	@Autowired
	private MeetingroomapplyService meetingroomapplyService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/meeting/saveMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> save(HttpServletRequest request,String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String sessionId = request.getParameter("sessionId");
		try{
			meetingroomapplyService.saveMeetingroomapply(formStore, param,
					caller);
		}catch (SystemException e){  //pc端和移动端处理异常错误信息,返回不同的错误提示
			String message = e.getMessage();
			if(sessionId!=null&!"".equals(sessionId)){ //移动端去除链接
				if(message.contains("<a href=")){
					message = message.substring(0,message.indexOf("</a>"));
					String code = message.substring(message.lastIndexOf(">"),message.length());
					message = message.substring(0,message.indexOf("<a href=")).replace(">", "") + code;
					message = message.replace(">", "");

					throw new SystemException(message);
				}
			}else{
				throw e;
			}
		}

		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/oa/meeting/deleteMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> deleteMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.deleteMeetingroomapply(id, caller);
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
	 * @throws ParseException 
	 */
	@RequestMapping("/oa/meeting/updateMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.updateMeetingroomapply(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/meeting/submitMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> submitMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.submitMeetingroomapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/meeting/resSubmitMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> resSubmitMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.resSubmitMeetingroomapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/meeting/auditMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> auditMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.auditMeetingroomapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/meeting/resAuditMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> resAuditMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.resAuditMeetingroomapply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认出席人员
	 */
	@RequestMapping("/oa/meeting/confirmMan.action")
	@ResponseBody
	public Map<String, Object> confirmMan(String caller,
			String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.confirmMan(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转会议纪要
	 */
	@RequestMapping("/oa/meeting/turnDoc.action")
	@ResponseBody
	public Map<String, Object> turnDoc(String caller, int ma_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", meetingroomapplyService.turnDoc(ma_id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 重新载入
	 */
	@RequestMapping("/oa/meeting/reLoadMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> reLoadMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.reLoad(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 取消会议
	 */
	@RequestMapping("/oa/meeting/cancelMeetingroomapply.action")
	@ResponseBody
	public Map<String, Object> cancelMeetingroomapply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		meetingroomapplyService.cancel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
