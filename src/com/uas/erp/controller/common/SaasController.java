package com.uas.erp.controller.common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.InitService;

@Controller
@RequestMapping("/common/saas")
public class SaasController {

	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private InitService initService;

	@RequestMapping(value = "/master.action", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap getSaasMaster(HttpServletRequest request, @RequestParam(value = "basePath", required = true) String basePath) {
		Master thisMaster = getMasterByPath(basePath);
		if (thisMaster != null) {
			ModelMap modelMap = new ModelMap();
			modelMap.put("ma_name", thisMaster.getMa_name());
			modelMap.put("ma_function", thisMaster.getMa_function());
			modelMap.put("init", thisMaster.isInit());
			modelMap.put("enable", thisMaster.isEnable());
			modelMap.put("type", thisMaster.getMa_installtype());
			// 检测数据库连接bean是否已创建
			if (ContextUtil.getBean(thisMaster.getMa_name()) == null)
				BaseUtil.createDataSource(thisMaster);
			// 游客
			if (thisMaster.isGuest()) {
				String tempName = null;
				String cookieName = "s_username_" + thisMaster.getMa_name();
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if (cookie.getName().equals(cookieName)) {
							tempName = cookie.getValue();
							break;
						}
					}
				}
				// 给新的游客新增一条employee
				if (tempName == null) {
					Employee temp = employeeService.generate(thisMaster.getMa_name());
					tempName = temp.getEm_code();
				}
				modelMap.put("tempName", tempName);
			}
			return modelMap;
		}
		return null;
	}

	@RequestMapping("/error.action")
	public ModelAndView saasErrorPage() {
		return new ModelAndView("/saas/error_domain");
	}

	@RequestMapping("/disable.action")
	public ModelAndView saasDisablePage() {
		return new ModelAndView("/saas/error_disable");
	}

	private Master getMasterByPath(String basePath) {
		String baseDomain = BaseUtil.getXmlSetting("saas.domain");
		if (baseDomain == null || basePath.indexOf(baseDomain) == -1)
			return null;
		int idx = 0;
		if ((idx = basePath.indexOf("//")) > -1) {
			basePath = basePath.substring(idx + 2);
		}
		String domain = basePath.substring(0, basePath.indexOf(baseDomain) - 1);
		if (domain == null)
			return null;
		List<Master> masters = enterpriseService.getMasters();
		Master thisMaster = null;
		for (Master master : masters) {
			if (master.getMa_domain() != null && master.getMa_domain().equals(domain)) {
				thisMaster = master;
				break;
			}
		}
		if (thisMaster == null) {
			thisMaster = enterpriseService.getMasterByDomain(domain);
			if (thisMaster != null)
				enterpriseService.clearMasterCache();
		}
		return thisMaster;
	}

	@RequestMapping(value = "/search.action", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, String>> searchSaasMaster(@RequestParam(value = "key", required = true) String key)
			throws UnsupportedEncodingException {
		List<Master> thisMasters = getMasterBySearch(key);
		if (thisMasters.size() > 0) {
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			String baseDomain = BaseUtil.getXmlSetting("saas.domain");
			for (Master master : thisMasters) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("ma_url", "http://" + master.getMa_domain() + "." + baseDomain);
				map.put("ma_function", master.getMa_function());
				list.add(map);
			}
			return list;
		}
		return null;
	}

	private List<Master> getMasterBySearch(String key) {
		List<Master> masters = enterpriseService.getMasters();
		List<Master> thisMasters = new ArrayList<Master>();
		for (Master master : masters) {
			if (master.getMa_function().contains(key)) {
				thisMasters.add(master);
			}
		}
		return thisMasters;
	}

	@RequestMapping("/common/sysinitnavigation.action")
	@ResponseBody
	public Map<String, Object> sysinitnavigation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<String> admininfo = initService.getAdminInfo();
		List<Map<String, Object>> color = initService.sysInitNavigation();
		modelMap.put("color", color);
		modelMap.put("admininfo", admininfo);
		return modelMap;
	}

	@RequestMapping("/common/checkData.action")
	@ResponseBody
	public Map<String, Object> checkData(HttpServletRequest request, HttpServletResponse response, String table, String value)
			throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean res = initService.checkData(table, value);
		modelMap.put("res", res);
		return modelMap;
	}

}
