package com.uas.mobile.controller.crm;

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
import com.uas.mobile.service.CustomerService;
import com.uas.mobile.service.SignCardLogService;

@Controller("mobileCustomerController")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	@Autowired
	private SignCardLogService signCardLogService;

	// 获取客户编号对应的商机编号
	@RequestMapping("/mobile/crm/getnichecodes.action")
	@ResponseBody
	public Map<String, Object> getNichecodes(HttpServletRequest request,
			String cu_code, int page, int pageSize, String custname) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		int end = page * pageSize;
		int start = end - pageSize;
		modelMap.put("businesschance",
				customerService.getNichecode(cu_code, start, end, custname));
		return modelMap;
	}

	// 关联已有客户
	@RequestMapping("/mobile/crm/getCustomerbySeller.action")
	@ResponseBody
	public Map<String, Object> getCustomerbySeller(HttpServletRequest request,
			String sellercode) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("customers",
				customerService.getCustomerbySeller(sellercode));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 客户资料界面接口
	// type 0-未成交，1-已成交，2-全部
	@RequestMapping("/mobile/crm/getCustomerDetail.action")
	@ResponseBody
	public Map<String, Object> getCustomerDetail(HttpServletRequest request,
			String emcode, int page, int pageSize, Integer type, Integer kind,
			int isSelected, String emplist) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * pageSize;
		end = page * pageSize;
		if (type == null)
			type = 2;
		modelMap.put("customers", customerService.getCustomerDetail(emcode,
				start, end, 2, kind, isSelected, emplist));
		modelMap.put("customers0", customerService.getCustomerDetail(emcode,
				start, end, 0, kind, isSelected, emplist));
		modelMap.put("customers1", customerService.getCustomerDetail(emcode,
				start, end, 1, kind, isSelected, emplist));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	@RequestMapping("/mobile/crm/getCustomerbycode.action")
	@ResponseBody
	public Map<String, Object> getCustomerbycode(HttpServletRequest request,
			String cu_code) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("customer", customerService.getCustomerbycode(cu_code));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 客户详情界面获取商机个数及商机阶段，拜访次数
	@RequestMapping("/mobile/crm/getDatasbycode.action")
	@ResponseBody
	public Map<String, Object> getDatasbycode(HttpServletRequest request,
			String custcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = customerService.getDatasbycode(custcode);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 保存拜访计划
	@RequestMapping("/mobile/crm/saveVisitPlan.action")
	@ResponseBody
	public Map<String, Object> saveVisitPlan(HttpServletRequest request,
			String formStore, String caller) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerService.saveVisitPlan(formStore, caller);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 获取拜访计划
	 */
	@RequestMapping("/mobile/crm/getVisitPlan.action")
	@ResponseBody
	public Map<String, Object> getVisitPlan(HttpServletRequest request,
			String emcode, String date, int page, int pageSize) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * pageSize + 1;
		end = page * pageSize;
		List<Map<String, Object>> list = customerService.getVisitPlan(employee,
				date, start, end);
		modelMap.put("success", true);
		modelMap.put("visitplan", list);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 获取拜访类型
	 */
	@RequestMapping("/mobile/crm/getVisitType.action")
	@ResponseBody
	public Map<String, Object> getVisitType(HttpServletRequest request,
			String custname, String custcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = customerService.getVisitType(custname,
				custcode);
		modelMap.put("success", true);
		modelMap.put("visittype", list);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 获取任务信息
	 */
	@RequestMapping("/mobile/crm/getTaskMsg.action")
	@ResponseBody
	public Map<String, Object> getTaskMsg(HttpServletRequest request,
			String emcode, String date, String status) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = customerService.getTaskPlan(emcode,
				date, status);
		modelMap.put("success", true);
		modelMap.put("taskMsg", list);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 获取日程信息
	 */
	@RequestMapping("/mobile/crm/getScheduleMsg.action")
	@ResponseBody
	public Map<String, Object> getScheduleMsg(HttpServletRequest request,
			String emcode, String date, String status) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("scheduleMsg",
				customerService.getScheduleMsg(emcode, date, status));
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 获取任务日程作息type-区分日程和任务
	 */
	@RequestMapping("/mobile/crm/getTaskAndScheduleMsg.action")
	@ResponseBody
	public Map<String, Object> getTaskAndScheduleMsg(
			HttpServletRequest request, String emcode, String date, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> map = customerService.getTaskAndScheduleMsg(emcode,
				date, type);
		modelMap.put("success", true);
		modelMap.put("taskAndScheMsg", map);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 任务日程及拜访计划
	@RequestMapping("/mobile/crm/gettaskscheduleandvisitplanmsg.action")
	@ResponseBody
	public Map<String, Object> getTaskAndScheduleAndVisitPlanMsg(
			HttpServletRequest request, String emcode, String date) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = customerService.getTaskAndScheduleAndVisitPlanMsg(emcode,
				date);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 荣誉墙
	@RequestMapping("/mobile/crm/getRankList.action")
	@ResponseBody
	public Map<String, Object> getRankList(HttpServletRequest request,
			String condition) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("ranklist",
				customerService.getRankList(condition, employee));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// CRM首页
	/*
	 * 个人排名
	 */
	@RequestMapping("/mobile/crm/getPersonalRank.action")
	@ResponseBody
	public Map<String, Object> getPersonalRank(HttpServletRequest request,
			String emcode, String yearmonth) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("datas",
				customerService.getPersonalRank(emcode, yearmonth, employee));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 本月销售简报
	 */
	@RequestMapping("/mobile/crm/getSalesKit.action")
	@ResponseBody
	public Map<String, Object> getSalesKit(HttpServletRequest request,
			String emcode, String yearmonth) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("datas",
				customerService.getSalesKit(emcode, yearmonth, employee));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 我的下属和我的指标
	 */
	@RequestMapping("/mobile/crm/getTargets.action")
	@ResponseBody
	public Map<String, Object> getTargets(HttpServletRequest request,
			String emcode, String yearmonth, int page, int pageSize) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * pageSize + 1;
		end = page * pageSize;
		modelMap.put("datas", customerService.getTargets(emcode, yearmonth,
				start, end, employee));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	/*
	 * 被遗忘的客户接口
	 */
	@RequestMapping("/mobile/crm/getInactionCusts.action")
	@ResponseBody
	public Map<String, Object> getInactionCusts(HttpServletRequest request,
			String emcode, int page, int pageSize) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * pageSize + 1;
		end = page * pageSize;
		modelMap.put("datas",
				customerService.getInactionCusts(emcode, start, end, employee));
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 结束会议
	@RequestMapping("/mobile/crm/updateMeeting.action")
	@ResponseBody
	public String updateMeeting(HttpServletRequest request, int ma_id) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		String result = customerService.updateMeeting(ma_id);

		return result;
	}

	// 更新拜访计划状态
	@RequestMapping("/mobile/crm/updateVistPlan.action")
	@ResponseBody
	public Map<String, Object> updateVistPlan(HttpServletRequest request,
			int vp_id, String cu_nichestep, String cu_code, String nichecode,
			int vr_id) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String result = customerService.updateVistPlan(vp_id, cu_nichestep,
				cu_code, nichecode, vr_id);
		modelMap.put("result", result);
		modelMap.put("sessionId", request.getSession().getId());

		return modelMap;
	}

	// 更新会议申请的会议纪要状态
	@RequestMapping("/mobile/crm/updateMatype.action")
	@ResponseBody
	public Map<String, Object> updateMatype(HttpServletRequest request,
			String ma_code) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String result = customerService.updateMatype(ma_code);
		modelMap.put("result", result);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 获取下属信息
	@RequestMapping("/mobile/crm/getstaffmsg.action")
	@ResponseBody
	public Map<String, Object> getStaffMsg(HttpServletRequest request,
			String emcode) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("status", 0);
		modelMap.put("datas", customerService.getStaffMsg(emcode));
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 获取直属领导
	@RequestMapping("/mobile/crm/getheadmanmsg.action")
	@ResponseBody
	public Map<String, Object> getheadmanmsg(HttpServletRequest request,
			String emcode) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("status", 0);
		modelMap.put("data", customerService.getheadmanmsg(emcode));
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 判断是否启用客户预录入
	@RequestMapping("/mobile/crm/ifuseprecustomer.action")
	@ResponseBody
	public Map<String, Object> ifUsePreCustomer(HttpServletRequest request,
			String currentsystem, String caller, String code) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("isStart", customerService.ifConfigs(caller, code) ? "1"
				: "0");
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 判断是否超出最大商机数
	@RequestMapping("/mobile/crm/ifoverrecv.action")
	@ResponseBody
	public Map<String, Object> ifOverRecv(HttpServletRequest request,
			String emcode) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("isok", customerService.ifOverRecv(emcode) ? "0" : "1");
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 判断是否是商机库管理员
	@RequestMapping("/mobile/crm/ifbusinessdatabaseadmin.action")
	@ResponseBody
	public Map<String, Object> ifBusinessDataBaseAdmin(
			HttpServletRequest request, String emcode) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		return customerService.ifBusinessDataBaseAdmin(emcode);
	}

	// 判断是否开通APP新版本功能
	@RequestMapping("/mobile/crm/openNewVision.action")
	@ResponseBody
	public Map<String, Object> openNewVision(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("isopen", customerService.openVersion());
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 更新日程状态
	@RequestMapping("/mobile/crm/updateSchedule.action")
	@ResponseBody
	public Map<String, Object> updateSchedule(HttpServletRequest request,
			String code) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		String result = customerService.updateSchedule(code);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", result);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 根据来源单号和执行人编号获取日程
	// 更新日程状态
	@RequestMapping("/mobile/crm/getSchedule.action")
	@ResponseBody
	public Map<String, Object> getSchedule(HttpServletRequest request,
			String bccode, String emname) {
		System.out.println("dddddd");
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result", customerService.getSchedule(bccode, emname));
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 获取联系人
	@RequestMapping("/mobile/crm/getContactPerson.action")
	@ResponseBody
	public Map<String, Object> getContactPerson(HttpServletRequest request,
			String condition, int page, int size) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");

		Map<String, Object> modelMap = new HashMap<String, Object>();
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * size + 1;
		end = page * size;
		List<Map<String, Object>> list = customerService.getContactPerson(
				condition, start, end);
		modelMap.put("success", true);
		modelMap.put("datalist", list);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	// 获取客户商机描述
	@RequestMapping("/mobile/crm/getBusinesschanceBewrite.action")
	@ResponseBody
	public Map<String, Object> getBusinesschanceBewrite(
			HttpServletRequest request, String custcode, String custname,
			int page) {
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * 20 + 1;
		end = page * 20;
		List<Map<String, Object>> list = customerService
				.getBusinesschanceBewrite(custcode, custname, start, end);
		modelMap.put("success", true);
		modelMap.put("datalist", list);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}
	
	// 查找客户
	@RequestMapping("/mobile/crm/searchCustomer.action")
	@ResponseBody
	public Map<String, Object> searchCustomer(
			HttpServletRequest request, String likestr,
			int page,int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int start = 0;
		int end = 0;
		if (page == 0)
			page = 1;
		start = (page - 1) * pageSize + 1;
		end = page * pageSize;
		List<String> list = customerService.searchCustomer(
				likestr, start, end);
		modelMap.put("success", true);
		modelMap.put("datas", list);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;		
	}	
}
