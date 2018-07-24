package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ECRDOCService;

@Controller
public class ECRDOCController extends BaseController {
	@Autowired
	private ECRDOCService ECRDOCService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveECRDOC.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDOCService.saveECRDOC(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECR数据 包括ECR明细
	 */
	@RequestMapping("/pm/bom/deleteECRDOC.action")
	@ResponseBody
	public Map<String, Object> deleteECRDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDOCService.deleteECRDOC(id, caller);
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
	@RequestMapping("/pm/bom/updateECRDOC.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDOCService.updateECRDOCById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交ECR
	 */
	@RequestMapping("/pm/bom/submitECRDOC.action")
	@ResponseBody
	public Map<String, Object> submitECRDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDOCService.submitECRDOC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交ECR
	 */
	@RequestMapping("/pm/bom/resSubmitECRDOC.action")
	@ResponseBody
	public Map<String, Object> resSubmitECRDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDOCService.resSubmitECRDOC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核ECR
	 */
	@RequestMapping("/pm/bom/auditECRDOC.action")
	@ResponseBody
	public Map<String, Object> auditECR(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDOCService.auditECRDOC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核ECR
	 */
	@RequestMapping("/pm/bom/resAuditECRDOC.action")
	@ResponseBody
	public Map<String, Object> resAuditECRDOC(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDOCService.resAuditECRDOC(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
