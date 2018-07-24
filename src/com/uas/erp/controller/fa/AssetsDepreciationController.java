package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AssetsDepreciationService;

@Controller("assetsDepreciationController")
public class AssetsDepreciationController extends BaseController {
	@Autowired
	private AssetsDepreciationService assetsDepreciationService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fix/saveAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String caller,
			String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.saveAssetsDepreciation(caller, formStore,
				param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除单据数据 包括采购明细
	 */
	@RequestMapping("/fa/fix/deleteAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> deleteAssetsDepreciation(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.deleteAssetsDepreciation(caller, id);
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
	@RequestMapping("/fa/fix/updateAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.updateAssetsDepreciationById(caller,
				formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印单据
	 */
	@RequestMapping("/fa/fix/printAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> printAssetsDepreciation(HttpSession session,
			String caller, int id, String reportName, String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("keyData", assetsDepreciationService
				.printAssetsDepreciation(caller, id, reportName, condition));
		return modelMap;
	}

	/**
	 * 提交单据
	 */
	@RequestMapping("/fa/fix/submitAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> submitAssetsDepreciation(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.submitAssetsDepreciation(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交单据
	 */
	@RequestMapping("/fa/fix/resSubmitAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssetsDepreciation(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.resSubmitAssetsDepreciation(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核单据
	 */
	@RequestMapping("/fa/fix/auditAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> auditAssetsDepreciation(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.auditAssetsDepreciation(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核单据
	 */
	@RequestMapping("/fa/fix/resAuditAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> resAuditAssetsDepreciation(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.resAuditAssetsDepreciation(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/fix/postAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> postProdInOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.postAssetsDepreciation(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/fix/resPostAssetsDepreciation.action")
	@ResponseBody
	public Map<String, Object> resPostProdInOut(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsDepreciationService.resPostAssetsDepreciation(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}
