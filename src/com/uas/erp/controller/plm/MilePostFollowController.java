package com.uas.erp.controller.plm;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.MilePostFollowService;

@Controller
public class MilePostFollowController extends BaseController {
	@Autowired
	private MilePostFollowService milePostFollowService;
	/**
	 * 保存
	 */
	@RequestMapping("/plm/task/saveMilePostFollow.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		milePostFollowService.saveMilePostFollow(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/plm/task/updateMilePostFollow.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		milePostFollowService.updateMilePostFollowById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/plm/task/deleteMilePostFollow.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		milePostFollowService.deleteMilePostFollow(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/plm/task/submitMilePostFollow.action")
	@ResponseBody
	public Map<String, Object> submitMilePostFollow(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		milePostFollowService.submitMilePostFollow(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/plm/task/resSubmitMilePostFollow.action")
	@ResponseBody
	public Map<String, Object> resSubmitMilePostFollow(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		milePostFollowService.resSubmitMilePostFollow(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/plm/task/auditMilePostFollow.action")  
	@ResponseBody 
	public Map<String, Object> auditMilePostFollow(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		milePostFollowService.auditMilePostFollow(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/plm/task/resAuditMilePostFollow.action")
	@ResponseBody
	public Map<String, Object> resAuditMilePostFollow(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		milePostFollowService.resAuditMilePostFollow(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

