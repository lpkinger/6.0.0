package com.uas.erp.controller.ac;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uas.erp.ac.service.common.CompanyNewService;
/**
 * 
 * @author wuyx
 * @time 创建时间：2018年3月26日
 *
 */
@RestController
public class CompanyNewController {
	@Autowired
	private CompanyNewService companyNewService;
	/**
	 * 企业注册检测
	 */
	@RequestMapping("ac/checkCompanyExist.action")
	@ResponseBody
	public Map<String, Object> checkCompanyExist(String name) {
		return companyNewService.checkCompanyExist(name);
	}
	
	/**
	 * 新增邀请记录
	 * *
	 */
	@RequestMapping("ac/getInviteUrl.action")
	@ResponseBody
	public Map<String, Object> getInviteUrl(String name,String type,String userTel,String vendusername) {
		Map<String, Object> res = new HashMap<>();
		if(type.equals("ptop")){
			res = companyNewService.getInviteUrl(name,vendusername,userTel);
		}else{//群分享
			res = companyNewService.getInviteUrl();
		}
		return res;
	}
}
