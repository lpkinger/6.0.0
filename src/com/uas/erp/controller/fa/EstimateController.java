package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.EstimateService;

@Controller("estimateController")
public class EstimateController extends BaseController {
	@Autowired
	private EstimateService estimateService;

	/**
	 * 转暂估
	 */
	@RequestMapping("/fa/EstimateController/turnEstimate.action")
	@ResponseBody
	public Map<String, Object> turnEstimate(HttpSession session) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.turnEstimate();
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/arp/EstimateController/saveEstimate.action")
	@ResponseBody
	public Map<String, Object> saveEstimate(HttpSession session,
			String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.saveEstimate(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/arp/EstimateController/deleteEstimate.action")
	@ResponseBody
	public Map<String, Object> deleteEstimate(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.deleteEstimate(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/arp/EstimateController/updateEstimate.action")
	@ResponseBody
	public Map<String, Object> updateEstimate(HttpSession session,
			String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.updateEstimate(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/arp/EstimateController/printEstimate.action")
	@ResponseBody
	public Map<String, Object> printEstimate(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.printEstimate(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/arp/EstimateController/submitEstimate.action")
	@ResponseBody
	public Map<String, Object> submitEstimate(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.submitEstimate(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/arp/EstimateController/resSubmitEstimate.action")
	@ResponseBody
	public Map<String, Object> resSubmitEstimate(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.resSubmitEstimate(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/arp/EstimateController/auditEstimate.action")
	@ResponseBody
	public Map<String, Object> auditEstimate(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.auditEstimate(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/arp/EstimateController/resAuditEstimate.action")
	@ResponseBody
	public Map<String, Object> resAuditEstimate(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.resAuditEstimate(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/fa/arp/EstimateController/postEstimate.action")
	@ResponseBody
	public Map<String, Object> postEstimate(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.postEstimate(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/fa/arp/EstimateController/resPostEstimate.action")
	@ResponseBody
	public Map<String, Object> resPostEstimate(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		estimateService.resPostEstimate(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

}
