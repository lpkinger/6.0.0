package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AssetsKindService;

@Controller("assetsKindController")
public class AssetsKindController extends BaseController {
	@Autowired
	private AssetsKindService assetsKindService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fix/saveAssetsKind.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsKindService.saveAssetsKind(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除采购单数据 包括采购明细
	 */
	@RequestMapping("/fa/fix/deleteAssetsKind.action")
	@ResponseBody
	public Map<String, Object> deleteAssetsCard(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsKindService.deleteAssetsKind(id, caller);
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
	@RequestMapping("/fa/fix/updateAssetsKind.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsKindService.updateAssetsKindById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印采购单
	 */
	// @RequestMapping("/fa/ars/printARBill.action")
	// @ResponseBody
	// public Map<String, Object> printARBill(HttpSession session, int id,String
	// caller) {
	//
	//
	// Map<String, Object> modelMap = new HashMap<String, Object>();
	// arBillService.printARBill(id, caller);
	// modelMap.put("success", true);
	// return modelMap;
	// }
	/**
	 * 提交采购单
	 */
	@RequestMapping("/fa/fix/submitAssetsKind.action")
	@ResponseBody
	public Map<String, Object> submitAssetsKind(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsKindService.submitAssetsKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交采购单
	 */
	@RequestMapping("/fa/fix/resSubmitAssetsKind.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssetsKind(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsKindService.resSubmitAssetsKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核采购单
	 */
	@RequestMapping("/fa/fix/auditAssetsKind.action")
	@ResponseBody
	public Map<String, Object> auditAssetsKind(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsKindService.auditAssetsKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核采购单
	 */
	@RequestMapping("/fa/fix/resAuditAssetsKind.action")
	@ResponseBody
	public Map<String, Object> resAuditAssetsKind(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsKindService.resAuditAssetsKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
