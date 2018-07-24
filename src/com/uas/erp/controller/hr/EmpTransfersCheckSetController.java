package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.EmpTransferCheckSetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class EmpTransfersCheckSetController {

	@Autowired
	private EmpTransferCheckSetService empTransferCheckSetService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("hr/emplmana/saveEmpTransferCheckSet.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckSetService.save(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("hr/emplmana/updateEmpTransferCheckSet.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckSetService.updateEmpTransferCheckSetById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("hr/emplmana/deleteEmpTransferCheckSet.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckSetService.deleteEmpTransferCheckSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
