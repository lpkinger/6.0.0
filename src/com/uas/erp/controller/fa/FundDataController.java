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

@Controller
public class FundDataController extends BaseController {
	
	@Autowired
	private FundDataService fundDataService;
	
	@RequestMapping("/fa/fundData/autogetItems.action")
	@ResponseBody
	public Map<String, Object> autogetItems(HttpSession session,Integer id,String kind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", fundDataService.autogetItems(id,kind));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/fundData/save.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundDataService.save(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fundData/delete.action")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundDataService.delete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/fundData/update.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fundDataService.update(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
