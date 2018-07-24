package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AssetsPleaseService;

@Controller
public class AssetsPleaseController extends BaseController {
	@Autowired
	private AssetsPleaseService assetsPleaseService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fix/saveAssetsPlease.action")
	@ResponseBody
	public Map<String, Object> saveAssetsPlease(HttpSession session,
			String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsPleaseService.saveAssetsPlease(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 注意:
	 */
	@RequestMapping("/fa/fix/deleteAssetsPlease.action")
	@ResponseBody
	public Map<String, Object> deleteAssetsPlease(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsPleaseService.deleteAssetsPlease(caller, id);
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
	@RequestMapping("/fa/fix/updateAssetsPlease.action")
	@ResponseBody
	public Map<String, Object> updateAssetsPlease(HttpSession session,
			String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsPleaseService.updateAssetsPleaseById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/fix/printAssetsPlease.action")
	@ResponseBody
	public Map<String, Object> printAssetsPlease(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsPleaseService.printAssetsPlease(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fix/submitAssetsPlease.action")
	@ResponseBody
	public Map<String, Object> submitAssetsPlease(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsPleaseService.submitAssetsPlease(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fix/resSubmitAssetsPlease.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssetsPlease(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsPleaseService.resSubmitAssetsPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/fix/auditAssetsPlease.action")
	@ResponseBody
	public Map<String, Object> auditAssetsPlease(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsPleaseService.auditAssetsPlease(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/fix/resAuditAssetsPlease.action")
	@ResponseBody
	public Map<String, Object> resAuditAssetsPlease(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsPleaseService.resAuditAssetsPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
