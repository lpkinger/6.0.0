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
import com.uas.erp.service.fa.LedgerMultiService;

@Controller
public class LedgerMultiController {

	@Autowired
	private LedgerMultiService ledgerMultiService;

	@Autowired
	private LedgerDetailService ledgerDetailService;

	@Autowired
	private LedgerDetailDao ledgerDetailDao;

	/**
	 * 总分类账查询
	 */
	@RequestMapping("/fa/ars/getGeneralLedgerMulti.action")
	@ResponseBody
	public Map<String, Object> getGeneralLedgerMulti(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", ledgerMultiService.getGeneralLedgerMulti(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 明细账查询
	 */
	@RequestMapping("/fa/ars/getGLDetailMulti.action")
	@ResponseBody
	public Map<String, Object> getGLDetailMulti(HttpSession session, String condition) {
		LedgerFilter filter = FlexJsonUtil.fromJson(condition, LedgerFilter.class);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		filter = ledgerDetailDao.queryByFilterMulti(filter);
		modelMap.put("data", ledgerDetailService.getGLDetail(filter));
		modelMap.put("filter", filter);
		modelMap.put("success", true);
		return modelMap;
	}
}
