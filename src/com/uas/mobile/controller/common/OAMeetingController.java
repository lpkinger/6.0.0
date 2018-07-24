package com.uas.mobile.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Employee;
import com.uas.mobile.service.OAMeetingService;

@Controller("mobileOAMeetingController")
public class OAMeetingController {

	@Autowired
	private OAMeetingService oaMeetingService;
	
	//保存和提交公用接口
	@RequestMapping("/mobile/oa/commonSaveAndSubmit.action")  
	@ResponseBody 
	public Map<String, Object> commonSaveAndSubmit(HttpServletRequest request,String caller, String formStore,String gridStore) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = oaMeetingService.commonSaveAndSubmit(caller,formStore,gridStore,employee.getEm_code(),employee.getEm_name());
		modelMap.put("success", true);
		return modelMap;
	}
	
	//保存和提交请假单
	@RequestMapping("/mobile/oa/saveAndSubmitAskLeave.action")  
	@ResponseBody 
	public Map<String, Object> saveAndSubmit(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = oaMeetingService.saveAndSubmitAskLeave(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//保存和提交补卡申请单
	@RequestMapping("/mobile/oa/saveAndSubmitMobileSignCard.action")  
	@ResponseBody 
	public Map<String, Object> saveAndSubmitMobileSignCard(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = oaMeetingService.saveAndSubmitMobileSignCard(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	//保存和提交费用报销单接口
	@RequestMapping("/mobile/oa/saveAndSubmitFYBX.action")  
	@ResponseBody 
	public Map<String, Object> saveAndSubmitFYBX(HttpServletRequest request, String formStore, String param, String param2, String caller) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = oaMeetingService.saveAndSubmitFYBX(caller, formStore, param, param2,employee.getEm_code(),employee.getEm_name());
		modelMap.put("success", true);
		return modelMap;
	}
	//费用报销单更新提交接口
		@RequestMapping("/mobile/oa/UpdateSubmitFYBX.action")
		@ResponseBody
		public Map<String,Object> UpdateSubmitFYBX(HttpServletRequest request,String caller,String formStore,String param1,String param2){			
			Employee employee=(Employee)request.getSession().getAttribute("employee");
			if(employee==null) BaseUtil.showError("会话已断开!");
			Map<String,Object> modelMap=new HashMap<String,Object>();
			modelMap=oaMeetingService.UpdateSubmitFYBX(caller,formStore,param1,param2);
			modelMap.put("success", true);
			return modelMap;
		}
	//获取会议明细签到未签到人员
	@RequestMapping("/mobile/crm/getMeetingDetailParticipants.action")
	@ResponseBody
	public Map<String, Object> getMeetingDetailParticipants(HttpServletRequest request,String ma_code) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("participants", oaMeetingService.getMeetingDetailParticipants(ma_code));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	/*
	 * 移动端会议签到
	 */
	@RequestMapping("/mobile/oa/meeting/meetingSignInMobile.action")
	@ResponseBody
	public Map<String,Object> meetingSignMobile(HttpServletRequest request,String em_code,String ma_code,String caller){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaMeetingService.meetingSignMobile(em_code,ma_code, caller);
		modelMap.put("success",true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	//外勤打卡
	@RequestMapping("/mobile/oa/saveOutSign.action")
	@ResponseBody
	public Map<String, Object> saveOutSign(HttpServletRequest request,String formStore, String caller) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaMeetingService.saveOutSign(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	//获取打卡列表
	@RequestMapping("/mobile/oa/workdata.action")
	@ResponseBody
	public Map<String, Object> workdata(HttpServletRequest request,String condition) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		list=oaMeetingService.workdata(condition);
		modelMap.put("listdata", list);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	//获取app菜单配置
	@RequestMapping("/mobile/oa/getmenuconfig.action")
	@ResponseBody
	public Map<String, Object> getMenuConfig(HttpServletRequest request,String condition) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		list=oaMeetingService.getMenuConfig(condition);
		modelMap.put("listdata", list);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	//OA表单，出差加班请假caller配置接口
	@RequestMapping("/mobile/oa/getoaconifg.action")
	@ResponseBody
	public Map<String, Object> getoaconifg(HttpServletRequest request) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = null;
		list=oaMeetingService.getoaconifg();
		modelMap.put("listdata", list);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}	
}
