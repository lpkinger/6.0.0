package com.uas.erp.service.common.impl;

import java.util.HashMap;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.AccountCenterService;
import com.uas.sso.AccountConfig;
import com.uas.sso.entity.UserView;
import com.uas.sso.util.AccountUtils;

@Service
public class AccountCenterServiceImpl implements AccountCenterService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public UserView sync(Employee employee, Master master) throws Exception {
		if(master.getMa_uu() == null || master.getMa_uu() == 0){
			BaseUtil.showError("企业UU不存在");
		}
		// 用户账户信息
		//检验手机号、邮箱号
		String mobile = employee.getEm_mobile();
		String email = employee.getEm_email();
		if(StringUtil.hasText(mobile)){
			boolean mobileMatch =  mobile.matches(Constant.REGEXP_MOBILE);
			if(mobileMatch){
				if(StringUtil.hasText(email)){
					if(!email.matches(Constant.REGEXP_EMAIL)){
						BaseUtil.showError("邮箱格式不正确，请输入正确的邮箱地址");
					}
				}
			}else{
				BaseUtil.showError("手机号格式不正确，请输入正确的手机号");
			}
		}
		//同步信息
		UserView user = new UserView();
		user.setVipName(employee.getEm_name());
		user.setMobile(employee.getEm_mobile());
		user.setEmail(employee.getEm_email());
		user.setIdCard(employee.getEm_iccode());
		user.setPassword(employee.getEm_password());
		return AccountUtils.addUser(master.getMa_uu(), user);
	}

	@Override
	public void unbind(Employee employee, Master master) throws Exception {
		AccountUtils.removeUser(employee.getEm_uu(),master.getMa_uu());
	}

	@Override
	public void resetPassword(Employee employee, Master master, String newPassword) throws Exception {
		if(master.getMa_uu() == null || master.getMa_uu() == 0){
			BaseUtil.showError("企业UU不存在");
		}
		if(employee.getEm_uu()==null){
			BaseUtil.showError("用户不存在UU号，无法同步密码到优软云");
		}
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("password",String.valueOf(newPassword));
		Response response = HttpUtil.sendPostRequest(AccountConfig.getUserSaveUrl()+"/update/password/"+employee.getEm_uu()+"/"+master.getMa_uu(), params, true, master.getMa_accesssecret());
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			String res = response.getResponseText();
			if (StringUtil.hasText(res)) {
				HashMap<String, Object> regInfos = FlexJsonUtil.fromJson(res,HashMap.class);
				if(regInfos != null){
					Object error = regInfos.get("error");
					if(error!=null&&(Boolean)error==true){
						BaseUtil.showError((String)regInfos.get("errMsg"));
					}
				}
			}
		} else {
			BaseUtil.showError("修改密码失败");
		}
	}

	@Override
	public boolean checkPassword(Employee employee, Master master, String password) throws Exception {
		return fuzzyCheckPassword(employee,master,password);
	}

	@Override
	public boolean fuzzyCheckPassword(Employee employee, Master master, String password) throws Exception {
		if (!StringUtils.isEmpty(employee.getEm_mobile()) && employee.getEm_mobile().matches(Constant.REGEXP_MOBILE)&&employee.getEm_uu()!=null) {// 这种验证方式必须要有绑定过手机号
			UserView user = new UserView();
			user.setUserUU(employee.getEm_uu());
			user.setVipName(employee.getEm_name());
			user.setMobile(employee.getEm_mobile());
			user.setEmail(employee.getEm_email());
			user.setPassword(password);
			baseDao.execute("insert into messagelog(ml_date,ml_man,ml_content,ml_result,code) "
					+ "values (sysdate,'"+employee.getEm_name()+"','校验密码','输入密码为："+password+",MA_MANAGEID:"+String.valueOf(master.getMa_manageid())+"','"+employee.getEm_mobile()+"')");
			return AccountUtils.fuzzyCheckPassword(user);
		}
		return false;
	}

	@Override
	public UserView getUserByToken(String token) throws Exception {
		return AccountUtils.getUserByToken(token);
	}

}
