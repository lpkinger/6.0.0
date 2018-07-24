package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AssKindDetailService;

@Controller
public class AssKindDetailController extends BaseController {
	@Autowired
	private AssKindDetailService assKindDetailService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/ars/saveAssKindDetail.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assKindDetailService.saveAssKindDetail(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/ars/deleteAssKindDetail.action")
	@ResponseBody
	public Map<String, Object> deleteAssKindDetail(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assKindDetailService.deleteAssKindDetail(id, caller);
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
	@RequestMapping("/fa/ars/updateAssKindDetail.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assKindDetailService.updateAssKindDetailById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
