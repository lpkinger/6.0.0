package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.EmpTransferCheckService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class EmpTransfersCheckController {

	@Autowired
	private EmpTransferCheckService empTransferCheckService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("hr/emplmana/saveEmpTransferCheck.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckService.save(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("hr/emplmana/updateEmpTransferCheck.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckService.updateEmpTransferCheckById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("hr/emplmana/deleteEmpTransferCheck.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckService.deleteEmpTransferCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/emplmana/submitEmpTransferCheck.action")
	@ResponseBody
	public Map<String, Object> submitEmpTransferCheck(String caller, int id) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckService.submitEmpTransferCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitEmpTransferCheck.action")
	@ResponseBody
	public Map<String, Object> resSubmitTrain(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckService.resSubmitEmpTransferCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/emplmana/auditEmpTransferCheck.action")
	@ResponseBody
	public Map<String, Object> auditEmpTransferCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckService.auditEmpTransferCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 检测
	 */
	@RequestMapping("/hr/emplmana/check.action")
	@ResponseBody
	public Map<String, Object> check(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckService.check(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转人员异动转任务交接
	 */
	@RequestMapping("/hr/emplmana/turnEmpTransferCheck.action")
	@ResponseBody
	public Map<String, Object> turnEmpTransferCheck(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		empTransferCheckService.turnEmpTransferCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
