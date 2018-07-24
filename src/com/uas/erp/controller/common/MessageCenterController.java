package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.common.EnterpriseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.MessageCenterService;
@Controller
public class MessageCenterController {

	@Autowired  MessageCenterService messageCenterService;
	@Autowired EnterpriseService enterpriseservice;
	@RequestMapping("common/getInfoCount.action")
	@ResponseBody
	public Map<String, Object> getInfoCount(HttpSession session,String timestr) {	
		Map<String, Object> modelMap = new HashMap<String, Object >();
		Employee employee=(Employee)session.getAttribute("employee");
		modelMap.put("data", messageCenterService.getCount(employee,timestr));
		modelMap.put("success", true);
		modelMap.put("IsRemind", employee.getEm_remind() == 1);
		modelMap.put("DtRemaind", (employee.getEm_dtremind()!=null && employee.getEm_dtremind()==-1));
		modelMap.put("ma_user",enterpriseservice.getMasterByName(employee.getEm_master()).getMa_function());
		return modelMap;
	}
	
	@RequestMapping("common/getMessageData.action")
	@ResponseBody
	public Map<String, Object> getMessageData(HttpSession session,String condition,
									String likestr,Integer page,Integer limit) {	
		Map<String, Object> modelMap = new HashMap<String, Object >();
		Employee employee=(Employee)session.getAttribute("employee");
		List<Map<String, Object>> list = messageCenterService.getMessageData(employee,condition,likestr, page,limit);
		//total是分页需要的字段
		modelMap.put("total", messageCenterService.getMessageTotal(employee, condition, likestr,page, limit));
		modelMap.put("data", list);
		modelMap.put("count",messageCenterService.getmessageCount(employee));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("common/getTaskData.action")
	@ResponseBody
	public Map<String, Object> getTaskData(HttpSession session,String condition, String fields, 
									String likestr,Integer page,Integer limit,String  type) {	
		Map<String, Object> modelMap = new HashMap<String, Object >();
		Employee employee=(Employee)session.getAttribute("employee");
		modelMap =  messageCenterService.getTaskData(employee,condition, fields, likestr, page, limit, type);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("common/getProcessData.action")
	@ResponseBody
	public Map<String, Object> getProcessData(HttpSession session,String type,String fields,String likestr,Integer page,Integer limit) {	
		Map<String, Object> modelMap = new HashMap<String, Object >();
		Employee employee=(Employee)session.getAttribute("employee");
		modelMap =  messageCenterService.getProcessData(employee,type,likestr, page, limit);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("common/getFlowData.action")
	@ResponseBody
	public Map<String, Object> getFlowData(HttpSession session,String type,String fields,String likestr,Integer page,Integer limit) {	
		Map<String, Object> modelMap = new HashMap<String, Object >();
		Employee employee=(Employee)session.getAttribute("employee");
		modelMap =  messageCenterService.getFlowData(employee,type,likestr, page, limit);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("common/getMessageContent.action")
	@ResponseBody
	public Map<String, Object> getMessageContent(HttpSession session,Integer id,String master ){
		
		Map<String, Object> modelMap = new HashMap<String, Object >();
		Employee employee=(Employee)session.getAttribute("employee");
		//modelMap.put("data", messageCenterService.getMessageContent(employee, id, master));
		modelMap.put("success", messageCenterService.getMessageContent(employee, id, master));
		return modelMap;
	}
	@RequestMapping("common/updateReadstatus.action")
	@ResponseBody
	public Map<String, Object> updateReadstatus(String data){
		Map<String, Object> modelMap = new HashMap<String, Object >();
		modelMap.put("success",messageCenterService.updateReadstatus(data));
		return modelMap;
	}
	@RequestMapping("common/isadmin.action")
	@ResponseBody
	public Map<String, Object> getFieldData(HttpSession session, String field, String table) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		String condition="em_id = "+employee.getEm_id();
		modelMap.put("data", messageCenterService.getFieldData(table, field, condition));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("common/isVirtual.action")
	@ResponseBody
	public Map<String, Object> isVirtual(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		if(employee.getEm_type().equals("admin")){
			modelMap.put("success", true);
		}else{
			if(employee.getEm_class().equals("admin_virtual")){
				modelMap.put("success", true);
			}else{
				modelMap.put("success", false);
			}
		}
		return modelMap;
	}
	@RequestMapping("common/searchdata.action")
	@ResponseBody
	public Map<String, Object> searchData(HttpSession session, String condition, String type,String filed) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");		
		modelMap.put("data", messageCenterService.searchData(employee, condition, type,filed));
		modelMap.put("success", true);
		return modelMap;
	}

}
