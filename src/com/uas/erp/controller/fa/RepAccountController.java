package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.RepAccountService;
@Controller
public class RepAccountController extends BaseController {
	@Autowired
	private RepAccountService repAccountService;
	
	/**报表计算
	 */
	@RequestMapping("/fa/ars/reportAccount.action")  
	@ResponseBody 
	public Map<String, Object> reportAccount(HttpSession session, Integer date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		repAccountService.reportAccount(date);
		modelMap.put("success", true);
		return modelMap;
	}

	/**佣金计算
	 */
	@RequestMapping("/fa/wg/WageAccount.action")  
	@ResponseBody 
	public Map<String, Object> wageAccount(HttpSession session, Integer date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		repAccountService.wageAccount(date);
		modelMap.put("success", true);
		return modelMap;
	}
}
