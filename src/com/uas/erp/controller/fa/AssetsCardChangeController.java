package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AssetsCardChangeService;

@Controller("assetsCardChangeController")
public class AssetsCardChangeController extends BaseController {
	@Autowired
	private AssetsCardChangeService AssetsCardChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fix/saveAssetsCardChange.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		AssetsCardChangeService.saveAssetsCardChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/fa/fix/deleteAssetsCardChange.action")
	@ResponseBody
	public Map<String, Object> deleteAssetsCardChange(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		AssetsCardChangeService.deleteAssetsCardChange(id, caller);
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
	@RequestMapping("/fa/fix/updateAssetsCardChange.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		AssetsCardChangeService.updateAssetsCardChangeById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fix/submitAssetsCardChange.action")
	@ResponseBody
	public Map<String, Object> submitAssetsCardChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		AssetsCardChangeService.submitAssetsCardChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fix/resSubmitAssetsCardChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssetsCardChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		AssetsCardChangeService.resSubmitAssetsCardChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fix/auditAssetsCardChange.action")
	@ResponseBody
	public Map<String, Object> auditAssetsCardChange(HttpSession session,
			int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		AssetsCardChangeService.auditAssetsCardChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
