package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ECNDetailLocationService;

@Controller
public class ECNDetailLocationController extends BaseController {
	@Autowired
	private ECNDetailLocationService ECNDetailLocationService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveECNDetailLocation.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDetailLocationService.saveECNDetailLocation(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/pm/bom/deleteECNDetailLocation.action")
	@ResponseBody
	public Map<String, Object> deleteECNDetailLocation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDetailLocationService.deleteECNDetailLocation(id, caller);
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
	@RequestMapping("/pm/bom/updateECNDetailLocation.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ECNDetailLocationService.updateECNDetailLocationById(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
