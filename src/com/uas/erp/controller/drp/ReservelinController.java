package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.ReservelinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ReservelinController {

	@Autowired
	private ReservelinService reservelinService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveReservelin.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		reservelinService.saveReservelin(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateReservelin.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		reservelinService.updateReservelinById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteReservelin.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		reservelinService.deleteReservelin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitReservelin.action")
	@ResponseBody
	public Map<String, Object> submitReservelin(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		reservelinService.submitReservelin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitReservelin.action")
	@ResponseBody
	public Map<String, Object> resSubmitReservelin(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		reservelinService.resSubmitReservelin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditReservelin.action")
	@ResponseBody
	public Map<String, Object> auditReservelin(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		reservelinService.auditReservelin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditReservelin.action")
	@ResponseBody
	public Map<String, Object> resAuditReservelin(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		reservelinService.resAuditReservelin(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
