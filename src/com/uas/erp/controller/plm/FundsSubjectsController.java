package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.plm.FundsSubjectsService;
@Controller
public class FundsSubjectsController {
	@Autowired
	private FundsSubjectsService fundsSubjectsService;
	@RequestMapping("/plm/budget/saveFundSubject.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    fundsSubjectsService.saveFundSubject(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/budget/deleteFundSubject.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session,int id) {
		
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    fundsSubjectsService.deleteFundSubject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/budget/updateFundSubject.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    fundsSubjectsService.updateFundSubject(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/budget/submitFundSubject.action")  
	@ResponseBody 
	public Map<String, Object> submitFundSubject(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundsSubjectsService.submitFundSubject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/plm/budget/resSubmitFundSubject.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitFundSubject(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundsSubjectsService.resSubmitFundSubject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/plm/budget/auditFundSubject.action")  
	@ResponseBody 
	public Map<String, Object> auditFundSubject(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundsSubjectsService.auditFundSubject(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/plm/budget/resAuditFundSubject.action")  
	@ResponseBody 
	public Map<String, Object> resAuditFundSubject(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundsSubjectsService.resAuditFundSubject(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
