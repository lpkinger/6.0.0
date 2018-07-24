package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.ProdioutinwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ProdioutinwareController {

	@Autowired
	private ProdioutinwareService prodioutinwareService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveProdioutinware.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutinwareService.saveProdioutinware(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateProdioutinware.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutinwareService
				.updateProdioutinwareById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteProdioutinware.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutinwareService.deleteProdioutinware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitProdioutinware.action")
	@ResponseBody
	public Map<String, Object> submitProdioutinware(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutinwareService.submitProdioutinware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitProdioutinware.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdioutinware(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutinwareService.resSubmitProdioutinware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditProdioutinware.action")
	@ResponseBody
	public Map<String, Object> auditProdioutinware(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutinwareService.auditProdioutinware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditProdioutinware.action")
	@ResponseBody
	public Map<String, Object> resAuditProdioutinware(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutinwareService.resAuditProdioutinware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
