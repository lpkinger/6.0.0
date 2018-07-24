package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.GiftRequestService;

@Controller
public class GiftRequestController {
	@Autowired
	private GiftRequestService giftRequestService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/crm/customercare/saveGiftRequest.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftRequestService.saveGiftRequest(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/customercare/updateGiftRequest.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftRequestService.updateGiftRequestById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/customercare/deleteGiftRequest.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftRequestService.deleteGiftRequest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/customercare/submitGiftRequest.action")
	@ResponseBody
	public Map<String, Object> submitGiftRequest(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftRequestService.submitGiftRequest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/customercare/resSubmitGiftRequest.action")
	@ResponseBody
	public Map<String, Object> resSubmitGiftRequest(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftRequestService.resSubmitGiftRequest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/customercare/auditGiftRequest.action")
	@ResponseBody
	public Map<String, Object> auditGiftRequest(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftRequestService.auditGiftRequest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/customercare/resAuditGiftRequest.action")
	@ResponseBody
	public Map<String, Object> resAuditGiftRequest(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftRequestService.resAuditGiftRequest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/customercare/turnGiPurchase.action")
	@ResponseBody
	public Map<String, Object> turnOaPurchase(String formdata, String griddata,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		giftRequestService.turnOaPurchase(formdata, griddata, caller);
		return modelMap;

	}
}
