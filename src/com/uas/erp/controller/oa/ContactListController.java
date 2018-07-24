package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.ContactListService;

@Controller
public class ContactListController {
	@Autowired
	private ContactListService contactListService;
	/**
	 * 保存
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/oa/persontask/myContactList/saveContactList.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contactListService.saveContactList(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
