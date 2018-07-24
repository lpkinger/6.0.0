package com.uas.b2c.service.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.uas.b2c.model.AccessToken;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.model.Master;

@Component
public class B2CAccessTokenService {

	private Map<Long, AccessToken> accessTokens = new ConcurrentHashMap<Long, AccessToken>();

	/**
	 * @Description 让其他成员直接获取有效的accessToken
	 * @param master
	 *            作为参数传递，避免{@code getAccessToken}不在主线程，从而无法得到 {@code SystemSession}
	 * @return access_token
	 */
	public String getAccessToken(Master master) {
		if (!accessIsValid(master.getMa_uu())) {
			AccessToken accessToken = getNextAccessToken(master);
			accessTokens.put(master.getMa_uu(), accessToken);
		}
		return accessTokens.get(master.getMa_uu()).getAccess_token();
	}

	/**
	 * 判断当前token是否有效
	 * 
	 * @return
	 */
	private boolean accessIsValid(Long uu) {
		if (accessTokens != null && accessTokens.containsKey(uu)) {
			return !accessTokens.get(uu).isExpired();
		}
		return false;
	}

	/**
	 * 调用接口获取accesstoken
	 */
	public AccessToken getNextAccessToken(Master master) {
		String url = master.getB2CUrl() + "/api/token";
		AccessToken accessToken = null;
		try {
			Response response = HttpUtil.sendGetRequest(url + "?id=" + master.getMa_uu() + "&secret=" + master.getMa_accesssecret(), null,
					true);
			if (response.getStatusCode() == HttpStatus.OK.value()) {// 获取成功
				// 解析为AccessToken对象并装载数据
				accessToken = FlexJsonUtil.fromJson(response.getResponseText(), AccessToken.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;

	}

}
