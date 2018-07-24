package com.uas.erp.service.common.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.encry.HmacUtils;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.model.UserSession;
import com.uas.erp.service.common.AccountCenterService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.sso.entity.UserView;

@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AccountCenterService accountCenterService;

	//同步到优软云密码校验正则表达式
	private final static String REGEXP_PASSWORD = "^(?=.{8,20})(((?=.*[0-9])(?=.*[a-z]))|((?=.*[0-9])(?=.*[A-Z]))).*$";
	
	@Override
	public String loginWithEmQrcode(String sob, String username,String sid,String ip, boolean isMobile,
			String webSite) {
		Master master = enterpriseService.getMasterByName(sob);
		if (master != null)
			SpObserver.putSp(sob);
		else
			return "账套不存在!";
		Employee employee = getEmployeeByName(username);
		if (employee == null) {
			return "员工账号不正确!";
		} 
		else if (!isMobile && ip != null && employee.getEm_lastip() != null) {
			//找之前登录的账号,可能会有多个相同用户的对象
			List<UserSession> userSessions = UserOnlineListener.getUsersByEmployee(employee.getEm_id(), sob);
			if (userSessions.size()!=0) {
				if (userSessions.size()==1 && ip.equals(userSessions.get(0).getIp())) {
					return "检测到您已登录本系统,请不要重复登录、打开空白页!";
				} else {
					//将无用的多个相同对象一并剔除
					for(UserSession u:userSessions) {
						UserOnlineListener.Kick(u);
					}
				}
			}
		}
		if (!check_onlyInner(employee, webSite))
			return "当前账号只能在局域网登录!";
		if (ip != null) {
			baseDao.updateByCondition("Employee", "em_lastip='" + ip + "'", "em_id=" + employee.getEm_id());
		}
		return null;
	}
	
	
	@Override
	public String loginWithEm(String sob, String username, String password, String ip, boolean isMobile, String webSite) {
		Master master = enterpriseService.getMasterByName(sob);
		if (master != null)
			SpObserver.putSp(sob);
		else
			return "账套不存在!";
		Employee employee = getEmployeeByName(username);
		if (employee == null) {
			return "员工账号不正确!";
		} else if (StringUtils.isEmpty(employee.getEm_password())) {
			try {
				boolean checked = accountCenterService.fuzzyCheckPassword(employee, master, password);
				if (checked) {
					resetPassword(employee.getEm_code(), password);
				} else {
					// TODO: use errCode
					return "未设置密码";
				}
			} catch (Exception e) {
				return e.getMessage();
			}
		} else if (!password.equals(employee.getEm_password())) {
			try {
				boolean checked = accountCenterService.fuzzyCheckPassword(employee, master, password);
				if (checked) {
					resetPassword(employee.getEm_code(), password);
				} else {
					// TODO: use errCode
					return "密码不正确!";
				}
			} catch (Exception e) {
				return e.getMessage();
			}
		} else if (!isMobile && ip != null && employee.getEm_lastip() != null) {
			UserSession us = UserOnlineListener.isOnLine(employee.getEm_id(), sob);
			if (us != null) {
				if (ip.equals(us.getIp())) {
					return "检测到您已登录本系统,请不要重复登录、打开空白页!";
				} else {
					return "账号: " + username + " 已于IP:" + us.getIp() + "登录,如非您本人操作，请及时联系管理员!";
				}
			}
		}
		if (!check_onlyInner(employee, webSite))
			return "当前账号只能在局域网登录!";
		if (ip != null) {
			baseDao.updateByCondition("Employee", "em_lastip='" + ip + "'", "em_id=" + employee.getEm_id());
		}
		return null;
	}

	@Override
	public Employee getEmployeeByName(String username) {
		return employeeDao.getEmployeeByTelOrCode(username);
	}

	@Override
	public Employee getEmployeeById(long em_id) {
		return employeeDao.getEmployeeByEmId(em_id);
	}
	@Override
	public Employee getEmployeeById(int em_id, String sob) {
		SpObserver.putSp(sob);
		return employeeDao.getEmployeeByEmId(em_id);
	}

	@Override
	public Employee getByCondition(String condition, String caller) {
		return employeeDao.getEmployeeByConditon(condition);
	}

	@Override
	public List<Employee> getEmployeesByOrId(int or_id) {
		return employeeDao.getEmployeesByOrId(or_id);
	}

	@Override
	public Master getMaster(Employee employee) {
		return employeeDao.getMaster(employee.getEm_id());
	}

	@Override
	@CacheEvict(value = "employee", allEntries = true)
	public String updatePwd(String caller, String em_oldpassword, String em_newpassword, String emid, String emcode, String empassword, String synchronize) {
		String result = null;
		if (!empassword.equals(em_oldpassword)) {
			result = "原密码输入错误!";
		} else {
			Employee employee = employeeDao.getEmployeeByEmId(Integer.parseInt(emid));
			if("1".equals(synchronize)){		//同步到优软云		
				result = resetPasswordToAccountCenter(employee,em_newpassword);
				//判断同步密码到云是否有问题
				if(result!=null){
					return result;
				}
				resetPassword(emcode, em_newpassword);
			}else{        //仅修改UAS密码
				resetPassword(emcode, em_newpassword);
			}
			String log = "insert into messagelog(ml_date,ml_man,ml_content,ml_result,code) values (sysdate,'"+employee.getEm_name()+"','修改密码','修改成功,原始密码为："+em_oldpassword+",新密码为："+em_newpassword+"','"+emcode+"')";
			baseDao.execute(log);
		}
		return result;
	}

	private void resetPassword(String emCode, String newPassword){
		Employee employee = getEmployeeByEmcode(emCode);
		//使用查询出来的employee，兼容系统账号界面修改密码时的em_name参数也是对应要修改的人员姓名
		String em_name = employee.getEm_name();
		String em_mobile = employee.getEm_mobile();
		//对密码进行加密
		String emPassword = PasswordEncryUtil.encryptPassword(newPassword, em_mobile);
		baseDao.updateByCondition("Employee", "em_password='" + emPassword + "'", "em_code='" + emCode + "'");
		if (BaseUtil.getXmlSetting("saas.domain") == null) {
			String sob = BaseUtil.getXmlSetting("defaultSob");
			String codes = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ma_user) from " + sob + ".master", String.class);
			if (codes != null) {
				String[] sobs = codes.split(",");
				for (String s : sobs) {
					baseDao.updateByCondition(s + ".Employee", "em_password='" + emPassword + "'", "em_code='" + emCode + "' and em_name='"+em_name+"'");
				}
			}
		}
		baseDao.execute("insert into messagelog(ml_date,ml_man,ml_content,ml_result,code) values (sysdate,'"+em_name+"','重置密码','重置密码为："+newPassword+"','"+emCode+"')");
	}

	@Override
	public List<Employee> getEmployeesByCondition(String condition) {
		return employeeDao.getEmployeesByConditon(condition);
	}

	@Override
	public void setMsgRemaind(Employee employee, int remind) {
		baseDao.updateByCondition("Employee", "em_remind=" + remind, "em_id=" + employee.getEm_id());
		employee.setEm_remind(remind);
	}
	@Override
	public void setDeskTopRemind(Employee employee, int remind) {
		baseDao.updateByCondition("Employee", "em_dtremind=" + remind, "em_id=" + employee.getEm_id());
		employee.setEm_dtremind(remind);
		
	}

	/**
	 * 修改移动客户端最新登录时间
	 */
	@Override
	public void setMoLastLoginTime(Employee employee, Date date) {
		String dateStr = "to_date('" + DateUtil.format(date, "yyyy-MM-dd HH:mm:ss") + "', 'yyyy-mm-dd HH24:mi:ss')";
		baseDao.updateByCondition("Employee", "em_mologintime=" + dateStr, "em_id=" + employee.getEm_id());
	}

	@Override
	public void updateEmployeeList(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Employee", "em_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "em_id", store.get("em_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public String checkWithMac(String smac, String username, String ip) {
		int count = baseDao.getCount("select count(*) from usermac where mac_address='" + smac + "'");
		if (count > 0) {
			baseDao.updateByCondition("usermac", "mac_lasttime=sysdate,mac_lastuser='" + username + "',mac_lastip='" + ip + "'",
					" mac_address='" + smac + "'");
			return null;
		} else {
			int countnum = baseDao.getCount("select count(*) from employee where em_code='" + username + "' and NVL(em_lastip,' ')<>' '");
			if (countnum > 0) {
				return "不是有效的MAC地址登陆！MAC:" + smac;
			} else {
				String sqlstr = "insert into usermac (mac_id,mac_address,mac_lasttime,mac_lastuser,mac_lastip)values(usermac_seq.nextval,'"
						+ smac + "',sysdate,'" + username + "','" + ip + "')";
				baseDao.execute(sqlstr);
				return null;
			}

		}

	}

	@Override
	public void logWithEm(String sip, String emname) {
		String sqlstr = "insert into loginfo(id,usname,sip,indate)values(loginfo_seq.nextval,'" + emname + "','" + sip + "',sysdate)";
		baseDao.execute(sqlstr);
	}

	@Override
	public Employee getEmployeeByUu(long uu) {
		return employeeDao.getEmployeeByEmUu(uu);
	}

	@Override
	public Employee getEmployeeByEmcode(String emcode) {
		return employeeDao.getEmployeeByEmcode(emcode);
	}

	@Override
	public List<Employee> getEmployees() {
		return employeeDao.getEmployees(SpObserver.getSp());
	}

	@Override
	public void saveEmployees(String jsonData) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(jsonData);
		List<String> sqls = new ArrayList<String>();
		Employee curEmployee = SystemSession.getUser();
		for (Map<Object, Object> map : maps) {
			if (map.get("em_code") == null || map.get("em_code").equals("")) {
				BaseUtil.showError("人员编号不能为空!");
			}
			if (map.get("em_position") == null || map.get("em_position").equals("")) {
				BaseUtil.showError("人员岗位不能为空!");
			}
			map.put("em_id", baseDao.getSeqId("EMPLOYEE_SEQ"));
			//对新账号密码进行加密
			map.put("em_password", PasswordEncryUtil.encryptPassword("111111", null));
			map.put("em_class", "正式");
			map.put("em_enid", curEmployee.getEm_enid());
			map.put("em_type", "normal");
			sqls.add(SqlUtil.getInsertSqlByMap(map, "employee"));
		}
		baseDao.execute(sqls);
	}

	@Override
	public void updateEmployees(String jsonData) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(jsonData);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			if (map.get("em_code") == null || map.get("em_code").equals("")) {
				BaseUtil.showError("人员编号不能为空!");
			}
			if (map.get("em_position") == null || map.get("em_position").equals("")) {
				BaseUtil.showError("人员岗位不能为空!");
			}

			sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "employee", "em_id"));
		}
		baseDao.execute(sqls);
	}

	@Override
	public void deleteEmployees(String jsonData) {
		List<Map<Object, Object>> maps = BaseUtil.parseGridStoreToMaps(jsonData);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : maps) {
			sqls.add("delete employee where em_id=" + map.get("em_id"));
		}
		baseDao.execute(sqls);
	}

	@Override
	public Employee getEmployeeByEmTel(String username) {
		return employeeDao.getEmployeeByEmTel(username);
	}

	@Override
	public Employee generate(String sob) {
		String thisSob = SpObserver.getSp();
		SpObserver.putSp(sob);
		String code = baseDao.sGetMaxNumber("Employee!Guest", 2);
		Integer enId = baseDao.getJdbcTemplate().queryForObject("select en_id from enterprise", Integer.class);
		Employee employee = new Employee();
		employee.setEm_code(code);
		employee.setEm_name("游客-" + code);
		//对新密码进行加密
		employee.setEm_password(PasswordEncryUtil.encryptPassword("1", null));
		employee.setEm_enid(enId);
		employee.setEm_sex("男");
		employee.setEm_status("已审核");
		employee.setEm_statuscode("AUDITED");
		employee.setEm_type("admin");
		employee.setEm_class("正式");
		employee.setEm_id(baseDao.getSeqId("employee_seq"));
		baseDao.save(employee);
		SpObserver.putSp(thisSob);
		return employee;
	}

	public String resetPasswordToAccountCenter(Employee employee, String em_newpassword) {
		// 这里只考虑有云账号的才调用
		if (!StringUtils.isEmpty(employee.getEm_mobile()) && employee.getEm_mobile().matches(Constant.REGEXP_MOBILE)) {
			if (!em_newpassword.matches(REGEXP_PASSWORD)) {
				return "{'code':'regexp','error':'密码过于简单，如需同步优软云，<br>请输入包含数字+英文的8-20位密码'}";
			}
			try {
				accountCenterService.resetPassword(employee, SystemSession.getUser().getCurrentMaster(), em_newpassword);
			}  catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
			}
		}else{
			return "{'code':'notBtob','error':'您没有优软云账号，不需要同步修改'}";
		}
		return null;
	}

	public String postToAccountCenter(Employee employee) {
		return postToAccountCenter(employee, SystemSession.getUser().getCurrentMaster());
	}

	public String postToAccountCenter(Employee employee, Master master) {
		if (!StringUtils.isEmpty(employee.getEm_mobile())) {
			try {
				UserView user = accountCenterService.sync(employee, master);
				Long userUU = null;
				String imId = null;
				if(user!=null){	
					userUU = user.getUserUU();
					if(user.getImId()!=null&&!user.getImId().equals("null")){					
						imId = user.getImId();
					}
				}
				baseDao.execute("update " + master.getMa_user() + ".employee set em_b2benable=-1,em_uu=?,em_imid=? where em_id=?", userUU,
						imId, employee.getEm_id());
			} catch (Exception e) {
				e.printStackTrace();
				return e.getMessage();
			}
		} else {
			return "用户手机号不存在无法开通app";
		}
		return null;
	}

	private boolean check_onlyInner(Employee employee, String WebSite) {
		// UserAgentUtil isInnerIP
		if (WebSite != null && employee.getEm_onlyinner() != null && employee.getEm_onlyinner() == 1) {
			Enterprise enterprise = enterpriseService.getEnterprise();
			if (enterprise != null && enterprise.getEn_erpurl() != null && WebSite.startsWith(enterprise.getEn_erpurl()))
				return false;
		}
		return true;
	}

	// 取当前员工所在组织的所有人员(包括下级组织)
	@Override
	public List<Employee> getHrorgEmployeesByEmcode(String emcode) {
		return employeeDao.getHrorgEmployeesByEmcode(emcode);
	}

	@Override
	public boolean checkAppToken(String sessionId, String newSessionId, String emcode, int type) {
		boolean bool = baseDao.checkIf("MOBILE_TOKEN", "APPTOKEN='" + HmacUtils.encode(sessionId + emcode) + "'");
		if (type == 0 || bool) {
			baseDao.execute("insert into MOBILE_TOKEN(APPTOKEN) values ('" + HmacUtils.encode(newSessionId + emcode) + "')");
			return true;
		}
		return false;
	}

	@Override
	public String deleteFromAccountCenter(Employee employee) {
		try {
			accountCenterService.unbind(employee, SystemSession.getUser().getCurrentMaster());
			baseDao.execute("update employee set em_b2benable=0,em_uu=null,em_imid=null where em_id=?", employee.getEm_id());
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

	/**
	 * 更新密码修改时间
	 */
	@Override
	public void updateChangeStatues(int id) {
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		baseDao.execute("update employee set em_pwdupdatedate=to_date('"+simpleDateFormat.format(new Date())+"','yyyy-MM-dd') where em_id="+id);
	}


	@Override
	public String getPassword(String condition) {
		Object[] obj = employeeDao.getFieldsEmployeeByCondition(new String[] {"em_password"}, condition);
		if(obj!=null&&obj[0]!=null){
			return String.valueOf(obj[0]);
		}
		return null;
	}


	@Override
	public boolean checkInitpwd(String condition) {
		String password = getPassword(condition);
		if("111111".equals(password)){
			return true;
		}else{			
			return false;
		}
	}


	@Override
	public String loginWithToken(String sob, String token, String ip, boolean isMobile, String webSite) {
		Master master = enterpriseService.getMasterByName(sob);
		if (master == null){			
			master = enterpriseService.getMasterByName(BaseUtil.getXmlSetting("defaultSob"));
		}
		SpObserver.putSp(sob);
		
		/**
		 * 通过token获取到UserView实体
		 */
		UserView user = null;
		try {
			user = accountCenterService.getUserByToken(token);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(user == null) {
			return "员工账号不正确!";
		}
		Employee employee = getEmployeeByName(user.getMobile());
		if (employee == null) {
			return "员工账号不正确!";
		} else if (!isMobile && ip != null && employee.getEm_lastip() != null) {
			UserSession us = UserOnlineListener.isOnLine(employee.getEm_id(), sob);
			if (us != null) {
				if (ip.equals(us.getIp())) {
					return "检测到您已登录本系统,请不要重复登录、打开空白页!";
				} else {
					return "账号: " + user.getMobile() + " 已于IP:" + us.getIp() + "登录,如非您本人操作，请及时联系管理员!";
				}
			}
		}
		if (!check_onlyInner(employee, webSite))
			return "当前账号只能在局域网登录!";
		if (ip != null) {
			baseDao.updateByCondition("Employee", "em_lastip='" + ip + "'", "em_id=" + employee.getEm_id());
		}
		return null;
	}


}
