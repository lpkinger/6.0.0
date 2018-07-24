package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.SocailAccountService;

@Controller
public class SocailAccountController {
	@Autowired
	private SocailAccountService socailAccountService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveSocailAccount.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		socailAccountService.saveSocailAccount(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateSocailAccount.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		socailAccountService.updateSocailAccountById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteSocailAccount.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		socailAccountService.deleteSocailAccount(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/emplmana/vastSocailaccount.action")
	@ResponseBody
	public Map<String, Object> vastSocailAccount(String caller, int[] id,
			String[] mark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		socailAccountService.vastSocailAccount(caller, mark, id);
		modelMap.put("success", true);
		return modelMap;
	}

}
