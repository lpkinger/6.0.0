package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PurcRemarkService;

@Controller
public class PurcRemarkController extends BaseController {
	@Autowired
	private PurcRemarkService purcRemarkService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/savePurcRemark.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcRemarkService.savePurcRemark(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deletePurcRemark.action")  
	@ResponseBody 
	public Map<String, Object> deletePurcRemark(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcRemarkService.deletePurcRemark(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updatePurcRemark.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcRemarkService.updatePurcRemarkById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/purchase/bannedPurcRemark.action")
	@ResponseBody
	public Map<String, Object> bannedPurcRemark(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcRemarkService.bannedPurcRemark(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/purchase/resBannedPurcRemark.action")
	@ResponseBody
	public Map<String, Object> resBannedPurcRemark(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcRemarkService.resBannedPurcRemark(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
