package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.ProdioutbuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ProdioutbuController {

	@Autowired
	private ProdioutbuService prodioutbuService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveProdioutbu.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutbuService.saveProdioutbu(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateProdioutbu.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutbuService.updateProdioutbuById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteProdioutbu.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutbuService.deleteProdioutbu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitProdioutbu.action")
	@ResponseBody
	public Map<String, Object> submitProdioutbu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutbuService.submitProdioutbu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitProdioutbu.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdioutbu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutbuService.resSubmitProdioutbu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditProdioutbu.action")
	@ResponseBody
	public Map<String, Object> auditProdioutbu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutbuService.auditProdioutbu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditProdioutbu.action")
	@ResponseBody
	public Map<String, Object> resAuditProdioutbu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutbuService.resAuditProdioutbu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
