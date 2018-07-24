package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CurrencysService;

@Controller
public class CurrencysController {
	@Autowired
	private CurrencysService currencysService;

	/**
	 * 保存Currencys
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/fa/ars/saveCurrencys.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencysService.saveCurrencys(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/ars/updateCurrencys.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencysService.updateCurrencysById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteCurrencys.action")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencysService.deleteCurrencys(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用
	 */
	@RequestMapping("/fa/ars/bannedCurrencys.action")
	@ResponseBody
	public Map<String, Object> bannedCurrencys(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencysService.bannedCurrencys(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/fa/ars/resBannedCurrencys.action")
	@ResponseBody
	public Map<String, Object> resBannedCurrencys(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencysService.resBannedCurrencys(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存Currencys
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/fa/fix/CurrencysController/updateCurrencysMonth.action")
	@ResponseBody
	public Map<String, Object> updateCurrencysMonth(HttpSession session,
			String formStore, String param, String mf, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencysService.updateCurrencysMonth(param, caller, mf);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存Currencys
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/fa/fix/CurrencysController/deleteCurrencysMonth.action")
	@ResponseBody
	public Map<String, Object> deleteCurrencysMonth(HttpSession session,
			String id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		currencysService.deleteCurrencysMonth(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/fix/lastrate.action")
	@ResponseBody
	public Map<String, Object> getLastEndRate(HttpSession session, String last,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", currencysService.getLastEndRate(last, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}
