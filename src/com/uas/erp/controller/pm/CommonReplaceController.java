package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.CommonReplaceService;

@Controller
public class CommonReplaceController extends BaseController {
	@Autowired
	private CommonReplaceService commonReplaceService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveCommonReplace.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonReplaceService.saveCommonReplaceService(param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/bom/deleteCommonReplace.action")
	@ResponseBody
	public Map<String, Object> deleteProdReplace(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonReplaceService.deleteCommonReplaceService(id, caller);
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
	@RequestMapping("/pm/bom/updateCommonReplace.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,String caller,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonReplaceService.updateCommonReplaceServiceById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
