package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AmortProgramService;

@Controller
public class AmortProgramController extends BaseController {
	@Autowired
	private AmortProgramService amortProgramService;

	@RequestMapping("/fa/gla/saveAmortProgram.action")
	@ResponseBody
	public Map<String, Object> saveAssetsLocation(HttpSession session,
			String formStore, String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		amortProgramService.saveAmortProgram(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/gla/deleteAmortProgram.action")
	@ResponseBody
	public Map<String, Object> deleteAmortProgram(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		amortProgramService.deleteAmortProgram(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gla/updateAmortProgram.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		amortProgramService.updateAmortProgramById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gla/auditAmortProgram.action")
	@ResponseBody
	public Map<String, Object> auditAmortProgram(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		amortProgramService.auditAmortProgram(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gla/resAuditAmortProgram.action")
	@ResponseBody
	public Map<String, Object> resAuditAmortProgram(HttpSession session,
			int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		amortProgramService.resAuditAmortProgram(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
