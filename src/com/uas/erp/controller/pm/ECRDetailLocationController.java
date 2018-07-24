package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ECRDetailLocationService;

@Controller
public class ECRDetailLocationController extends BaseController {
	@Autowired
	private ECRDetailLocationService ECRDetailLocationService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveECRDetailLocation.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDetailLocationService.saveECRDetailLocation(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECR数据 包括ECR明细
	 */
	@RequestMapping("/pm/bom/deleteECRDetailLocation.action")
	@ResponseBody
	public Map<String, Object> deleteECRDetailLocation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDetailLocationService.deleteECRDetailLocation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/updateECRDetailLocation.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECRDetailLocationService.updateECRDetailLocationById(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
