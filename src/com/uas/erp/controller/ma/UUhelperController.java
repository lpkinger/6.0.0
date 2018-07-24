package com.uas.erp.controller.ma;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UUhelperController {
	//uu助手列表
	@RequestMapping("/ma/uuHelperList.action")
	private ModelAndView openNewsViewPage(Integer page, Integer pageSize) {
		page = page == null ? 1 : page;
		pageSize = pageSize == null ? 10 : pageSize;
		ModelMap map = new ModelMap();
		map.put("page", page);
		map.put("pageSize", pageSize);
		return new ModelAndView("/ma/uuHelper", map);
	}
	
}
