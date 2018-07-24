package com.uas.erp.controller.ma;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.service.ma.CreateAccountBook;

@Controller
public class CreateAccountBookController {

	@Autowired
	private CreateAccountBook createAccountBook;

	@RequestMapping(value = "/ma/createAccountBook/validBusinessCode.action", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject validBusinessCode(HttpServletRequest request, HttpServletResponse response, String businessCode) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		
		return createAccountBook.validBusinessCode(businessCode);
	}
	
	@RequestMapping(value = "/ma/createAccountBook/validBusinessName.action", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject validBusinessName(HttpServletRequest request, HttpServletResponse response, String businessName) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");

		return createAccountBook.validBusinessName(businessName);
	}
	
	@RequestMapping(value = "/ma/createAccountBook/applyUsoftCloud.action", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject applyCloud(HttpServletRequest request, HttpServletResponse response, String businessInfo, String accountInfo) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		
		Map<Object,Object> bInfo = BaseUtil.parseFormStoreToMap(businessInfo);
		Map<Object,Object> aInfo = BaseUtil.parseFormStoreToMap(accountInfo);
		return createAccountBook.applyCloud(bInfo, aInfo);
	}
	
	@RequestMapping(value = "/ma/createAccountBook/saveAccountInfo.action", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject saveAccountInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session, String businessInfo, String accountInfo) {
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		
		Map<Object,Object> bInfo = BaseUtil.parseFormStoreToMap(businessInfo);
		Map<Object,Object> aInfo = BaseUtil.parseFormStoreToMap(accountInfo);
		return createAccountBook.saveAccountInfo(session, bInfo, aInfo);
	}
	
	@RequestMapping(value = "/ma/createAccountBook/getAccountInfo.action", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getStep(HttpSession session) {
		
		return createAccountBook.getStep(session);
	}
	
	@RequestMapping(value = "/ma/createAccountBook/active.action", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject active(String accountID) {
		
		return createAccountBook.active(accountID);
	}
	
	@RequestMapping(value = "/ma/createAccountBook/getSource.action", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject getSource() {
		
		return createAccountBook.getSource();
	}
}
