package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.GoodsSendService;

@Controller("goodsSendController")
public class GoodsSendController extends BaseController {
	@Autowired
	private GoodsSendService goodsSendService;

	/**
	 * 转发出商品
	 */
	@RequestMapping("/fa/GoodsSendController/turnGoodsSend.action")
	@ResponseBody
	public Map<String, Object> turnGoodsSend(HttpSession session) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.turnGoodsSend();
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/GoodsSendController/saveGoodsSend.action")
	@ResponseBody
	public Map<String, Object> saveGoodsSend(HttpSession session,
			String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.saveGoodsSend(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/GoodsSendController/deleteGoodsSend.action")
	@ResponseBody
	public Map<String, Object> deleteGoodsSend(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.deleteGoodsSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/GoodsSendController/updateGoodsSend.action")
	@ResponseBody
	public Map<String, Object> updateGoodsSend(HttpSession session,
			String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.updateGoodsSend(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/GoodsSendController/printGoodsSend.action")
	@ResponseBody
	public Map<String, Object> printGoodsSend(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.printGoodsSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/GoodsSendController/submitGoodsSend.action")
	@ResponseBody
	public Map<String, Object> submitGoodsSend(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.submitGoodsSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/GoodsSendController/resSubmitGoodsSend.action")
	@ResponseBody
	public Map<String, Object> resSubmitGoodsSend(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.resSubmitGoodsSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/GoodsSendController/auditGoodsSend.action")
	@ResponseBody
	public Map<String, Object> auditGoodsSend(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.auditGoodsSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/GoodsSendController/resAuditGoodsSend.action")
	@ResponseBody
	public Map<String, Object> resAuditGoodsSend(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.resAuditGoodsSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/ars/GoodsSendController/postGoodsSend.action")
	@ResponseBody
	public Map<String, Object> postGoodsSend(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.postGoodsSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/ars/GoodsSendController/resPostGoodsSend.action")
	@ResponseBody
	public Map<String, Object> resPostGoodsSend(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		goodsSendService.resPostGoodsSend(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

}
