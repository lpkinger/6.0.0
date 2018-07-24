package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.ProdiouttuiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ProdiouttuiController {

	@Autowired
	private ProdiouttuiService prodiouttuiService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveProdiouttui.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodiouttuiService.saveProdiouttui(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateProdiouttui.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodiouttuiService.updateProdiouttuiById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteProdiouttui.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodiouttuiService.deleteProdiouttui(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitProdiouttui.action")
	@ResponseBody
	public Map<String, Object> submitProdiouttui(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodiouttuiService.submitProdiouttui(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitProdiouttui.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdiouttui(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodiouttuiService.resSubmitProdiouttui(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditProdiouttui.action")
	@ResponseBody
	public Map<String, Object> auditProdiouttui(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodiouttuiService.auditProdiouttui(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditProdiouttui.action")
	@ResponseBody
	public Map<String, Object> resAuditProdiouttui(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodiouttuiService.resAuditProdiouttui(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
