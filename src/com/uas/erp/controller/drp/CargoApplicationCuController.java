package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.CargoApplicationCuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CargoApplicationCuController {

	@Autowired
	private CargoApplicationCuService cargoApplicationCuService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/distribution/saveCargoApplicationCu.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationCuService.saveCargoApplicationCu(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/distribution/updateCargoApplicationCu.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationCuService.updateCargoApplicationCuById(formStore,
				param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/distribution/deleteCargoApplicationCu.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationCuService.deleteCargoApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitCargoApplicationCu.action")
	@ResponseBody
	public Map<String, Object> submitCargoApplicationCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationCuService.submitCargoApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitCargoApplicationCu.action")
	@ResponseBody
	public Map<String, Object> resSubmitCargoApplicationCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationCuService.resSubmitCargoApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditCargoApplicationCu.action")
	@ResponseBody
	public Map<String, Object> auditCargoApplicationCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationCuService.auditCargoApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditCargoApplicationCu.action")
	@ResponseBody
	public Map<String, Object> resAuditCargoApplicationCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		cargoApplicationCuService.resAuditCargoApplicationCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
