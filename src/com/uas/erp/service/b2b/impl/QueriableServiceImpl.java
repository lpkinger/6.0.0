package com.uas.erp.service.b2b.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.EnterpriseDetail;
import com.uas.erp.model.Master;
import com.uas.erp.model.QueriableMember;
import com.uas.erp.model.QueriableUser;
import com.uas.erp.service.b2b.QueriableService;

@Service
public class QueriableServiceImpl implements QueriableService {

	@Override
	public Map<String, Object> findMembersByVendor(String name, String shortName, Long uu) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			try {
				Map<String, String> params = new HashMap<String, String>();
				if (name != null)
					params.put("name", name);
				if (shortName != null)
					params.put("shortName", shortName);
				if (uu != null)
					params.put("uu", StringUtil.valueOf(uu));
				Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/members", params);
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					String data = response.getResponseText();
					if (StringUtil.hasText(data))
						return FlexJsonUtil.fromJson(data);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			BaseUtil.showError("您还没有启用平台数据交互功能！");
		return null;
	}

	@Override
	public QueriableMember findMemberByUU(long uu) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			try {
				Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/members/" + uu, null);
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					String data = response.getResponseText();
					if (StringUtil.hasText(data))
						return FlexJsonUtil.fromJson(data, QueriableMember.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			BaseUtil.showError("您还没有启用平台数据交互功能！");
		return null;
	}

	@Override
	public QueriableUser findUserByUU(long enUU, long userUU) {
		Master master = SystemSession.getUser().getCurrentMaster();
		if (master.getMa_b2bwebsite() != null) {
			try {
				Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/members/" + enUU + "/users/"
						+ userUU, null);
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					String data = response.getResponseText();
					if (StringUtil.hasText(data))
						return FlexJsonUtil.fromJson(data, QueriableUser.class);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			BaseUtil.showError("您还没有启用平台数据交互功能！");
		return null;
	}

	@Override
	public List<Map<String, String>> findEnterprisesByKey(String key) {
		Master master = SystemSession.getUser().getCurrentMaster();
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (master.getMa_b2bwebsite() != null) {
			try {
				Map<String, String> params = new HashMap<String, String>();
				if (key != null) {
					params.put("name", key);
					params.put("shortName", key);
				}
				Response response = HttpUtil.sendGetRequest(master.getMa_b2bwebsite() + "/public/queriable/enterprises", params);
				if (response.getStatusCode() == HttpStatus.OK.value()) {
					String data = response.getResponseText();
					System.out.println(response.getResponseText());
					if (StringUtil.hasText(data)) {
						List<EnterpriseDetail> enterpriseDetails = FlexJsonUtil.fromJsonArray(data, EnterpriseDetail.class);
						if (enterpriseDetails.size() > 0) {
							for (EnterpriseDetail member : enterpriseDetails) {
								Map<String, String> map = new HashMap<String, String>();
								map.put("en_url", member.getUrl());
								map.put("en_name", member.getName());
								map.put("en_shortname", member.getShortName());
								map.put("en_uu", member.getUu().toString());
								map.put("en_address", member.getAddress());
								map.put("en_management", member.getManagement());
								map.put("en_info", member.getInfos());
								map.put("en_products", member.getProducts());
								list.add(map);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			BaseUtil.showError("您还没有启用平台数据交互功能！");
		return list;
	}

}
