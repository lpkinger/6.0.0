package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;

import org.apache.derby.tools.sysinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.hr.WageItemService;
import com.uas.erp.service.hr.WageReportService;


@Controller
public class WageReportController extends BaseController {

	@Autowired
	private WageReportService wageReportService;
	
	
	/**
	 * 计算生成工资报表
	 * @param date
	 * @return
	 */
	@RequestMapping("/hr/wage/report/calculate.action")
	@ResponseBody
	public Map<String, Object> calculate(String date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageReportService.calculate(date);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	@RequestMapping("/hr/wage/report/delete.action")
	@ResponseBody
	public Map<String, Object> delete(String date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageReportService.delete(date);
		modelMap.put("success", true);
		return modelMap;
	}

}
