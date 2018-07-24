package com.uas.erp.controller.ma;

import java.util.List;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Configs;
import com.uas.erp.service.ma.ConfigService;

/**
 * 参数配置
 * 
 * @author yingp
 * 
 */
@Controller
public class ConfigController {

	@Autowired
	private ConfigService configService;

	@RequestMapping(value = "/ma/setting/configs.action", method = RequestMethod.GET)
	@ResponseBody
	public List<Configs> getConfigsByCaller(String caller,HttpSession session) {
		return configService.getConfigsByCaller(caller,session);
	}

	@RequestMapping(value = "/ma/setting/config.action", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject getConfigByCallerAndCode(String caller, String code) {
		return configService.getConfigByCallerAndCode(caller, code);
	}

	@RequestMapping(value = "/ma/setting/configs.action", method = RequestMethod.POST)
	@ResponseBody
	public void saveConfigs(String updated) {
		configService.beforeSave(updated);
		configService.saveConfigs(BaseUtil.parseGridStoreToMaps(updated));
		configService.afterSave(updated);
	}
	@RequestMapping(value = "/ma/setting/getConfigsByCondition.action", method = RequestMethod.GET)
	@ResponseBody
	public List<Configs> getConfigsByCondition(String condition) {
		return configService.getConfigsByCondition(condition);
	}
}
