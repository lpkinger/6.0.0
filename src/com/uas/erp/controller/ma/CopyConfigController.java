package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Configs;
import com.uas.erp.service.ma.CopyConfigService;

/**
 * 参数配置
 * 
 * @author yingp
 * 
 */
@Controller
public class CopyConfigController {

	@Autowired
	private CopyConfigService copyConfigService;
	
	/**
	 * 更改
	 */
	@RequestMapping(value = "/ma/setting/updateCopyConfigs.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller,String formCaller, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		copyConfigService.updateCopyConfigByCaller(caller,formCaller,gridStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping(value = "/ma/setting/deleteCopyConfigs.action")  
	@ResponseBody 
	public Map<String, Object> delete(String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		copyConfigService.deleteCopyConfigByCondition(condition);
		modelMap.put("success", true);
		return modelMap;
	}
	
}
