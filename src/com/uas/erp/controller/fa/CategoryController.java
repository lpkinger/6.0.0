package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CategoryService;

@Controller
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@RequestMapping("/common/getAllCateTree.action")
	@ResponseBody
	public Map<String, Object> getAllHrOrgsTree(HttpSession session,
			String key, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> caClass = categoryService.getCateClass();
		Map<String, Object> data = new HashMap<String, Object>();
		for (String cls : caClass) {
			data.put(
					cls,
					categoryService.getAllCategoryTree(caller, "ca_class='"
							+ cls + "'"));
		}
		modelMap.put("data", data);
		modelMap.put("findToUi", categoryService.getToUi(key, caller));
		modelMap.put("success", true);
		return modelMap;
	}

}
