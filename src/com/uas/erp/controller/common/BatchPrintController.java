package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.common.BatchPrintService;


@Controller
public class BatchPrintController {
	@Autowired
	private BatchPrintService batchPrintService;

	
	@RequestMapping("/common/BatchPrintController/batchPrint.action")  
	@ResponseBody 
	public Map<String, Object> print(HttpSession session, String idS,String reportName,String condition,String title,String todate,String dateFW,String fromdate,String enddate) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys=null;		
		keys = batchPrintService.printBatch(idS,language, employee, reportName, condition, title,todate,dateFW,fromdate,enddate);		
		modelMap.put("success", true);		
		modelMap.put("keyData",keys);
		return modelMap;
	}
	

}
