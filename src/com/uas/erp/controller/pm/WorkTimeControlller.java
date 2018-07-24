package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.WorkTimeService;

@Controller
public class WorkTimeControlller extends BaseController {
	@Autowired
	private WorkTimeService workTimeService;

	/**
	 * 保存WorkTime
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/craft/saveWorkTime.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workTimeService.saveWorkTime(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/craft/deleteWorkTime.action")
	@ResponseBody
	public Map<String, Object> deleteWorkTime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workTimeService.deleteWorkTime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/craft/updateWorkTime.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workTimeService.updateWorkTimeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
