package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.cost.CostVoucherService;

/**
 * 成本的凭证制作
 * 
 * @author yingp
 * 
 */
@Controller
public class CostVoucherController {

	@Autowired
	private CostVoucherService costVoucherService;

	/**
	 * 制作费用结转凭证制作
	 * 
	 * @param session
	 * @param ca_code
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/co/cost/makeCreate.action")
	@ResponseBody
	public Map<String, Object> makeCreate(HttpSession session, String makeCatecode, String makeToCatecode, Boolean account,
			String materialsCatecode, Boolean account2, String manMakeCatecode, Boolean account3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", costVoucherService.makeCreate(makeCatecode, makeToCatecode, account, materialsCatecode, account2,
				manMakeCatecode, account3));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 主营成本结转凭证制作
	 * 
	 * @param session
	 * @param ca_code
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/co/cost/mainCreate.action")
	@ResponseBody
	public Map<String, Object> mainCreate(HttpSession session, Boolean account) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", costVoucherService.mainCreate(account));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 主营成本结转凭证取消
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/co/cost/unCreate.action")
	@ResponseBody
	public Map<String, Object> unCreate(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", costVoucherService.unCreate());
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生产成本-工程成本结转凭证制作
	 * 
	 * @param session
	 * @param ca_code
	 * @param account
	 * @return
	 */
	@RequestMapping(value = "/co/cost/engineeringCreate.action")
	@ResponseBody
	public Map<String, Object> engineeringCreate(HttpSession session, String enCatecode, String gsCatecode, Boolean account) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", costVoucherService.engineeringCreate(enCatecode, gsCatecode, account));
		modelMap.put("success", true);
		return modelMap;
	}
}
