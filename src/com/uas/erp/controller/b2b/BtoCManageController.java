package com.uas.erp.controller.b2b;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.uas.api.crypto.util.SecretUtil;
import com.uas.b2c.model.B2CUtil;
import com.uas.b2c.service.common.B2CProdService;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.scm.BatchDealService;
import com.uas.remoting.hessian.MultiProxyFactoryBean;
import com.uas.sso.entity.UserView;
import com.uas.sso.util.AccountUtils;

@Controller("B2CManageUrlController")
public class BtoCManageController {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private B2CUtil b2cUtil;
	@Autowired
    private B2CProdService b2cProductService;
	@Autowired
	private BatchDealService batchDealService;
	@RequestMapping("/b2c/manageUrl.action")
	public void redirectManage(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		Master master = SystemSession.getUser().getCurrentMaster();
		String spaceUID = String.valueOf(baseDao.getFieldDataByCondition("enterprise", "en_businesscode", "rownum=1"));
		String requestUrl = Constant.b2cHost();
		if (!StringUtils.isEmpty(master.getMa_env()) && master.getMa_env().equals("prod")) {
			requestUrl = Constant.b2cHost();
		} else {
			requestUrl = Constant.b2cTestHost();
		}
		String token = null;
		Employee employee = SystemSession.getUser();
		if (employee != null && employee.getEm_mobile().matches(Constant.REGEXP_MOBILE)) {
			UserView user = new UserView();
			user.setUserUU(employee.getEm_uu());
			user.setVipName(employee.getEm_name());
			user.setMobile(employee.getEm_mobile());
			user.setEmail(employee.getEm_email());
			user.setIdCard(employee.getEm_iccode());
			try {
				token = AccountUtils.getAccessToken(user);
			} catch (Exception e) {
				token = null;
				e.printStackTrace();
			}
		}
		if (StringUtils.isEmpty(url)) {
			url = "index";
		}
		if (token != null) {
			requestUrl = requestUrl + "/api/webpage?access_token=" + token + "&redirect_page=" + url;
		}
		response.sendRedirect(requestUrl);
	}
	@RequestMapping("/b2b/ucloudUrl_token.action")
	public void redirectUcloud2(HttpServletRequest request, HttpServletResponse response,String url,String accountUrl,String b2cUrl,String urlType,HttpSession session) throws IOException {
		Master master = SystemSession.getUser().getCurrentMaster();
		String token = null;
		String requestUrl = null;
		Employee employee = SystemSession.getUser();
		accountUrl = StringUtils.isEmpty(accountUrl)?"https://sso.ubtob.com":accountUrl;
		b2cUrl = StringUtils.isEmpty(b2cUrl)?"https://www.usoftmall.com":b2cUrl;
		String mobile = employee.getEm_mobile();
		String en_uu = String.valueOf(session.getAttribute("en_uu"));
		Long em_uu = employee.getEm_uu();
		if(en_uu.equals("null")){
			en_uu = String.valueOf(master.getMa_uu());
		}
		if(em_uu==null){
			if(mobile==null){
				if((urlType!=null)&&!urlType.equals("ubtob")){
					BaseUtil.showError("您尚未填写手机号，请填写手机号并联系管理人员开通手机APP再访问");
				}
			}else{
				String data = "[{\"em_id\":"+employee.getEm_id().toString()+"}]";
				batchDealService.vastPostToAccountCenter("Employees!Deal2",data);
				em_uu = Long.parseLong(baseDao.getFieldDataByCondition("employee", "em_uu", "em_id="+employee.getEm_id()).toString());
				employee.setEm_uu(em_uu);
			}
		}
		if (employee != null && (!"null".equals(en_uu))) {
			//获取token
			Map<String, Object> maps = beforeAccountToken(employee,master,Long.parseLong(en_uu),accountUrl);
			
			if(maps!=null&&!maps.isEmpty()&&maps.size()>0){
				if(maps.get("success")!=null&&(boolean) maps.get("success")){
					token = String.valueOf(maps.get("content"));
				}else{
					BaseUtil.showError(String.valueOf(maps.get("content")));
				}
			}
		}
		
		if(urlType!=null && urlType.equals("myStore")){//店铺跳转
			if (b2cUtil.isB2CMAll(master)) {
				SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
				SpObserver.putSp(master.getMa_name());
				MultiProxyFactoryBean.setProxy(master.getEnv());
				String decorationB2CUrl = b2cProductService.DecorationB2CUrl();
				if(decorationB2CUrl!=null&&decorationB2CUrl.length()>0){
					url = url.replace("/vendor_erp#/store/maintain", decorationB2CUrl);
				}
			}
		}else if(urlType!=null && urlType.equals("ubtob")){//优软云
			if (token != null) {
				requestUrl = accountUrl + "/agency?appId=b2b&token=" + token + "&returnURL="+url;
			}else
				requestUrl = url;
		}
		if (token != null) {
			if(urlType!=null && urlType.equals("sendLocal")){
				String localPath = request.getScheme()+"://" + request.getServerName() + ":"+ request.getServerPort()+ request.getContextPath();
				url +="&localPath="+localPath+"&erpPath="+request.getParameter("erpPath");
			}
			requestUrl = requestUrl==null ? accountUrl + "/agency?appId=b2b&isLoginAll=false&token=" + token + "&returnURL="+b2cUrl+URLEncoder.encode(url, "UTF-8")+"&baseURL="+b2cUrl+"/newLogin/other" :requestUrl;
		}else{
			requestUrl = requestUrl==null ? b2cUrl :requestUrl;
		}
		requestUrl=requestUrl.replace("#", "%23");
		response.sendRedirect(requestUrl);
	}
	
