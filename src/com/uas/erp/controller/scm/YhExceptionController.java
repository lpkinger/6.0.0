package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.YhExceptionService;

@Controller
public class YhExceptionController {
	@Autowired
	private YhExceptionService yhExceptionService;

	/**
	 * 保存ComplaintRecords
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/scm/qc/saveYhException.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		yhExceptionService.saveYhException(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/scm/qc/updateYhException.action")
	@ResponseBody
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		yhExceptionService.updateYhExceptionById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/qc/deleteYhException.action")
	@ResponseBody
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		yhExceptionService.deleteYhException(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitYhException.action")
	@ResponseBody
	public Map<String, Object> submitYhException(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		yhExceptionService.submitYhException(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/qc/resSubmitYhException.action")
	@ResponseBody
	public Map<String, Object> resSubmitYhException(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		yhExceptionService.resSubmitYhException(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditYhException.action")
	@ResponseBody
	public Map<String, Object> auditYhException(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		yhExceptionService.auditYhException(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditYhException.action")
	@ResponseBody
	public Map<String, Object> resAuditYhException(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		yhExceptionService.resAuditYhException(id);
		modelMap.put("success", true);
		return modelMap;
	}
}
