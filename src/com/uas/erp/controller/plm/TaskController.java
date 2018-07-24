package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.support.MobileSessionContext;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Task;
import com.uas.erp.service.plm.TaskService;

@Controller
public class TaskController extends BaseController {
	@Autowired
	private TaskService taskService;

	@RequestMapping("/plm/task/saveTask.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		String lauguage = (String) session.getAttribute("lauguage");
		taskService.saveTask(formStore, param, lauguage, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/addbilltask.action")
	@ResponseBody
	public Map<String, Object> saveBilltask(HttpServletRequest request,HttpSession session, String formStore, String sessionId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// Employee employee = (Employee) session.getAttribute("employee");
		Employee employee = session.getAttribute("employee") != null ? (Employee) session.getAttribute("employee")
				: (Employee) MobileSessionContext.getInstance().getSessionById(sessionId).getAttribute("employee");
		String lauguage = (String) session.getAttribute("lauguage");
		System.out.println("lauguage="+lauguage);
		taskService.saveBillTask(formStore, lauguage, employee);
		modelMap.put("success", true);
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	@RequestMapping("/plm/task/deleteTask.action")
	@ResponseBody
	public Map<String, Object> deleteTask(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.deleteTask(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/deleteDetail.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.deleteDetail(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/updateTask.action")
	@ResponseBody
	public Map<String, Object> updateTask(HttpSession session, String formStore, String param) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.updateTaskById(formStore, param, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/submitTask.action")
	@ResponseBody
	public Map<String, Object> submitTask(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.submitTask(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/resSubmitTask.action")
	@ResponseBody
	public Map<String, Object> resSubmitTask(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.resSubmitTask(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/auditTask.action")
	@ResponseBody
	public Map<String, Object> auditTask(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.auditTask(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/GetTeammember.action")
	@ResponseBody
	public Map<String, Object> GetTeammember(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", taskService.getJSONMember(id, language));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/addTask.action")
	@ResponseBody
	public Map<String, Object> insert(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		String lauguage = (String) session.getAttribute("lauguage");
		taskService.insertTask(formStore, param, employee, lauguage);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/updateTaskName.action")
	@ResponseBody
	public Map<String, Object> updateTaskName(HttpSession session, String name, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.updateTaskName(name, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/task/getTask.action")
	@ResponseBody
	public Map<String, Object> get(HttpSession session, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Task task = taskService.getTaskByCode(code);
		modelMap.put("task", task);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/plm/task/vastReSubmitActive.action")
	@ResponseBody
	public Map<String, Object> vastReSubmitActive(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.vastReSubmitActive(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/plm/task/getTaskInfo.action")
	@ResponseBody
	public Map<String, Object> getTaskInfo(HttpSession session, int taskId) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("info", taskService.getTaskInfo(language, employee, taskId));
		modelMap.put("success", true);
		return modelMap;
	}

	/*
	 * 工作日程安排
	 */
	@RequestMapping("plm/task/saveAgenda.action")
	@ResponseBody
	public Map<String, Object> saveAgenda(String formStore) throws Exception {
		return taskService.saveAgenda(formStore);
	}

	@RequestMapping("plm/task/deleteAgenda.action")
	@ResponseBody
	public Map<String, Object> deleteAgenda(int id) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.deleteAgenda(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/task/updateAgenda.action")
	@ResponseBody
	public Map<String, Object> updateAgenda(String formStore) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.updateAgenda(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 工作日程查看
	 * */
	@RequestMapping("plm/task/getAgendaData.action")
	@ResponseBody
	public Map<String, Object> getAgendaData(String emcode, String condition) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("evts", taskService.getMyAgenda(emcode, condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存 Form任务
	 * */
	@RequestMapping("plm/task/saveFormTask.action")
	@ResponseBody
	public String saveFormTask(String formStore, String caller, String codeValue) {
		taskService.saveFormTask(formStore, caller, codeValue);
		return "success";
	}

	/**
	 * 修改Form任务
	 * */
	@RequestMapping("plm/task/updateFormTask.action")
	@ResponseBody
	public String updateFormTask(String formStore) {
		taskService.updateFormTask(formStore);
		return "success";
	}

	/**
	 * 获取表单关联任务
	 * */
	@RequestMapping("plm/task/getFormTasks.action")
	@ResponseBody
	public Map<String, Object> getFormTasks(String caller, String codevalue) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tasks", taskService.getFormTasks(caller, codevalue));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 通过知会消息 添加日程事务
	 * */
	@RequestMapping("plm/task/addScheduleTask.action")
	@ResponseBody
	public Map<String, Object> addScheduleTask(String title, String context) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.addScheduleTask(title, context);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 添加任务前置/后置条件
	 * */
	@RequestMapping("plm/task/saveTaskInterceptor.action")
	@ResponseBody
	public Map<String, Object> saveTaskInterceptor(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.saveTaskInterceptor(data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 检测任务前置/后置条件
	 * */
	@RequestMapping("plm/task/checkTaskInterceptor.action")
	@ResponseBody
	public Map<String, Object> checkTaskInterceptor(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.checkTaskInterceptor(data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除任务前置/后置条件
	 * */
	@RequestMapping("plm/task/deleteTaskInterceptor.action")
	@ResponseBody
	public Map<String, Object> deleteTaskInterceptor(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskService.deleteTaskInterceptor(data);
		modelMap.put("success", true);
		return modelMap;
	}
}
