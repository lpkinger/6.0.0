package com.uas.erp.service.ma;

import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

public interface CreateAccountBook {
	JSONObject validBusinessCode(String businessCode);
	JSONObject validBusinessName(String businessName);
	JSONObject applyCloud(Map<Object, Object> businessInfo, Map<Object, Object> accountInfo);
	JSONObject saveAccountInfo(HttpSession session, Map<Object, Object> businessInfo, Map<Object, Object> accountInfo);
	JSONObject getStep(HttpSession session);
	JSONObject active(String accountID);
	JSONObject getSource();
}
