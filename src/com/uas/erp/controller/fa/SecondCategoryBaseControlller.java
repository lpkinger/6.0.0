package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.SecondCategoryBaseService;

@Controller
public class SecondCategoryBaseControlller extends BaseController {
	@Autowired
	private SecondCategoryBaseService SecondCategoryBaseService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/ars/saveSecondCategoryBase.action")
	@ResponseBody
	public Map<String, Object> saveSecondCategoryBase(HttpSession session,
			String formStore, String param) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SecondCategoryBaseService.saveSecondCategoryBase(formStore, language,
				employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteSecondCategoryBase.action")
	@ResponseBody
	public Map<String, Object> deleteSecondCategoryBase(HttpSession session,
			int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SecondCategoryBaseService.deleteSecondCategoryBase(id, language,
				employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/updateSecondCategoryBase.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SecondCategoryBaseService.updateSecondCategoryBaseById(formStore,
				language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitSecondCategoryBase.action")
	@ResponseBody
	public Map<String, Object> submitVoucher(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SecondCategoryBaseService.submitSecondCategory(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitSecondCategoryBase.action")
	@ResponseBody
	public Map<String, Object> resSubmitVoucher(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SecondCategoryBaseService.resSubmitSecondCategory(id, language,
				employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditSecondCategoryBase.action")
	@ResponseBody
	public Map<String, Object> auditVoucher(HttpSession session, int id) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SecondCategoryBaseService.auditSecondCategory(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
}
