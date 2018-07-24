package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ReserveCloseService;

@Controller
public class ReserveCloseController extends BaseController {
	@Autowired
	private ReserveCloseService reserveCloseService;
	
	/**
	 * 库存结账
	 */
	@RequestMapping("/scm/reserve/confirmReserveClose.action")  
	@ResponseBody 
	public Map<String, Object> confirmAutoDepreciation(Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reserveCloseService.reserveclose(param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 取物料月份期间
	 */
	@RequestMapping("/scm/reserve/getCurrentYearmonth.action")  
	@ResponseBody 
	public Map<String, Object> getCurrentYearmonth(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", reserveCloseService.getCurrentYearmonth());
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 库存反结账
	 */
	@RequestMapping("/scm/reserve/unperiodsdetail.action")  
	@ResponseBody 
	public Map<String, Object> unperiodsdetail(Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reserveCloseService.unperiodsdetail(param);
		modelMap.put("success", true);
		return modelMap;
	}
}
