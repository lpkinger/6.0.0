package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.FeatureProductService;

@Controller
public class FeatureProductController extends BaseController {
	@Autowired
	private FeatureProductService featureProductService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveFeatureProduct.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.saveFeatureProduct(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * FeatureProduct整批导入
	 */
	@RequestMapping("/pm/bom/batchSaveFeatureProduct.action")
	@ResponseBody
	public Map<String, Object> batchSave(String caller, String bom,
			String detail) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.batchSaveFeatureProduct(bom, detail,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除FeatureProduct数据 包括FeatureProduct明细
	 */
	@RequestMapping("/pm/bom/deleteFeatureProduct.action")
	@ResponseBody
	public Map<String, Object> deleteFeatureProduct(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.deleteFeatureProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("/pm/bom/deleteFeatureProductDetail.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.deleteDetail(id, caller);
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
	@RequestMapping("/pm/bom/updateFeatureProduct.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.updateFeatureProductById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交FeatureProduct
	 */
	@RequestMapping("/pm/bom/submitFeatureProduct.action")
	@ResponseBody
	public Map<String, Object> submitFeatureProduct(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.submitFeatureProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交FeatureProduct
	 */
	@RequestMapping("/pm/bom/resSubmitFeatureProduct.action")
	@ResponseBody
	public Map<String, Object> resSubmitFeatureProduct(String caller, 
			int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.resSubmitFeatureProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核FeatureProduct
	 */
	@RequestMapping("/pm/bom/auditFeatureProduct.action")
	@ResponseBody
	public Map<String, Object> auditFeatureProduct(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.auditFeatureProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核FeatureProduct
	 */
	@RequestMapping("/pm/bom/resAuditFeatureProduct.action")
	@ResponseBody
	public Map<String, Object> resAuditFeatureProduct(String caller, 
			int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.resAuditFeatureProduct(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("/pm/bom/deleteAllFeatureProductDetail.action")
	@ResponseBody
	public Map<String, Object> deleteAllDetail(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		featureProductService.deleteAllDetail(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 根据表名及condition获取所需要字段的数据集合
	 */
	@RequestMapping("/pm/bom/getList.action")
	@ResponseBody
	public Map<String, Object> getList(String caller, String tablename,
			String[] field, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("list", featureProductService.getList(tablename, field,
				condition, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
