package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProdRelationService;

@Controller
public class ProdRelationController extends BaseController {

	@Autowired
	private ProdRelationService prodRelationService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveProdRelation.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodRelationService.saveProdRelation(formStore, param, caller);
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
	@RequestMapping("/pm/bom/updateProdRelation.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodRelationService.updateProdRelation(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/deleteProdRelation.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodRelationService.deleteProdRelation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/submitProdRelation.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, Integer id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodRelationService.submitProdRelation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/resSubmitProdRelation.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodRelationService.resSubmitProdRelation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/auditProdRelation.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, Integer id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodRelationService.auditProdRelation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核grid数据
	 * 
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/resAuditProdRelation.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodRelationService.resAuditProdRelation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用
	 */
	@RequestMapping("/pm/bom/bannedProdRelation.action")
	@ResponseBody
	public Map<String, Object> bannedProdRelation(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = prodRelationService.bannedProdRelation(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
