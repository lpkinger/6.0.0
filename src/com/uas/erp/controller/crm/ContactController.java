package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ContactService;

@Controller
public class ContactController {
	@Autowired
	private ContactService contactService;

	// 规范 小写
	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("crm/customermgr/saveContact.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contactService.saveContact(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/customermgr/deleteContact.action")
	@ResponseBody
	public Map<String, Object> deleteContract(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contactService.deleteContact(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/customermgr/updateContact.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contactService.updateContactById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
