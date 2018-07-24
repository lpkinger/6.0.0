package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.AssKindService;

@Controller
public class AssKindController extends BaseController {
	@Autowired
	private AssKindService assKindService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/ars/saveAssKind.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assKindService.saveAssKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteAssKind.action")
	@ResponseBody
	public Map<String, Object> deleteAssKind(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assKindService.deleteAssKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/updateAssKind.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assKindService.updateAssKindById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 核算类型
	 */
	@RequestMapping("/fa/ars/assKind.action")
	@ResponseBody
	public List<Map<String, Object>> getAssKind() {
		return assKindService.getAssKind();
	}

}
