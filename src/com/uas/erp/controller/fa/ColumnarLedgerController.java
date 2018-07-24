package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.model.ColumnarLedgerFilter;
import com.uas.erp.service.fa.ColumnarLedgerService;

@Controller
public class ColumnarLedgerController {

	@Autowired
	private ColumnarLedgerService columnarledgerService;

	/**
	 * 多栏式明细账查询
	 */
	@RequestMapping("/fa/ars/getColumnarLedger.action")
	@ResponseBody
	public Map<String, Object> getColumnarLedger(HttpSession session, String condition) {
		ColumnarLedgerFilter filter = FlexJsonUtil.fromJson(condition, ColumnarLedgerFilter.class);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (filter.getChkall())
			filter = columnarledgerService.getCurrentAsscode(filter);
		modelMap.put("filter", filter);
		modelMap.put("data", columnarledgerService.getColumnarLedger(filter));
		modelMap.put("columns", columnarledgerService.getGridColumnsByMulticolacScheme(filter));
		modelMap.put("success", true);
		return modelMap;
	}

}
