package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.dao.common.LedgerDetailDao;
import com.uas.erp.model.LedgerFilter;
import com.uas.erp.service.fa.LedgerDetailService;
import com.uas.erp.service.fa.LedgerService;

@Controller
public class LedgerController {

	@Autowired
	private LedgerService ledgerService;

	@Autowired
	private LedgerDetailService ledgerDetailService;

	@Autowired
	private LedgerDetailDao ledgerDetailDao;

	/**
	 * 总分类账查询
	 */
	@RequestMapping("/fa/ars/getGeneralLedger.action")
	@ResponseBody
	public Map<String, Object> getGeneralLedger(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", ledgerService.getGeneralLedger(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总分类账查询(单行)
	 */
	@RequestMapping("/fa/ars/getGeneralLedgerSingle.action")
	@ResponseBody
	public Map<String, Object> getGeneralLedgerSingle(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", ledgerService.getGeneralLedgerSingle(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 明细账查询
	 */
	@RequestMapping("/fa/ars/getGLDetail.action")
	@ResponseBody
	public Map<String, Object> getGLDetail(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", ledgerService.getGLDetail(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 明细账查询
	 */
	@RequestMapping("/fa/ars/getGLDetail2.action")
	@ResponseBody
	public Map<String, Object> getGLDetail2(HttpSession session, String condition) {
		LedgerFilter filter = FlexJsonUtil.fromJson(condition, LedgerFilter.class);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		filter = ledgerDetailDao.queryByFilter(filter);
		modelMap.put("data", ledgerDetailService.getGLDetail(filter));
		modelMap.put("filter", filter);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 全部打印取数
	 */
	@RequestMapping("/fa/ars/getGLDetailPrintData.action")
	@ResponseBody
	public Map<String, Object> getGLDetailPrintData(HttpSession session, String condition) {
		LedgerFilter filter = FlexJsonUtil.fromJson(condition, LedgerFilter.class);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("filter", ledgerDetailDao.queryByFilter(filter));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 部门明细查询
	 */
	@RequestMapping("/fa/ars/getDeptDetail.action")
	@ResponseBody
	public Map<String, Object> getDeptDetail(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", ledgerService.getDeptDetail(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证汇总表查询
	 */
	@RequestMapping("/fa/ars/getVoucherSum.action")
	@ResponseBody
	public Map<String, Object> getVoucherSum(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", ledgerService.getVoucherSum(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 凭证汇总表凭证数量
	 */
	@RequestMapping("/fa/ars/getVoucherSumCount.action")
	@ResponseBody
	public Map<String, Object> getVoucherSumCount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", ledgerService.getVoucherCount(condition));
		modelMap.put("success", true);
		return modelMap;
	}
}
