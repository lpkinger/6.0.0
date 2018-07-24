package com.uas.erp.service.common.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.AccessTokenService;

@Service
public class AccessTokenServiceImpl implements AccessTokenService {

	@Override
	public Map<String, Object> validFormManage(String accessToken) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		Response response = null;
		try {
			response = HttpUtil.sendGetRequest(Constant.manageHost() + "/public/token", params);
		} catch (Exception e) {
			return null;
		}
		String body = response.getResponseText();
		Map<String, Object> data = FlexJsonUtil.fromJson(body);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return data;
		} else {
			throw new SystemException(String.valueOf(data.get("error")));
		}
	}

	@Override
	public Map<String, Object> validFormB2b(String accessToken,Master master) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		Response response = null;
		String website = "";
		try {
			if(master!=null){
				website = master.getMa_b2bwebsite();
			}else{
				website = Constant.b2bHost();
			}
			response = HttpUtil.sendGetRequest(website + "/public/queriable/token", params);
		} catch (Exception e) {
			return null;
		}
		String body = response.getResponseText();
		Map<String, Object> data = FlexJsonUtil.fromJson(body);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return data;
		} else {
			throw new SystemException(String.valueOf(data.get("error")));
		}
	}
	
	@Override
	public Map<String, Object> validFormCc(String accessToken,Master master) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		Response response = null;
		String website = "";
		try {
			if(master!=null){
				website = master.getMa_ccwebsite();
			}
			response = HttpUtil.sendGetRequest(website + "/public/queriable/token", params);
		} catch (Exception e) {
			return null;
		}
		String body = response.getResponseText();
		Map<String, Object> data = FlexJsonUtil.fromJson(body);
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return data;
		} else {
			throw new SystemException(String.valueOf(data.get("error")));
		}
	}

}
