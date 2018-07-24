package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.FormulaService;

@Controller
public class FormulaController extends BaseController {

	@Autowired
	private FormulaService formulaService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/wg/saveFormula.action")
	@ResponseBody
	public Map<String, Object> saveFormula(HttpSession session,
			String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		formulaService.saveFormula(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/wg/deleteFormula.action")
	@ResponseBody
	public Map<String, Object> deleteFormula(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		formulaService.deleteFormula(id, caller);
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
	@RequestMapping("/fa/wg/updateFormula.action")
	@ResponseBody
	public Map<String, Object> updateFormula(HttpSession session,
			String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		formulaService.updateFormulaById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/wg/submitFormula.action")
	@ResponseBody
	public Map<String, Object> submitFormula(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		formulaService.submitFormula(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/wg/resSubmitFormula.action")
	@ResponseBody
	public Map<String, Object> resSubmitFormula(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		formulaService.resSubmitFormula(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/wg/auditFormula.action")
	@ResponseBody
	public Map<String, Object> auditFormula(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		formulaService.auditFormula(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/wg/resAuditFormula.action")
	@ResponseBody
	public Map<String, Object> resAuditFormula(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		formulaService.resAuditFormula(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
