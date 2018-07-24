package com.uas.erp.controller.plm;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.plm.TransactionService;

@Controller
public class TransactionController {
	@Autowired
	private TransactionService transactionService;
	@RequestMapping(value="/plm/project/saveTransaction.action")
	@ResponseBody
	public Map<String, Object> saveTransaction(HttpSession session,String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		transactionService.saveTransaction(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping(value="/plm/project/updateTransaction.action")
	@ResponseBody
	public Map<String, Object> updateTransaction(HttpSession session,String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		transactionService.updateTransaction(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/submitTransaction.action")  
	@ResponseBody 
	public Map<String, Object> submitTransaction(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		transactionService.submitTransaction(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/ressubmitTransaction.action")  
	@ResponseBody 
	public Map<String, Object> ressubmitTransaction(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		transactionService.resSubmitTransaction(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/auditTransaction.action")  
	@ResponseBody 
	public Map<String, Object> auditTransaction(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		transactionService.auditTransaction(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/project/resauditTransaction.action")  
	@ResponseBody 
	public Map<String, Object> resauditTransaction(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		transactionService.resAuditTransaction(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
