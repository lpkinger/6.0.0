package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.AssetsCardService;

@Controller("assetsCardController")
public class AssetsCardController extends BaseController {
	@Autowired
	private AssetsCardService assetsCardService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/fix/saveAssetsCard.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsCardService.saveAssetsCard(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除单数据 包括明细
	 */
	@RequestMapping("/fa/fix/deleteAssetsCard.action")
	@ResponseBody
	public Map<String, Object> deleteAssetsCard(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsCardService.deleteAssetsCard(id, caller);
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
	@RequestMapping("/fa/fix/updateAssetsCard.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsCardService.updateAssetsCardById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fix/submitAssetsCard.action")
	@ResponseBody
	public Map<String, Object> submitAssetsCard(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsCardService.submitAssetsCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fix/resSubmitAssetsCard.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssetsCard(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsCardService.resSubmitAssetsCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fix/auditAssetsCard.action")
	@ResponseBody
	public Map<String, Object> auditAssetsCard(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsCardService.auditAssetsCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fix/resAuditAssetsCard.action")
	@ResponseBody
	public Map<String, Object> resAuditAssetsCard(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsCardService.resAuditAssetsCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制
	 */
	@RequestMapping("/fa/fix/copyAssetsCard.action")
	@ResponseBody
	public Map<String, Object> copyAssetsCard(HttpSession session, int id, String caller, String accode, int kindid) {
		return success(assetsCardService.copyAssetsCard(id, accode, kindid, caller));
	}

	/**
	 * 更新使用状况
	 */
	@RequestMapping("/fa/fix/assetscard/updateusestatus.action")
	@ResponseBody
	public Map<String, Object> updateusestatus(HttpSession session, int id, String usestatus) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsCardService.updateusestatus(id, usestatus);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 卡片自动补断号
	 */
	@RequestMapping(value = "/fa/fix/getAssetsCardCodeNum.action")
	@ResponseBody
	public Map<String, Object> getAssetsCardCodeNum(String caller, String kind) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("codes", assetsCardService.getAssetsCardCodeNum(caller, kind));
		modelMap.put("success", true);
		return modelMap;
	}
}
