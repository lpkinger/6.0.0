package com.uas.erp.ac.service.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.uas.b2b.model.InvitationRecord;
import com.uas.b2b.model.InvitationRecord2;
import com.uas.b2b.model.PageInfo;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.sso.support.Page;

@Service
public class InvitationRecordServiceImpl implements InvitationRecordService {
	@Autowired 
	private BaseDao baseDao;
	@Override
	public Map<String, Object> invitations(String keyword, Integer start, Integer pageNumber, Integer pageSize,int value)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Master master = SystemSession.getUser().getCurrentMaster();
		PageInfo pageInfo = new PageInfo();
		pageInfo.setKeyword(keyword);
		pageInfo.setPageNumber(pageNumber);
		pageInfo.setPageSize(pageSize);
		pageInfo.setUseruu(Long.valueOf(SystemSession.getUser().getEm_uu()));
		pageInfo.setEnuu(master.getMa_uu());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("data", FlexJsonUtil.toJsonDeep(pageInfo));
		if(value==0){//value为0代表只看自己
			params.put("onlySelf", "1");
		}else{
			params.put("onlySelf", "0");
		}
		Response response = HttpUtil.sendGetRequest(
				master.getMa_b2bwebsite()+  "/erp/userSpaceDetail/invitations?access_id=" + master.getMa_uu(), params,
				true, master.getMa_accesssecret());
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String data = response.getResponseText();
			if (StringUtil.hasText(data)) {
				Page<InvitationRecord> details = JSONObject.parseObject(data,
						new TypeReference<Page<InvitationRecord>>() {
						});
				map.put("data", details.getContent());
				map.put("count", details.getTotalElements());
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getInvitationCount() {
		Employee employee = SystemSession.getUser();
		Master master = employee.getCurrentMaster();
		Map<String, Object> res = new HashMap<String,Object>();
		String b2bUrl = master.getMa_b2bwebsite();
		Object userUU = employee.getEm_uu();
		Object enUU = master.getMa_uu();
		if((!StringUtil.hasText(userUU))||(!StringUtil.hasText(enUU))){
			res.put("data", "");
			return res;
		}
		if(!StringUtil.hasText(b2bUrl)){
			b2bUrl = "http://uas.ubtob.com";
		}
		b2bUrl += "/public/invitation/count?enUU="+enUU+"&userUU="+userUU;
		try {
			Response response = HttpUtil.sendGetRequest(b2bUrl, new HashMap<String, String>());
			if(response.getStatusCode() == HttpStatus.OK.value()){
				String resData = response.getResponseText();
				if(StringUtil.hasText(resData)){
					res.put("data", resData);
				}
			}else{
				if(StringUtil.hasText(response.getResponseText())){
					BaseUtil.showError(response.getResponseText());
				}else{
					BaseUtil.showError("系统错误，错误码："+response.getStatusCode());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
		return res;
	}

	@Override
	public Map<String, Object> getInvitationsRecord(Integer start, Integer page, Integer limit,String _state,String keyword) {
		Employee employee = SystemSession.getUser();
		Master master = employee.getCurrentMaster();
		String b2bUrl = master.getMa_b2bwebsite();
		Object userUU = employee.getEm_uu();
		Object enUU = master.getMa_uu();
		Map<String,Object > map = new HashMap<String, Object>();
		map.put("success",true);
		if((!StringUtil.hasText(userUU))||(!StringUtil.hasText(enUU))){
			map.put("data", "");
			map.put("count", 0);
			return map;
		}
		if(!StringUtil.hasText(b2bUrl)){
			b2bUrl = "http://uas.ubtob.com/";
		}
		Object businessCode = baseDao.getFieldDataByCondition("ENTERPRISE","EN_BUSINESSCODE", "1=1");
		b2bUrl += "/public/invitation/records?enUU="+enUU
		+"&page="+page
		+"&count="+limit
		+"&_state="+_state
		+"&userUU="+userUU
		+"&businessCode="+businessCode;
		if(StringUtil.hasText(keyword)){
			b2bUrl +="&keyword="+keyword;
		}
		try {
			Response response = HttpUtil.sendGetRequest(b2bUrl, new HashMap<String,String>());
			if(response.getStatusCode() == HttpStatus.OK.value()){
				String resData = response.getResponseText();
				if(StringUtil.hasText(resData)){
					com.uas.b2b.model.Page<InvitationRecord2> invitationsRecords = JSONObject.parseObject(resData,
							new TypeReference<com.uas.b2b.model.Page<InvitationRecord2>>() {});
					map.put("data", invitationsRecords.getContent());
					map.put("count", invitationsRecords.getTotalElement());
				}
			}else{
				if(StringUtil.hasText(response.getResponseText())){
					BaseUtil.showError(response.getResponseText());
				}else{
					BaseUtil.showError("系统错误，错误码："+response.getStatusCode());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
		return map;
	}

}
