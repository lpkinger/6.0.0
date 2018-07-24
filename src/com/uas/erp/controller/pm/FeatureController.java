package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.FeatureService;

@Controller
public class FeatureController {

	@Autowired
	private FeatureService featureService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/pm/bom/saveFeature.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.saveFeature(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/pm/bom/updateFeature.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.updateFeatureById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/bom/deleteFeature.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.deleteFeature(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交BOM
	 */
	@RequestMapping("/pm/bom/submitFeature.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.submitFeature(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交BOM
	 */
	@RequestMapping("/pm/bom/resSubmitFeature.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.resSubmitFeature(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核BOM
	 */
	@RequestMapping("/pm/bom/auditFeature.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.auditFeature(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核BOM
	 */
	@RequestMapping("/pm/bom/resAuditFeature.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.resAuditFeature(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/pm/bom/updateFeatureName.action")
	@ResponseBody
	public Map<String, Object> updateName(String caller, int id, String name) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("flag",
				featureService.updateFeatureNameById(id, name, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 增加明细
	 */
	@RequestMapping("/pm/bom/addFeatureDetail.action")
	@ResponseBody
	public Map<String, Object> addDetail(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.addFeatureDetail(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 增加约束
	 */
	@RequestMapping("/pm/bom/addFeatureRelation.action")
	@ResponseBody
	public Map<String, Object> addRelation(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.addFeatureRelation(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改明细备注
	 */
	@RequestMapping("/pm/bom/updateRemark.action")
	@ResponseBody
	public Map<String, Object> updateRemark(String caller, String remark, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.updateRemark(id, remark, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检测特征名是否重复
	 */
	@RequestMapping("/pm/bom/checkName.action")
	@ResponseBody
	public Map<String, Object> checkName(String caller, String name) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("flag", featureService.checkName(name, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改明细状态
	 */
	@RequestMapping("/pm/bom/updateByCondition.action")
	@ResponseBody
	public Map<String, Object> updateByCondition(String caller,
			String tablename, String condition, String update) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.updateByCondition(tablename, condition, update, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用明细
	 * */
	@RequestMapping("pm/bom/bannedDetails.action")
	@ResponseBody
	public Map<String, Object> bannedDetails(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureService.bannedDetails(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
