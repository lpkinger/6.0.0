package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.CategoryDocService;

@Controller
public class CategoryDocController extends BaseController {
	@Autowired
	private CategoryDocService categoryDocService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/fa/ars/saveCategoryDoc.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryDocService.saveCategoryDoc(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteCategoryDoc.action")
	@ResponseBody
	public Map<String, Object> deleteCategoryDoc(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryDocService.deleteCategoryDoc(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/ars/updateCategoryDoc.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		categoryDocService.updateCategoryDocById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
