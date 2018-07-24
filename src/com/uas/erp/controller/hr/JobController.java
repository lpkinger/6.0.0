package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.JobService;

@Controller
public class JobController {
	@Autowired
	private JobService jobService;

	/**
	 * 保存Job
	 */
	@RequestMapping("/hr/employee/saveJob.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.saveJob(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/employee/updateJob.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.updateJobById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交Job
	 */
	@RequestMapping("/hr/employee/submitJob.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.submitJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提交Job
	 */
	@RequestMapping("/hr/employee/resSubmitJob.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.resSubmitJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核Job
	 */
	@RequestMapping("/hr/employee/auditJob.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.auditJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核Job
	 */
	@RequestMapping("/hr/employee/resAuditJob.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.resAuditJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/hr/employee/deleteJob.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.deleteJob(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取岗位信息
	 * */
	@RequestMapping("/hr/employee/getJobs.action")
	@ResponseBody
	public Map<String,Object> getJobs(String orgid,String isStandard){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("jobs", jobService.getJobs(orgid,isStandard));
		return modelMap;
		
	}
	/*
	 * saas初始化获取岗位信息
	 * 
	 * */
	@RequestMapping("/hr/employee/getSaasJobs.action")
	@ResponseBody
	public Map<String,Object> getSaasJobs(String isStandard){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("data", jobService.getSaasJobs(isStandard));
		return modelMap;
	}
	/*
	 * saas初始化保存岗位信息
	 * 
	 * */
	@RequestMapping("/hr/employee/saveSaasJobs.action")
	@ResponseBody
	public Map<String,Object> saveSaasJobs(String gridStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.saveSaasJobs(gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * saas初始化删除岗位信息
	 * 
	 * */
	@RequestMapping("/hr/employee/deleteSaasJob.action")
	@ResponseBody
	public Map<String,Object> deleteSaasJob(int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		/*int result=*/jobService.deleteSaasJob(id);
		/*if(result==1){
			modelMap.put("exceptionInfo", "该岗位存在相应的人员信息，不允许删除!");
		}
		if(result==2){
			modelMap.put("exceptionInfo", "该岗位有下级岗位，不允许删除！");
		}
		if(result==0){
			modelMap.put("success", true);
		}*/
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * saas初始化重新加载岗位和人员信息
	 * 
	 * */
	@RequestMapping("/hr/employee/loadNewData.action")
	@ResponseBody
	public Map<String,Object> loadNewData(String jo_id){
		Map<String, Object> modelMap = new HashMap<String, Object>(); 
		String[] info=new String[2];
		info=jobService.getInfo(jo_id);
		String emp = jobService.getSaasEmployees(Integer.parseInt(jo_id));
		modelMap.put("success", true);
		modelMap.put("jo_description", info[0]);
		modelMap.put("jo_powerdes", info[1]);
		modelMap.put("jo_name", info[2]);
		modelMap.put("data", emp);
		return modelMap;
	}
	/*
	 * saas初始化更新岗位描述和岗位权限描述
	 * 
	 * */
	@RequestMapping("/hr/employee/updateDescrption.action")
	@ResponseBody
	public Map<String,Object> updateDescrption(String jo_id,String jo_power,String jo_description){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.updateDescription(jo_id,jo_power,jo_description);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @author wsy
	 * 通过条件获取岗位信息
	 */
	@RequestMapping("/hr/employee/getJobByCondition.action")
	@ResponseBody
	public Map<String,Object> getJobsByCondition(String condition){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("jobs", jobService.getJobsByCondition(condition));
		return modelMap;
	}
	/**
	 * 保存岗位
	 * */
	@RequestMapping("/hr/employee/saveJobs.action")
	@ResponseBody
	public Map<String,Object> saveJobs(String jsonData){
		Map<String,Object> modelMap=new HashMap<String,Object>();
	    jobService.saveJobs(jsonData);
	    modelMap.put("success", true);
		return modelMap;
		
	}
	/**
	 * Wsy
	 * 	保存员工
	 */
	@RequestMapping("/hr/employee/saveEmployeess.action")
	@ResponseBody
	public Map<String,Object> saveEmployees(String jsonData,String enUU){
		Map<String,Object> modelMap=new HashMap<String,Object>();
	    jobService.saveEmployees(jsonData,enUU);
	    modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * Wsy 修改员工
	 * */
	@RequestMapping("/hr/employee/updateEmployeess.action")
	@ResponseBody
	public Map<String,Object> updateEmployees(String jsonData){
		Map<String,Object> modelMap=new HashMap<String,Object>();
	    jobService.updateEmployees(jsonData);
	    modelMap.put("success", true);
		return modelMap;
		
	}
	/**
	 * 修改岗位
	 * */
	@RequestMapping("/hr/employee/updateJobs.action")
	@ResponseBody
	public Map<String,Object> updateJobs(String jsonData){
		Map<String,Object> modelMap=new HashMap<String,Object>();
	    jobService.updateJobs(jsonData);
	    modelMap.put("success", true);
		return modelMap;
		
	}
	/**
	 * 获得权限复制的日志
	 * */
	@RequestMapping("/hr/employee/getJobsWithStandard.action")
	@ResponseBody
	public Map<String,Object> getJobsWithStandard(){
		Map<String,Object> modelMap=new HashMap<String,Object>();
	    modelMap.put("jobs",jobService.getJobsWithStandard());
	    modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获得岗位下所有人员
	 * */
	@RequestMapping("/hr/job/getEmployees.action")
	@ResponseBody
	public Map<String, Object> getEmployees(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, String> emp = jobService.getEmployees(id);
		modelMap.put("emp", emp);
		return modelMap;
	}
	
	/**
	 *禁用
	 */
	@RequestMapping("hr/employee/bannedJob.action")
	@ResponseBody 
	public Map<String, Object> bannedJob(String caller,int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.bannedJob(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 *反禁用
	 */
	@RequestMapping("hr/employee/resBannedJob.action")
	@ResponseBody 
	public Map<String, Object> resBannedJob(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobService.resBannedJob(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
