package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.FittingBomService;

@Controller
public class FittingBomController extends BaseController {
	@Autowired
	private FittingBomService fittingBomService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveFittingBom.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fittingBomService.saveFittingBom(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteFittingBom.action")  
	@ResponseBody 
	public Map<String, Object> deleteFittingBOM(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fittingBomService.deleteFittingBom(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateFittingBom.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fittingBomService.updateFittingBomById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitFittingBom.action")  
	@ResponseBody 
	public Map<String, Object> submitFittingBom(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fittingBomService.submitFittingBom(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitFittingBom.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitFittingBom(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fittingBomService.resSubmitFittingBom(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditFittingBom.action")  
	@ResponseBody 
	public Map<String, Object> auditFittingBom(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fittingBomService.auditFittingBom(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditFittingBom.action")  
	@ResponseBody 
	public Map<String, Object> resAuditFittingBom(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fittingBomService.resAuditFittingBom(id);
		modelMap.put("success", true);
		return modelMap;
	}	
}
