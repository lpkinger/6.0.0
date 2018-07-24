package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.GroupService;

@Controller
public class GroupController {
	
	@Autowired
	private GroupService groupService;

	/**
	 * 更新帐套设置
	 * @param session
	 * @param data
	 * @return
	 */
	@RequestMapping("/ma/group/updateBaseDataSet.action")
	@ResponseBody
	public Map<String, Object> updateBaseDataSet(HttpSession session, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		groupService.updateBaseDataSet(data);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新同步设置
	 * @param session
	 * @param data
	 * @return
	 */
	@RequestMapping("/ma/group/updatePostStyleSet.action")
	@ResponseBody
	public Map<String, Object> updatePostStyleSet(HttpSession session, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		groupService.updatePostStyleSet(data);
		modelMap.put("success", true);
		return modelMap;
	}
}
