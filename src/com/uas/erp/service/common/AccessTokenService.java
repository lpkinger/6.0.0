package com.uas.erp.service.common;

import java.util.Map;

import com.uas.erp.model.Master;

public interface AccessTokenService {

	/**
	 * 请求管理平台校验token串，并返回用户信息
	 * 
	 * @param accessToken
	 * @return
	 */
	Map<String, Object> validFormManage(String accessToken);
	
	/**
	 * 请求B2B平台校验token串，并返回用户信息
	 * 
	 * @param accessToken
	 * @return
	 */
	Map<String, Object> validFormB2b(String accessToken,Master master);
	
	/**
	 * 请求产城平台校验token串，并返回用户信息
	 * 
	 * @param accessToken
	 * @return
	 */
	Map<String, Object> validFormCc(String accessToken,Master master);

}
