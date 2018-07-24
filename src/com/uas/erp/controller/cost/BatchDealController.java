package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.BatchDealService;

/**
 * 存货核算
 */
@Controller("CostBatchDealController")
public class BatchDealController {
	@Autowired
	private BatchDealService batchDealService;

	/**
	 * 核算作业
	 */
	@RequestMapping(value = "/cost/accountProdio.action")
	@ResponseBody
	public Map<String, Object> accountProdio(HttpSession session, String caller, String data, String condition, String condParams) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.accountProdio(language, employee, caller, data, condition, condParams);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取价作业
	 */
	@RequestMapping(value = "/cost/getPrice.action")
	@ResponseBody
	public Map<String, Object> getPrice(HttpSession session, String caller, String data, String condition, String condParams) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.getPrice(language, employee, caller, data, condition, condParams);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 费用分摊
	 */
	@RequestMapping(value = "/cost/shareFee.action")
	@ResponseBody
	public Map<String, Object> shareFee(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.shareFee(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 单价重置
	 */
	@RequestMapping(value = "/cost/resPrice.action")
	@ResponseBody
	public Map<String, Object> resPrice(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.resPrice(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量保存、修改
	 */
	@RequestMapping(value = "/cost/batchUpdate.action")
	@ResponseBody
	public Map<String, Object> batchSave(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.batchSave(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 拨入拨出一致性检测
	 */
	@RequestMapping(value = "/cost/consistencyCheck.action")
	@ResponseBody
	public Map<String, Object> consistency(HttpSession session, Integer date) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.consistency(date, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 销售拨入拨出一致性检测
	 */
	@RequestMapping(value = "/cost/consistencySaleCheck.action")
	@ResponseBody
	public Map<String, Object> consistencySale(HttpSession session, Integer date) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.consistencySale(date, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表实际工时/成本调整金额维护
	 */
	@RequestMapping(value = "/cost/vastSaveCostDetail.action")
	@ResponseBody
	public Map<String, Object> vastSaveCostDetail(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastSaveCostDetail(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：无值转出生成凭证
	 */
	@RequestMapping(value = "/cost/vastNowhVoucherCredit.action")
	@ResponseBody
	public Map<String, Object> vastNowhVoucherCredit(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastNowhVoucherCredit(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本表：调整差异生成凭证
	 */
	@RequestMapping(value = "/cost/vastDifferVoucherCredit.action")
	@ResponseBody
	public Map<String, Object> vastDifferVoucherCredit(HttpSession session, String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastDifferVoucherCredit(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 月结表：调整金额维护
	 */
	@RequestMapping(value = "/cost/vastSaveCostDetailMaterial.action")
	@ResponseBody
	public Map<String, Object> vastSaveCostDetailMaterial(HttpSession session, String caller, String data) {
		String language = (String) session.getAttribute("language");
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.vastSaveCostDetailMaterial(language, employee, caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
}
