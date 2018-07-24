package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.EmpWorkDateModelService;

@Controller
public class EmpWorkDateModelController {

	@Autowired
	private EmpWorkDateModelService empWorkDateModelService;

	/**
	 * 保存
	 */
	@RequestMapping("/hr/attendance/saveEmpWorkDateModel.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateModelService.saveEmpWorkDateModel(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/attendance/updateEmpWorkDateModel.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateModelService.updateEmpWorkDateModelById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteEmpWorkDateModel.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateModelService.deleteEmpWorkDateModel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/attendance/setEmpWorkDateModel.action")
	@ResponseBody
	public Map<String, Object> setEmpWorkDateModel(String caller, int wdid,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateModelService.setEmpWorkDateModel(wdid, condition, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消设置
	 * 
	 * @param session
	 * @param wdid
	 * @param condition
	 * @return
	 */
	@RequestMapping("/hr/attendance/cancelEmpWorkDateModel.action")
	@ResponseBody
	public Map<String, Object> cancelEmpWorkDateModel(String caller,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empWorkDateModelService.cancelEmpWorkDateModel(condition, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 班次模板设置后更新emp_work_datelist 信息
	 * 
	 * @param session
	 * @param ids
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	@RequestMapping("/hr/attendance/updateEmpWorkDateList.action")
	@ResponseBody
	public Map<String, Object> updateEmpWorkDateList(String caller,
			String[] ids, String startdate, String enddate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String r = empWorkDateModelService.updateEmpWorkDateList(ids,
				startdate, enddate, caller);
		if (r.equals("ok")) {
			modelMap.put("success", true);
		} else {
			modelMap.put("error", r);
			modelMap.put("success", false);
		}
		return modelMap;
	}

	@RequestMapping("/hr/attendance/loadGridDate.action")
	@ResponseBody
	public Map<String, Object> loadGridDate(String caller, String emid,
			String startdate, String enddate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String dataString = empWorkDateModelService.loadGridDate(emid,
				startdate, enddate, caller);
		modelMap.put("success", true);
		modelMap.put("data", dataString);
		return modelMap;
	}

}
