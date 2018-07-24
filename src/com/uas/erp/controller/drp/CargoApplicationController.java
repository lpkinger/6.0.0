package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.drp.CargoApplicationService;

@Controller
public class CargoApplicationController {
	@Autowired
	private CargoApplicationService cargoApplicationService;
	
	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/distribution/saveCargoApplication.action")
	@ResponseBody
	public Map<String, Object> save( String formStore, String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationService.saveCargoApplication(formStore, param,  caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/drp/distribution/updateCargoApplication.action")
	@ResponseBody
	public Map<String, Object> update( String formStore,String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationService.updateCargoApplicationById(formStore, param,  caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/drp/distribution/deleteCargoApplication.action")
	@ResponseBody
	public Map<String, Object> delete( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationService.deleteCargoApplication(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitCargoApplication.action")
	@ResponseBody
	public Map<String, Object> submitCargoApplicationCu( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationService.submitCargoApplication(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitCargoApplication.action")
	@ResponseBody
	public Map<String, Object> resSubmitCargoApplicationCu( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationService.resSubmitCargoApplication(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditCargoApplication.action")
	@ResponseBody
	public Map<String, Object> auditCargoApplicationCu( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationService.auditCargoApplication(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditCargoApplication.action")
	@ResponseBody
	public Map<String, Object> resAuditCargoApplicationCu( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationService.resAuditCargoApplication(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转借货出库
	 */
	@RequestMapping("/drp/distribution/turnFXSale.action")  
	@ResponseBody 
	public Map<String, Object> turnFXSale( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int sa_id=cargoApplicationService.turnFXSale(id,  caller);
		modelMap.put("success", true);
		modelMap.put("id", sa_id);
		return modelMap;
	}
}
