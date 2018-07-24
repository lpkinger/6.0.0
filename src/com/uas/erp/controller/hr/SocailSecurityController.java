package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.SocailSecurityService;

@Controller
public class SocailSecurityController {
	@Autowired
	private SocailSecurityService socailSecurityService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveSocailSecurity.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		socailSecurityService.saveSocailSecurity(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateSocailSecurity.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		socailSecurityService.updateSocailSecurityById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteSocailSecurity.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		socailSecurityService.deleteSocailSecurity(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/emplmana/vastSocailsecu.action")
	@ResponseBody
	public Map<String, Object> vastSocailsecu(String caller, int[] id,
			String[] mark) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		socailSecurityService.vastSocailsecu(caller, mark, id);
		modelMap.put("success", true);
		return modelMap;
	}
}
