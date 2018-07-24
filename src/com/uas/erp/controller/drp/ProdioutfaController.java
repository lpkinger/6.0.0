package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.ProdioutfaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ProdioutfaController {

	@Autowired
	private ProdioutfaService prodioutfaService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveProdioutfa.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutfaService.saveProdioutfa(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateProdioutfa.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutfaService.updateProdioutfaById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteProdioutfa.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutfaService.deleteProdioutfa(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitProdioutfa.action")
	@ResponseBody
	public Map<String, Object> submitProdioutfa(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutfaService.submitProdioutfa(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitProdioutfa.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdioutfa(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutfaService.resSubmitProdioutfa(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditProdioutfa.action")
	@ResponseBody
	public Map<String, Object> auditProdioutfa(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutfaService.auditProdioutfa(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditProdioutfa.action")
	@ResponseBody
	public Map<String, Object> resAuditProdioutfa(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutfaService.resAuditProdioutfa(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
