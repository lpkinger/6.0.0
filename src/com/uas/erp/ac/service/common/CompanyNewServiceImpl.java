package com.uas.erp.ac.service.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.uas.b2b.core.PSHttpUtils;
import com.uas.b2b.model.EnterpriseBaseInfo;
import com.uas.b2b.model.InvitationRecord;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;


@Service
public class CompanyNewServiceImpl implements CompanyNewService {
	@Autowired
	private BaseDao baseDao;
	@Override
	public Map<String, Object> checkCompanyExist(String name) {
		Map<String, Object> res = new HashMap<String, Object>();
		Employee employee = SystemSession.getUser();
		Master master = SystemSession.getUser().getCurrentMaster();
		String b2bUrl = master.getMa_b2bwebsite();
		Long enUU= master.getMa_uu();
		Long emUU = employee.getEm_uu();
		if(enUU==null){
			//BaseUtil.showError("您的企业还未开通企业B2B，请到企业信息中开通此功能！");
			BaseUtil.showError("您的企业还未注册优软云，请联系管理员注册企业优软云！");
		}
		if(emUU==null){
			BaseUtil.showError("您还不是优软云的个人用户，请联系管理员开通！");
		}
		if(!StringUtil.hasText(b2bUrl)){
			b2bUrl = "http://uas.ubtob.com/";
		}
		Object businessCode = baseDao.getFieldDataByCondition("enterprise", "en_businesscode", "1=1");
		b2bUrl+="/public/invitation/checkEnName?name="+name
				+ "&userUU="+emUU
				+ "&userTel="+employee.getEm_mobile()
				+ "&enUU="+enUU;
		/*if(enUU==null){
			b2bUrl+="&businessCode="+businessCode;
		}else{
			b2bUrl+="&enUU="+enUU;
		}*/
		try {
			Response response = HttpUtil.sendGetRequest(b2bUrl,new HashMap<String, String>());
			if(response.getStatusCode() == HttpStatus.OK.value()){
				String data = response.getResponseText();
				if(StringUtil.hasText(data)){
					List<EnterpriseBaseInfo>  dataList = JSONObject.parseObject(data,
							new TypeReference<List<EnterpriseBaseInfo>>() {});
					if(!CollectionUtil.isEmpty(dataList)){//企业存在 返回企业信息展示前台
						res.put("isExist", true);
						res.put("data", dataList.get(0));
						return res;
					}else{//企业不存在 发起点对点邀请
						//传回前台提供
						res.put("emCode", employee.getEm_code());
						res.put("businessCode", businessCode);
						res.put("notExist", true);
						return res;
					}
				}
			}else{
				if(StringUtils.isEmpty(response.getResponseText()))
					BaseUtil.showError("程序错误。错误码："+response.getStatusCode());
				else BaseUtil.showError(response.getResponseText());
			}
		} catch (Exception e) {
			BaseUtil.showError(e.getMessage());
			e.printStackTrace();
		}
		return res;
	}


	@Override
	public Map<String, Object> getInviteUrl() {//群分享
		Map<String, Object> res = new HashMap<String, Object>();
		Employee employee  = SystemSession.getUser();
		Master master = employee.getCurrentMaster();
		String time = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String inviteUrl = baseDao.getDBSetting("CompanyNew","inviteUrl");
		if(!StringUtil.hasText(inviteUrl)){
			inviteUrl = "https://sso.ubtob.com/register/enterpriseRegistration";
		}
		Object inviteUserUU = employee.getEm_uu();
		Object inviteSpaceUU = master.getMa_uu();
		if(inviteSpaceUU == null){
			BaseUtil.showError("您的企业还未注册优软云，请到企业信息中注册企业优软云！");
		}
		if(inviteUserUU==null){
			BaseUtil.showError("您还不是优软云的个人用户，请联系管理员开通！");
		}
		res.put("url",inviteUrl+"?inviteUserUU="+inviteUserUU+"&inviteSpaceUU="+inviteSpaceUU+"&invitationTime="+time+"&source=UAS");
		return res;
	}

	@Override
	public Map<String, Object> getInviteUrl(String name, String vendusername, String userTel) {
		Map<String, Object> res = new HashMap<String, Object>();
		
		Employee employee  = SystemSession.getUser();
		Master master = employee.getCurrentMaster();
		String b2bUrl = master.getMa_b2bwebsite();
		Object inviteUserUU = employee.getEm_uu();
		Object inviteSpaceUU = master.getMa_uu();
		if(inviteSpaceUU == null){
			BaseUtil.showError("您的企业还未注册优软云，请联系管理员注册企业优软云！");
		}
		if(inviteUserUU==null){
			BaseUtil.showError("您还不是优软云的个人用户，请联系管理员开通！");
		}
		if(!StringUtil.hasText(b2bUrl)){
			b2bUrl = "http://uas.ubtob.com/";
		}
		try {
			InvitationRecord invita = new InvitationRecord();
			invita.setUseruu(employee.getEm_uu());
			invita.setEnuu(master.getMa_uu());
			invita.setVendusertel(userTel);
			invita.setVendname(name);
			invita.setVendusername(vendusername);
			invita.setSource("UAS");
			
			JSONObject formData = new JSONObject();
			formData.put("jsonStr", invita);
			b2bUrl+="/public/invitation/add";
			com.uas.b2b.core.PSHttpUtils.Response response = PSHttpUtils.sendPostRequest(b2bUrl, formData);
			//记录操作日志：
			baseDao.execute("insert into messagelog (ML_ID, ML_DATE, ML_MAN, ML_CONTENT, ML_RESULT) "
					+ "values (messagelog_seq.nextval,sysdate,'"+employee.getEm_name()+"("+employee.getEm_code()+")','邀请注册记录','【"+response.getStatusCode()+"】:"+response.getResponseText()+"')");
			String inviteUrl = baseDao.getDBSetting("CompanyNew","inviteUrl");
			if(!StringUtil.hasText(inviteUrl)){
				inviteUrl = "https://sso.ubtob.com/register/enterpriseRegistration";
			}
			
			res.put("url",inviteUrl+"?inviteUserUU="+inviteUserUU+"&inviteSpaceUU="+master.getMa_uu()+"&invitationTime="+new SimpleDateFormat("yyyyMMdd").format(new Date())+"&source=UAS");
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError(e.getMessage());
		}
		return res;
	}

}
