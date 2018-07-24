package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.ProdioutlinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ProdioutlinController {

	@Autowired
	private ProdioutlinService prodioutlinService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveProdioutlin.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutlinService.saveProdioutlin(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateProdioutlin.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutlinService.updateProdioutlinById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteProdioutlin.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutlinService.deleteProdioutlin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitProdioutlin.action")
	@ResponseBody
	public Map<String, Object> submitProdioutlin(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutlinService.submitProdioutlin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitProdioutlin.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdioutlin(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutlinService.resSubmitProdioutlin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditProdioutlin.action")
	@ResponseBody
	public Map<String, Object> auditProdioutlin(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutlinService.auditProdioutlin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditProdioutlin.action")
	@ResponseBody
	public Map<String, Object> resAuditProdioutlin(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutlinService.resAuditProdioutlin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
