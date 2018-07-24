package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.BookKindService;

@Controller
public class BookKindController {
	@Autowired
	private BookKindService bookKindService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/saveBookKind.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookKindService.saveBookKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/oa/publicAdmin/book/basicData/deleteBookKind.action")
	@ResponseBody
	public Map<String, Object> deleteBookKind(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookKindService.deleteBookKind(id, caller);
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
	@RequestMapping("/oa/publicAdmin/book/basicData/updateBookKind.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bookKindService.updateBookKindById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
