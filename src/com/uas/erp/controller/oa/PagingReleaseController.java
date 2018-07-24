package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.service.oa.PagingReleaseService;

@Controller
public class PagingReleaseController {
	@Autowired
	private PagingReleaseService pagingReleaseService;

	/**
	 * 发送寻呼
	 * 
	 * @param formStore
	 */
	@RequestMapping("oa/info/sendPagingRelease.action")
	@ResponseBody
	public Map<String, Object> send(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pagingReleaseService.save(formStore, SystemSession.getUser());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 阅读寻呼时，修改状态 保留寻呼操作时，修改寻呼状态
	 * 
	 * @param formStore
	 */
	@RequestMapping("oa/info/updateStatus.action")
	@ResponseBody
	public Map<String, Object> updateStatus(String caller, int id, int status, String master) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pagingReleaseService.updateStatus(id, status, master);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 接收寻呼
	 */
	@RequestMapping("oa/info/getPagingRelease.action")
	@ResponseBody
	public Map<String, Object> getPaging(HttpSession session, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		modelMap.put("data", pagingReleaseService.getPaging(employee));
		modelMap.put("IsRemind", employee.getEm_remind() == 1);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取当前组织下所有员工的在线状态
	 */
	@RequestMapping("oa/info/checkOnline.action")
	@ResponseBody
	public Map<String, Object> checkOnline(String caller, int orgid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", pagingReleaseService.getOnlineEmployeeByOrg(orgid));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 已查阅寻呼转入寻呼历史表，加速检索
	 *//*
	@RequestMapping("oa/info/turnHistory.action")
	@ResponseBody
	public Map<String, Object> turnHistory(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", pagingReleaseService.turnToHoitory());
		modelMap.put("success", true);
		return modelMap;
	}
*/
	/**
	 * 已查阅寻呼转入寻呼历史表，加速检索
	 */
	@RequestMapping("oa/info/confirmNotifyJprocess.action")
	@ResponseBody
	public Map<String, Object> confirmNotifyJprocess(String caller, int id, String source) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", pagingReleaseService.confirmNotifyJprocess(id, source));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("oa/info/receive.action")
	@ResponseBody
	public ModelAndView receive(Integer _em, Integer id) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (_em != null)
			params.put("_em", _em);
		params.put("data", pagingReleaseService.getPagingById(id));
		return new ModelAndView("oa/info/pagingget", params);
	}

	/**
	 * 获取人员信息
	 * */
	@RequestMapping("/oa/info/getUsersIsOnline.action")
	@ResponseBody
	public Map<String, Object> getOnlineList(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("emps", pagingReleaseService.getUsersIsOnline());
		return modelMap;
	}

	/**
	 * 发送消息
	 * */
	@RequestMapping("/oa/info/paging.action")
	@ResponseBody
	public Map<String, Object> SendMsg(HttpSession session, String mans, String title, String context) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pagingReleaseService.pagingRelease(mans, title, context);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 平台产生的消息
	 * */
	@RequestMapping("/oa/info/b2bmsg.action")
	@ResponseBody
	public Map<String, Object> B2BMsg(String caller,String ids,String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pagingReleaseService.B2BMsg(caller, ids, type);
		modelMap.put("success", true);
		return modelMap;
	}
}
