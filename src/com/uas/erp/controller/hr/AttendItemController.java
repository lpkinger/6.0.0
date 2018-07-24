package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.AttendItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AttendItemController {

	@Autowired
	private AttendItemService attendItemService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/attendance/saveAttendItem.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendItemService.saveAttendItem(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/attendance/updateAttendItem.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendItemService.updateAttendItemById(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteAttendItem.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendItemService.deleteAttendItem(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 考勤计算
	 */
	@RequestMapping("/hr/attendance/attendDataCom.action")
	@ResponseBody
	public Map<String, Object> attendDataCom(String caller, String startdate,
			String enddate, String emcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendItemService.attendDataCom(emcode, startdate, enddate, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 考勤计算
	 */
	@RequestMapping("/hr/attendance/cardLogImp.action")
	@ResponseBody
	public Map<String, Object> cardLogImp(String caller, String startdate,
			String enddate, String cardcode, String yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendItemService.cardLogImp(cardcode, startdate, enddate, yearmonth,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
