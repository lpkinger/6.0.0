package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AssetsIOService;

@Controller
public class AssetsIOController extends BaseController {
	@Autowired
	private AssetsIOService assetsIOService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fix/saveAssetsIO.action")
	@ResponseBody
	public Map<String, Object> saveAssetsIO(HttpSession session, String caller,
			String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsIOService.saveAssetsIO(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 注意:
	 */
	@RequestMapping("/fa/fix/deleteAssetsIO.action")
	@ResponseBody
	public Map<String, Object> deleteAssetsIO(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsIOService.deleteAssetsIO(caller, id);
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
	@RequestMapping("/fa/fix/updateAssetsIO.action")
	@ResponseBody
	public Map<String, Object> updateAssetsIO(HttpSession session,
			String caller, String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsIOService.updateAssetsIOById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/fix/printAssetsIO.action")
	@ResponseBody
	public Map<String, Object> printAssetsIO(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsIOService.printAssetsIO(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fix/submitAssetsIO.action")
	@ResponseBody
	public Map<String, Object> submitAssetsIO(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsIOService.submitAssetsIO(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fix/resSubmitAssetsIO.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssetsIO(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsIOService.resSubmitAssetsIO(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/fix/auditAssetsIO.action")
	@ResponseBody
	public Map<String, Object> auditAssetsIO(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsIOService.auditAssetsIO(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/fix/resAuditAssetsIO.action")
	@ResponseBody
	public Map<String, Object> resAuditAssetsIO(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsIOService.resAuditAssetsIO(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
