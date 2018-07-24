package com.uas.mobile.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.mobile.service.impl.MobilePushAdminServiceImpl;

@Controller
public class MobilePushAdminController {
	
	@Autowired
	MobilePushAdminServiceImpl mobilePushAdminServiceImpl;
	@Autowired
	private EnterpriseService enterpriseService;
	
	@RequestMapping("/mobile/getAdminUser.action")
	@ResponseBody
	public Map<String, Object> getAdminUser(HttpServletRequest request,HttpSession session,String master) {
		Map<String,Object> model = new HashMap<String, Object>();
		Master masters = enterpriseService.getMasterByName(master);
		if (masters != null) {
			SpObserver.putSp(master);
			model.put("success", true);
			model.put("data", mobilePushAdminServiceImpl.getAdminUser());
			return model;
		}else {
			model.put("error", "账套不存在！");
			return model;
		}
			
		
	}		
}
