package com.uas.erp.service.common.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DesUtil;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.ForgetPwdService;
import com.uas.erp.service.oa.SendMailService;

@Service
public class ForgetPwdServiceImpl implements ForgetPwdService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private SendMailService sendMailService;
	@Autowired
	private EmployeeDao employeeDao;
	
	private final String KEYSTRING = "98753184";
	
	/**
	 * 发送带更改密码链接的邮件
	 * @throws Exception 
	 */
	@Override
	public Map<String, Object> sendChangePwdEmail(HttpServletRequest request, String em_code,String em_name) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		String defaultSob = BaseUtil.getXmlSetting("defaultSob");
		SpObserver.putSp(defaultSob);		
		List<Master> masterList = enterpriseService.getMasters();
		Employee employee = null;
		for(Master master : masterList){
			SpObserver.putSp(master.getMa_name());
			employee = employeeService.getEmployeeByName(em_code);
			if(employee != null && employee.getEm_email() != null)
				break;
		}
		if(employee != null && !em_name.equals(employee.getEm_name())){
			map.put("message", "您输入的账号和姓名不匹配!");
			map.put("success", false);
		}else{
			if(employee != null && employee.getEm_email() != null){
				String email = employee.getEm_email();
				String title = "找回密码";
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
				String key = "";
				try {
					key = new DesUtil(KEYSTRING).encrypt(em_code+","+sdf.format(date));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String content = "<font style='font-size:24px;font-weight:600'><a href='"+BaseUtil.getBasePath(request)+"common/changePwd.action?key="+key+"'>请点击这里进行新密码设置</a></font>";
				boolean isSend = false;
				try {
					isSend = sendMailService.sendSysMail(title, content, email, "");
				} catch (Exception e) {
					map.put("success", false);
					map.put("message", "邮件发送失败,请检查您的收件邮箱格式是否正确!");
					return map;
				}
				if(isSend){
					map.put("message", "修改密码的地址已发送至您的邮箱：" + email + ",请查收");
					map.put("success", true);
				}
			}else{
				map.put("success", false);
				map.put("message", "您的账号没有设置邮箱，无法找回，请联系管理员或使用手机UU修改。");
			}
		}
		return map;
	}


	/**
	 * 判断邮件有效期
	 */
	@Override
	public Map<String, Object> changePwd(String key) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		String str = new DesUtil(KEYSTRING).decrypt(key);
		String dataString = str.substring(str.lastIndexOf(",")+1,str.length());
		String codeString = str.substring(0,str.lastIndexOf(","));
		map.put("em_code", codeString);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date = sdf.parse(dataString);
			if((new Date().getTime() - date.getTime())/(3600*24*1000) > 1){
				map.put("success", false);
				map.put("message", "此链接已失效!");
			}else{
				map.put("success", true);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 更改密码
	 */
	@Transactional
	public Map<String, Object> updatePassword(String password, String em_code){
		Map<String, Object> map = new HashMap<String, Object>();
		Employee employee = employeeDao.getEmployeeByConditon("em_code = '" + em_code + "'");
		int em_id = employee.getEm_id();
		String oldPassword = employee.getEm_password();
		String result = employeeService.updatePwd(null,oldPassword,password,String.valueOf(em_id),em_code,oldPassword,"0");
		if (result == null||result.length()==0) {
			map.put("success", true);
			employeeService.updateChangeStatues(em_id);
		} else {
			map.put("result", result);
		}
		return map;
	}
}
