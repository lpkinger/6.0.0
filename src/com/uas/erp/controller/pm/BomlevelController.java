package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.BomlevelService;

@Controller
public class BomlevelController {

	@Autowired
	private BomlevelService bomlevelService;

	/**
	 * 保存pmOrg
	 */
	@RequestMapping("/pm/bom/saveBomlevel.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param, param2, param3 };
		bomlevelService.saveBomlevel(formStore, params,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/pm/bom/updateBomlevel.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param, param2, param3 };
		bomlevelService.updateBomlevelById(formStore, params,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/bom/deleteBomlevel.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomlevelService.deleteBomlevel(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("pm/bom/submitBomlevel.action")
	@ResponseBody
	public Map<String, Object> submitBomlevel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomlevelService.submitBomlevel(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("pm/bom/resSubmitBomlevel.action")
	@ResponseBody
	public Map<String, Object> resSubmitBomlevel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomlevelService.resubmitBomlevel(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/bom/auditBomlevel.action")
	@ResponseBody
	public Map<String, Object> auditBomlevel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomlevelService.auditBomlevel(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/bom/resAuditBomlevel.action")
	@ResponseBody
	public Map<String, Object> resAuditBomlevel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomlevelService.reauditBomlevel(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/bom/updateBomleveldetail.action")
	@ResponseBody
	public Map<String, Object> updateBomLevel(String caller, Integer id,
			String param1, String param3, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomlevelService.updateBomleveldetail(id,caller, param1, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

}
