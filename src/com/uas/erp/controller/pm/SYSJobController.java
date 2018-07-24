package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.SYSJobService;

@Controller
public class SYSJobController extends BaseController {

	@Autowired
	private SYSJobService SYSJobService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mps/saveSYSJob.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.saveSYSJob(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mps/deleteSYSJob.action")
	@ResponseBody
	public Map<String, Object> deleteSYSJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.deleteSYSJob(id, caller);
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
	@RequestMapping("/pm/mps/updateSYSJob.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.updateSYSJobById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mps/submitSYSJob.action")
	@ResponseBody
	public Map<String, Object> submitSYSJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.submitSYSJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mps/resSubmitSYSJob.action")
	@ResponseBody
	public Map<String, Object> resSubmitSYSJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.resSubmitSYSJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mps/auditSYSJob.action")
	@ResponseBody
	public Map<String, Object> auditSYSJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.auditSYSJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mps/resAuditSYSJob.action")
	@ResponseBody
	public Map<String, Object> resAuditSYSJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.resAuditSYSJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 测试 创建任务
	 */
	@RequestMapping("/pm/mps/testOracleJob.action")
	@ResponseBody
	public Map<String, Object> testOracleJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.testOracleJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 立即执行任务
	 */
	@RequestMapping("/pm/mps/runOracleJob.action")
	@ResponseBody
	public Map<String, Object> runOracleJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.runOracleJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 启用任务
	 */
	@RequestMapping("/pm/mps/enableOracleJob.action")
	@ResponseBody
	public Map<String, Object> enableOracleJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.enableOracleJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用任务
	 */
	@RequestMapping("/pm/mps/stopOracleJob.action")
	@ResponseBody
	public Map<String, Object> stopOracleJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SYSJobService.stopOracleJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取JOB
	 */
	@RequestMapping("/pm/mps/getOracleJob.action")
	@ResponseBody
	public Map<String, Object> getOracleJob(
			int start,
			int limit,
			int page) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("data", SYSJobService.getOracleJob(start,limit*page));
		modelMap.put("totalCount", SYSJobService.getCountOracleJob());		
		return modelMap;
	}
	
}
