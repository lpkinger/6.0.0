package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.GiftBaseService;

@Controller
public class GiftBaseController {
	@Autowired
	private GiftBaseService giftBaseService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("crm/customercare/saveGiftBase.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftBaseService.saveGiftBase(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 * 
	 */
	@RequestMapping("crm/customercare/deleteGiftBase.action")
	@ResponseBody
	public Map<String, Object> deleteChance(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftBaseService.deleteGiftBase(id, caller);
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
	@RequestMapping("crm/customercare/updateGiftBase.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		giftBaseService.updateGiftBase(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
