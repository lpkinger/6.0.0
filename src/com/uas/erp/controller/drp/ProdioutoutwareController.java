package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.ProdioutoutwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ProdioutoutwareController {

	@Autowired
	private ProdioutoutwareService prodioutoutwareService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveProdioutoutware.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutoutwareService.saveProdioutoutware(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateProdioutoutware.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutoutwareService.updateProdioutoutwareById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteProdioutoutware.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutoutwareService.deleteProdioutoutware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitProdioutoutware.action")
	@ResponseBody
	public Map<String, Object> submitProdioutoutware(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutoutwareService.submitProdioutoutware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitProdioutoutware.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdioutoutware(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutoutwareService.resSubmitProdioutoutware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditProdioutoutware.action")
	@ResponseBody
	public Map<String, Object> auditProdioutoutware(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutoutwareService.auditProdioutoutware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditProdioutoutware.action")
	@ResponseBody
	public Map<String, Object> resAuditProdioutoutware(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodioutoutwareService.resAuditProdioutoutware(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
