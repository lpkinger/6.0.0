package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.common.FilePathService;
import com.uas.erp.service.plm.ProjectGanttTaskService;

@Controller
public class ProjectGanttTaskController {
	@Autowired
	private ProjectGanttTaskService projectGanttTaskService;
	@Autowired
	private FilePathService filePathService;

	@RequestMapping("/plm/main/saveProjectGanttTask.action")
	@ResponseBody
	public Map<String, Object> saveProjectGanttTask(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.saveProjectGanttTask(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/deleteProjectGanttTask.action")
	@ResponseBody
	public Map<String, Object> deleteProjectGanttTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.deleteProjectGanttTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/main/updateProjectGanttTask.action")
	@ResponseBody
	public Map<String, Object> updateProjectGanttTask(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.updateProjectGanttTask(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/submitProjectGanttTask.action")
	@ResponseBody
	public Map<String, Object> submitProjectGanttTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.submitProjectGanttTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/resSubmitProjectGanttTask.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjectGanttTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.resSubmitProjectGanttTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/auditProjectGanttTask.action")
	@ResponseBody
	public Map<String, Object> auditProjectGanttTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.auditProjectGanttTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/main/resAuditProjectGanttTask.action")
	@ResponseBody
	public Map<String, Object> resAuditProjectGanttTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.resAuditProjectGanttTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/gantt/end.action")
	@ResponseBody
	public Map<String, Object> End(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.End(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("plm/gantt/resEnd.action")
	@ResponseBody
	public Map<String, Object> resEnd(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.resEnd(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转任务执行单
	 * */
	@RequestMapping("plm/gantt/TurnTask.action")
	@ResponseBody
	public Map<String, Object> TurnTask(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.TurnTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 加载任务节点
	 * */
	@RequestMapping("plm/gantt/LoadTaskNode.action")
	@ResponseBody
	public Map<String, Object> LoadTaskNode(HttpSession session, int id, String type, String caller,String startdate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectGanttTaskService.LoadTaskNode(id, type, caller,startdate);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取任务完成情况
	 * */
	@RequestMapping("plm/gantt/getTaskCompletion.action")
	@ResponseBody
	public Map<String, Object> getTaskCompletion(HttpSession session, Integer taskId, Integer resourceEmpId) {
		Map<String, Object> modelMap = projectGanttTaskService.getTaskCompletion(taskId,resourceEmpId);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 根据项目id取任务的id
	 * */
	@RequestMapping("plm/gantt/getPreTask.action")
	@ResponseBody
	public Map<String, Object> getMainTaskId(HttpSession session, Integer id, String caller) {
		Map<String, Object> modelMap =  new HashMap<String, Object>();
		projectGanttTaskService.getPreTask(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
