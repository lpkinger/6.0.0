package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MakeKindService;

@Controller
public class MakeKindController extends BaseController {

	@Autowired
	private MakeKindService makeKindService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/pm/make/saveMakeKind.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeKindService.saveMakeKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteMakeKind.action")
	@ResponseBody
	public Map<String, Object> deleteMakeKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeKindService.deleteMakeKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/pm/make/updateMakeKind.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeKindService.updateMakeKindById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
