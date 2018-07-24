package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.FeeKindService;

@Controller
public class FeeKindController {
	@Autowired
	private FeeKindService FeeKindService;
	/**
	 * 保存FeeKind
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/fa/fp/saveFeeKind.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FeeKindService.saveFeeKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/fa/fp/updateFeeKind.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FeeKindService.updateFeeKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除FeeKind
	 */
	@RequestMapping("/fa/fp/deleteFeeKind.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FeeKindService.deleteFeeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核FeeKind
	 */
	@RequestMapping("/fa/fp/auditFeeKind.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FeeKindService.auditFeeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核FeeKind
	 */
	@RequestMapping("/fa/fp/resAuditFeeKind.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FeeKindService.resAuditFeeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交FeeKind
	 */
	@RequestMapping("/fa/fp/submitFeeKind.action")  
	@ResponseBody 
	public Map<String, Object> submit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FeeKindService.submitFeeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交FeeKind
	 */
	@RequestMapping("/fa/fp/resSubmitFeeKind.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		FeeKindService.resSubmitFeeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
