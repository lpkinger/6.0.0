package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.ApplicationPLMService;

@Controller
public class ApplicationPLMController extends BaseController {
	@Autowired
	private ApplicationPLMService applicationPLMService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/plm/application/saveApplication.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.saveApplication(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/plm/application/deleteApplication.action")  
	@ResponseBody 
	public Map<String, Object> deleteApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.deleteApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/plm/application/updateApplication.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.updateApplicationById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/plm/application/printApplication.action")  
	@ResponseBody 
	public Map<String, Object> printApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.printApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/plm/application/submitApplication.action")  
	@ResponseBody 
	public Map<String, Object> submitApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.submitApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/plm/application/resSubmitApplication.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.resSubmitApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/plm/application/auditApplication.action")  
	@ResponseBody 
	public Map<String, Object> auditApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.auditApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/plm/application/resAuditApplication.action")  
	@ResponseBody 
	public Map<String, Object> resAuditApplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.resAuditApplication(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转采购
	 */
	@RequestMapping("/plm/application/turnapplication.action")  
	@ResponseBody 
	public Map<String, Object> turnapplication(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int puid = applicationPLMService.turnPurchase(id, caller);
		modelMap.put("id", puid);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 到applicationPrice取供应商
	 */
	@RequestMapping("/plm/application/getVendor.action")  
	@ResponseBody 
	public Map<String, Object> getVendor(int[] id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		applicationPLMService.getVendor(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 请购单批量抛转
	 * @param id 请购单ID
	 * @param ma_id 帐套ID
	 */
	@RequestMapping(value="/plm/application/postAppliaction.action")
	@ResponseBody
	public Map<String, Object> vastPost(HttpSession session, String caller, int[] id, int ma_id){
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", applicationPLMService.postApplication(id, employee.getEm_maid(), ma_id));
		modelMap.put("success", true);
		return modelMap;
	}
}
