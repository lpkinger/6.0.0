package com.uas.erp.core.interceptor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.sso.SSOHelper;
import com.uas.sso.SSOToken;
import com.uas.sso.entity.UserAccount;
import com.uas.sso.web.spring.AbstractSSOInterceptor;

public class SSOInterceptor extends AbstractSSOInterceptor {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	protected boolean onAuthenticateFailed(HttpServletRequest request, HttpServletResponse response) {
		// 允许非账户中心方式登录
		return true;
	}

	@Override
	protected void onAuthenticateSuccess(HttpServletRequest request, HttpServletResponse response) {
		Employee employee = (Employee) request.getSession().getAttribute("employee");
		if (employee == null) {
			SSOToken token = SSOHelper.attrToken(request);
			if (token.getData() != null) {
				employee = getEmployeeByToken(token);
				if (employee != null) {
					logSession(request.getSession(), employee);
				}
			}
		}
		if (employee != null)
			SystemSession.setUser(employee);
	}

	@Override
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String domain = null;
		if (isSaas()) {
			domain = getSaasRequestDomain(request);
		}
		if (domain != null) {
			SSOHelper.clearLogin(request, response);
			// 这是跳转账户中心登录的方式，暂未启用
			String loginUrl = SSOHelper.getRedirectLoginUrl(request) + "&domain=" + domain;
			response.sendRedirect(loginUrl);
		} else {
			super.sendRedirect(request, response);
		}
	}

	private boolean isSaas() {
		return BaseUtil.getXmlSetting("saas.domain") != null;
	}

	private String getSaasRequestDomain(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		String saasUrl = BaseUtil.getXmlSetting("saas.domain");
		Pattern p = Pattern.compile("http(s)*://(.+)\\." + saasUrl + ".*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(url);
		if (m.find()) {
			return m.group(2);
		}
		return null;
	}

	private Employee getEmployeeByToken(SSOToken token) {
		Employee employee = null;
		UserAccount user = FlexJsonUtil.fromJson(token.getData(), UserAccount.class);
		if (!StringUtils.isEmpty(user.getSpaceDomain())) {
			Master master = enterpriseService.getMasterByDomain(user.getSpaceDomain());
			if (null != master) {
				SpObserver.putSp(master.getMa_name());
				// 检测数据库连接bean是否已创建
				if (ContextUtil.getBean(master.getMa_name()) == null)
					BaseUtil.createDataSource(master);
				if (!StringUtils.isEmpty(user.getMobile())) {
					employee = employeeService.getEmployeeByEmTel(user.getMobile());
				} else if (!StringUtils.isEmpty(user.getUserUU())) {
					employee = employeeService.getEmployeeByEmcode(user.getUserUU().toString());
				}
				if (null != employee) {
					employee.setCurrentMaster(master);
					employee.setEm_master(master.getMa_name());
				}
			}
		}
		return employee;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		try {
			super.afterCompletion(request, response, handler, ex);
		} finally {
			SystemSession.clear();
		}
	}

	private void logSession(HttpSession session, Employee employee) {
		session.setAttribute("employee", employee);
		Enterprise enterprise = enterpriseService.getEnterpriseById(employee.getEm_enid());
		if (enterprise != null) {
			session.setAttribute("en_uu", enterprise.getEn_uu());
			session.setAttribute("en_name", enterprise.getEn_Name());
			session.setAttribute("en_email", enterprise.getEn_Email());
			session.setAttribute("en_admin", enterprise.getEn_Admin());
		}
		session.setAttribute("en_uu", enterprise.getEn_uu());
		session.setAttribute("en_name", enterprise.getEn_Name());
		session.setAttribute("em_uu", employee.getEm_id());
		session.setAttribute("em_id", employee.getEm_id());
		session.setAttribute("em_name", employee.getEm_name());
		session.setAttribute("em_code", employee.getEm_code());
		session.setAttribute("em_position", employee.getEm_position());
		session.setAttribute("em_defaulthsid", employee.getEm_defaulthsid());
		session.setAttribute("em_defaultorid", employee.getEm_defaultorid());
		session.setAttribute("em_defaultorname", employee.getEm_defaultorname());
		session.setAttribute("em_depart", employee.getEm_depart());
		session.setAttribute("em_departmentcode", employee.getEm_departmentcode());
		session.setAttribute("em_type", employee.getEm_type());
		session.setAttribute("username", employee.getEm_code());
		session.setAttribute("language", "zh_CN");
		session.setAttribute("joborgnorelation", enterpriseService.checkJobOrgRelation());
		UserOnlineListener.addUser(employee, session.getId());
	}
}
