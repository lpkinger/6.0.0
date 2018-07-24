package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.ma.SysInitService;

@Controller
@RequestMapping("ma/sysinit")
public class SysInitController {
	@Autowired
	private SysInitService sysInitService;
	@Autowired
	private EnterpriseService enterpriseService;

	@RequestMapping("/initImportData.action")
	public ModelAndView SysImportPage(String caller, String title) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("whoami", caller);
		modelMap.put("title", title);
		return new ModelAndView("/common/import", modelMap);
	}

	@RequestMapping(value = "/saveParamSet.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveParamSet(String update, String argType) {
		Map<String, Object> map = new HashMap<String, Object>();
		sysInitService.saveInitSet(update, argType);
		map.put("success", true);
		return map;
	}

	@RequestMapping(value = "/getImportDataItem.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getImportDataItem() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", sysInitService.getImportDataItem());
		map.put("success", true);
		return map;
	}

	/**
	 * 从标准帐套载入数据
	 * */
	@RequestMapping(value = "/InitDataFromStandard.action", method = RequestMethod.POST)
	@ResponseBody
	public void InitDataFromStandard(String table) {
		sysInitService.InitDataFromStandard(table);
	}

	@RequestMapping(value = "/InitHrDataFromStandard.action", method = RequestMethod.POST)
	@ResponseBody
	public void InitHrDataFromStandard() {
		sysInitService.InitHrDataFromStandard();
	}

	@RequestMapping(value = "/InitProcessDataFromStandard.action", method = RequestMethod.POST)
	@ResponseBody
	public void InitProcessDataFromStandard() {
		sysInitService.InitProcessDataFromStandard();
	}

	@RequestMapping(value = "/finishInit.action", method = RequestMethod.POST)
	@ResponseBody
	public void finishInit() {
		sysInitService.finishInit();
		enterpriseService.clearMasterCache();
	}
	/**
	 * beforeDelete方法
	 * @param caller
	 * @param keyValue
	 */
	@RequestMapping(value = "/beforeDelete.action", method = RequestMethod.POST)
	@ResponseBody
	public void beforeDelete(String status,int keyValue,String table,String statuscode,String keyField){
		sysInitService.beforeDelete(status,keyValue,table,statuscode,keyField);
	} 
	
	@RequestMapping(value = "/saveBefore.action", method = RequestMethod.POST)
	@ResponseBody
	public void saveBefore(String caller, int keyValue) {
		sysInitService.saveBefore(caller, keyValue);
	}

	@RequestMapping(value = "/saveAfter.action", method = RequestMethod.POST)
	@ResponseBody
	public void saveAfter(String caller, int keyValue) {
		sysInitService.saveAfter(caller, keyValue);
	}
	/**
	 * wusy
	 */
	@RequestMapping("/insertReadStatus.action")
	@ResponseBody
	public Map<String,Object> insertReadStatus(HttpSession session,String status,int man,String sourcekind){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		session.setAttribute("UCloud",true);
		sysInitService.finishUcloud();
		sysInitService.insertReadStatus(status,man,sourcekind);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * wusy
	 */
	@RequestMapping("/getStatus.action")
	@ResponseBody
	public Map<String,Object> getStatus(int man,String em_code){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		boolean bool = sysInitService.getStatus(man,em_code);
		modelMap.put("status", bool);
		modelMap.put("success", true);
		return modelMap;
	} 
	/**
	 * 拿到关于fields的数据
	 * 
	 * @param tablename
	 *            表名
	 * @param fields
	 *            字段
	 * @param condition
	 *            带入的条件
	 */
	@RequestMapping("/getFieldsDatas.action")
	@ResponseBody
	public Map<String, Object> getFieldsDatas(HttpSession session, String fields, String relfields,String tablename, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", sysInitService.getFieldsDatas(tablename, fields,relfields, condition).toString());
		modelMap.put("success", true);
		return modelMap;
	}
	
}
