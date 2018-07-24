package com.uas.mobile.controller.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.mobile.service.VisitRecordService;

@Controller("mobileVisitRecordController")
public class VisitRecordController {

	@Autowired
	private VisitRecordService mobileVisitRecordService;

	/**
	 * 根据输入的客户名关键字模糊查询客户编号和客户名
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param name
	 *            输入的客户名关键字
	 * @param size
	 *            每页的条数
	 * @param page
	 *            第几页
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/mobile/crm/getLikeCuName.action")
	@ResponseBody
	public Map<String, Object> getLikeCuName(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, String name,
			Integer size, Integer page) throws UnsupportedEncodingException {
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String result = mobileVisitRecordService
				.getCustomerCodeNameByNameFuzzy(
						URLDecoder.decode(name, "UTF-8"), size, page);
		modelMap.put("success", true);
		modelMap.put("result", result);
		return modelMap;
	}

	/**
	 * 根据输入的客户编号关键字模糊查询客户编号和客户名
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param code
	 *            输入的客户编号关键字
	 * @param size
	 *            每页的条数
	 * @param page
	 *            第几页
	 * @return
	 */
	@RequestMapping("/mobile/crm/getLikeCuCode.action")
	@ResponseBody
	public Map<String, Object> getLikeCuCode(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, String code,
			Integer size, Integer page) {
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String result = mobileVisitRecordService
				.getCustomerCodeNameByCodeFuzzy(code, size, page);
		modelMap.put("success", true);
		modelMap.put("result", result);
		return modelMap;
	}
}
