package com.uas.erp.controller.hr;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.hr.KpibillService;

@Controller
public class KpibillController {

	@Autowired
	private KpibillService  kpibillService;
	/**
	 * 修改
	 */
	@RequestMapping("hr/kpi/updateKpibill.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillService.updateKpibill(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("hr/kpi/deleteKpibill.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillService.deleteKpibill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 * @throws ParseException 
	 */
	@RequestMapping("hr/kpi/submitKpibill.action")
	@ResponseBody
	public Map<String, Object> submitKpibill(String caller, int id) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillService.submitKpibill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 * @throws ParseException 
	 */
	@RequestMapping("hr/kpi/resSubmitKpibill.action")
	@ResponseBody
	public Map<String, Object> resSubmitKpibill(String caller, int id) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillService.resSubmitKpibill(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("hr/kpi/printKpibill.action")
	@ResponseBody
	public Map<String, Object> printKpibill(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = kpibillService.printKpibill(id, caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	/**
	 * 分数来源查询
	 */
	@RequestMapping("hr/kpi/getScorefrom.action")
	@ResponseBody
	public Map<String, Object> getScorefrom(String kt_kdbid,String kt_bemanid,String ktd_kiid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = kpibillService.getScorefrom(kt_kdbid, kt_bemanid, ktd_kiid);
		return modelMap;
	}
}