	@RequestMapping("/b2b/ucloudUrl.action")
	public void redirectUcloud(HttpServletRequest request, HttpServletResponse response,String url,String accountUrl,String b2cUrl,String urlType,HttpSession session) throws IOException {
		Employee employee = SystemSession.getUser();
		String mobile = employee.getEm_mobile();
		String password = employee.getEm_password();
		Object en_uu = session.getAttribute("en_uu");
		if(urlType==null||(urlType!=null && !urlType.equals("ubtob"))){
			if(employee.getEm_uu()==null){
				if(mobile==null){
					BaseUtil.showError("您尚未填写手机号，请填写手机号并联系管理人员开通手机APP再访问");
				}else{
					String data = "[{\"em_id\":"+employee.getEm_id().toString()+"}]";
					batchDealService.vastPostToAccountCenter("Employees!Deal2",data);
				}
			}
		}
		String	requestUrl = null;
		accountUrl = accountUrl==null?"http://sso.ubtob.com":accountUrl;
		b2cUrl = b2cUrl==null?"https://www.usoftmall.com":b2cUrl;
		if(urlType!=null && urlType.equals("myStore")){//店铺跳转
			Master master = SystemSession.getUser().getCurrentMaster();
			if (b2cUtil.isB2CMAll(master)) {
				SecretUtil.set(Long.toString(master.getMa_uu()), master.getMa_accesssecret());
				SpObserver.putSp(master.getMa_name());
				MultiProxyFactoryBean.setProxy(master.getEnv());
				String decorationB2CUrl = b2cProductService.DecorationB2CUrl();
				if(decorationB2CUrl!=null&&decorationB2CUrl.length()>0){
					url = url.replace("/vendor_erp#/store/maintain", decorationB2CUrl);
				}
			}else{
				
			}
		}else if(urlType!=null && urlType.equals("ubtob")){
			requestUrl = accountUrl+"/sso/proxy?appId=b2b&t="+mobile+"&p="+password+"&u="+en_uu+"&returnURL="+url;
		}
		requestUrl = requestUrl==null ? accountUrl+"/sso/proxy?appId=b2b&t="+mobile+"&p="+password+"&u="+en_uu+"&returnURL="+b2cUrl+url+"&baseURL="+b2cUrl+"/login/other" :requestUrl;
		requestUrl=requestUrl.replace("#", "%23");
		response.sendRedirect(requestUrl);

	}
	
	/**
	 * 
	 * */
	private Map<String, Object> beforeAccountToken(Employee employee,Master master,Long enUU,String accountUrl) {
		Map<String, Object> regInfos = getAccountToken(employee.getEm_uu(),enUU,master,accountUrl);
		Map<String, Object> res = new HashMap<String, Object>();
		if(regInfos!=null){
			if(regInfos.get("success")!=null&&(boolean)regInfos.get("success")){
				  res.put("success",true);
				  res.put("content", regInfos.get("content"));
			}else if(regInfos.get("error")!=null&&(boolean)regInfos.get("error")&&regInfos.get("errDetail")!=null){
				if(regInfos.get("errDetail")!=null){
					Map<String, Object> errDetail = FlexJsonUtil.fromJson(regInfos.get("errDetail").toString());
					 if(errDetail.get("hasUserSpace")!=null&&!(boolean) errDetail.get("hasUserSpace")){
						 res.put("success",false);
						 res.put("content","您所在企业尚未注册优软云，请前往企业资料进行注册！");
					 }else if(errDetail.get("hasUser")!=null&&!(boolean) errDetail.get("hasUser")){
						 bindUsers(employee.getEm_uu(),enUU);
						 regInfos = getAccountToken(employee.getEm_uu(),enUU,master,accountUrl);
						 if(regInfos!=null){
							 if(regInfos.get("success")!=null&&(boolean)regInfos.get("success")){
								 res.put("success",true);
								 res.put("content", regInfos.get("content"));
							 }else{
								 res.put("success",false);
								 res.put("content","校验用户信息失败！");
							 } 
						 }
					 }
				 }else{
					 res.put("success",false);
					 res.put("content","校验用户信息失败！");
				 }
			}else if(regInfos.get("error")!=null&&(boolean)regInfos.get("error")&&regInfos.get("errMsg")!=null){
				res.put("success",false);
				res.put("content",regInfos.get("errMsg"));
			}
		}else{
			res.put("success",false);
			res.put("content","校验用户信息为空！");
		}
		return res;
	}
	/**
	 * 获取账户中心token方法
	 * */
	private Map<String, Object> getAccountToken(Long emUU,Long enUU,Master master,String accountUrl) {
		HashMap<String, Object> regInfos = new HashMap<String, Object>();
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("enUU",String.valueOf(enUU));
			params.put("userUU",String.valueOf(emUU));
			Response response = HttpUtil.sendGetRequest(
					//"https://sso.ubtob.com/sso/login/getToken?access_id=" + enUU + "&appId=" + BaseUtil.getAppId(), params,
					//"http://192.168.253.12:32323/sso/login/getToken?access_id=" + enUU + "&appId=" + BaseUtil.getAppId(), params,
					accountUrl+"/sso/login/getToken?access_id=" + enUU + "&appId=" + BaseUtil.getAppId(), params,
					true, master.getMa_accesssecret());
			System.out.println("master.getMa_accesssecret(): "+master.getMa_accesssecret());
			if (response.getStatusCode() == HttpStatus.OK.value()) {
				String res = response.getResponseText();
				if (StringUtil.hasText(res)) {
					regInfos = FlexJsonUtil.fromJson(res,HashMap.class);
					if(regInfos != null){
						return regInfos;
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {

		}
		return null;
	}
	/**
	 * 绑定企业
	 * */
	private String bindUsers(Long emUU, Long enUU) {
		try {
			AccountUtils.addUser(emUU,enUU);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return null;
	}
}
