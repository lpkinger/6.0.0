package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.JasperReportPrintService;


@Controller
public class JasperReportPrintController {

	@Autowired
	private JasperReportPrintService jasperReportPrintService;
	/*
	 * 单据界面 打印+按条件打印
	 */
	@RequestMapping(value = "common/JasperReportPrint/printDefault.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> printById(HttpServletRequest request, int id, String caller, String reportname,boolean isProdIO) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info",jasperReportPrintService.print(id,caller,reportname,isProdIO,request));
		map.put("success", true);
		return map;

	}
	/*
	 * 打印界面
	 */
	@RequestMapping(value = "common/JasperReportPrint/print.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> print(HttpServletRequest request, String params, String caller,String reportname) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info",jasperReportPrintService.print(params,caller,reportname,request));
		map.put("success", true);
		return map;
	}
	/*
	 * 批量打印界面
	 */
	@RequestMapping(value = "common/JasperReportPrint/batchPrint.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> batchPrint(HttpServletRequest request, String ids, String caller,String reportname) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info",jasperReportPrintService.batchPrint(ids,caller,reportname,request));
		map.put("success", true);
		return map;
	}
	/*
	 * 打印设置 
	 */
	@RequestMapping(value = "common/JasperReportPrint/getCount.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getCount(HttpServletRequest request, String condition) {
		Map<String, Object> modelMap =new HashMap<String, Object>();
		modelMap.put("count", jasperReportPrintService.getCount(condition));		
		return modelMap;
	}
	/*
	 * 打印设置 
	 */
	@RequestMapping(value = "common/JasperReportPrint/getData.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getData(HttpServletRequest request, String condition,int page,
			int pageSize) {
		Map<String, Object> modelMap =jasperReportPrintService.getData(condition, page, pageSize);
		return modelMap;
	}
	/*
	 * 打印设置 保存
	 */
	@RequestMapping(value = "common/JasperReportPrint/save.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jasperReportPrintService.save(param);
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * 打印设置 删除
	 */
	@RequestMapping(value = "common/JasperReportPrint/delete.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jasperReportPrintService.delete(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/*
	 * 按条件打印
	 */
	@RequestMapping("common/JasperReportPrint/getFields.action")
	@ResponseBody
	public Map<String, Object> getFields(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("datas", jasperReportPrintService.getFields(caller));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("common/JasperReportPrint/getPrintType.action")
	@ResponseBody
	public Map<String, Object> getPrintType(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(session.getAttribute("en_admin")==null){
			jasperReportPrintService.setPrintType(session);
		}
		modelMap.put("printtype", session.getAttribute("en_admin"));
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("common/JasperReportPrint/setPrintType.action")
	@ResponseBody
	public Map<String, Object> setPrintType(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jasperReportPrintService.setPrintType(session);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("common/JasperReportPrint/JasperGetReportnameByProcedure.action")
	@ResponseBody
	public Map<String, Object> JasperGetReportnameByProcedure(HttpSession session,String ids,String caller,String reportname) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String res=jasperReportPrintService.JasperGetReportnameByProcedure(ids,caller,reportname);
		if(res!=null&&res!=""){
			modelMap.put("reportname", res);
			modelMap.put("success", true);
		}else{
			modelMap.put("success", false);
		}
		
		return modelMap;
	}
}
