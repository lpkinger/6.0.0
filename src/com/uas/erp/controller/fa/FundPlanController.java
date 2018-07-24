package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AccountSetService;
import com.uas.erp.service.fa.FundDataService;
import com.uas.erp.service.fa.FundPlanService;

@Controller
public class FundPlanController extends BaseController {
	
	@Autowired
	private FundPlanService fundPlanService;
	
	
	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/fundPlan/save.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundPlanService.save(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fundPlan/delete.action")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundPlanService.delete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/fundPlan/update.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundPlanService.update(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
