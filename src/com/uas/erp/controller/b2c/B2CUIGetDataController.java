package com.uas.erp.controller.b2c;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.b2c.B2CGetDataService;
import com.uas.erp.service.b2c.B2CSettingService;

@Controller
public class B2CUIGetDataController {
  @Autowired
  private B2CGetDataService b2CGetDataService;
  
    /**
     * B2C 获取datalist的数量
	 */
	@RequestMapping("/b2c/getDatalistCount.action")
	@ResponseBody
	public Map<String, Object> getDatalistCount(String caller,String condition,String table,String fields) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",b2CGetDataService.getDatalistCount(caller,condition,table,fields));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
     * B2C 获取datalist的数据
	 */
	@RequestMapping("/b2c/getDatalistData.action")
	@ResponseBody
	public Map<String, Object> getDatalistData(String caller,String condition,String table,String fields, String orderby, int page, int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",b2CGetDataService.getDatalistData(caller,condition,table,fields,orderby,page,pageSize));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * B2C 获取grid数据
	 *
	 */
	@RequestMapping("/b2c/getFieldsDatas.action")
	@ResponseBody
	public Map<String, Object> getFieldsDatas(HttpSession session, String fields, String caller, String condition, String tablename) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", b2CGetDataService.getFieldsDatas(caller, fields, condition, tablename));
		modelMap.put("success", true);
		return modelMap;
	}
	
}
