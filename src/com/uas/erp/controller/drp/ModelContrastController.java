package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.drp.ModelContrastService;

@Controller
public class ModelContrastController {
	@Autowired
	private ModelContrastService modelContrastService;

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/drp/distribution/updateModelContrast.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelContrastService.updateModelContrast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
