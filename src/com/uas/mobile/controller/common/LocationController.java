package com.uas.mobile.controller.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.mobile.model.Location;
import com.uas.mobile.service.LocationService;

/**
 * 位置记录请求
 * @author suntg
 * @date 2014年10月27日16:52:56
 *
 */
@Controller("mobileLocationController")
public class LocationController {
	
	@Autowired
	private LocationService locationService;
	
	/**
	 * 保存请求
	 * @return
	 */
	@RequestMapping("/mobile/postLocation.action")
	@ResponseBody
	public Map<String, Object> saveLocation(HttpServletRequest request, HttpServletResponse response,
			String data,String currentMaster) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		locationService.saveLocation(data,currentMaster);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 根据用户编号查询
	 * @param request
	 * @param response
	 * @param session
	 * @param emcode 员工编号
	 * @return
	 */
	/*@RequestMapping("/mobile/common/getLocationByEmcode.action")
	@ResponseBody
	public Map<String, Object> getLocationByEmcode(HttpServletRequest request, HttpServletResponse response,
			String emcode) {
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		System.out.println(emcode);
		List<Location> locations = locationService.getLocationByEmcode(emcode);
		String result = "";
		result = FlexJsonUtil.toJsonArray(locations);
		modelMap.put("success", true);
		modelMap.put("result", result);
		return modelMap;
	}*/

}
