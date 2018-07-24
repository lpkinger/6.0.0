package com.uas.erp.core.interceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.EmployeeCreater;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.AccessTokenService;
import com.uas.erp.service.common.EnterpriseService;

/**
 * 验证access_token拦截器
 * 
 * <pre>
 * 找到token信息并自动登录
 * </pre>
 * 
 * @author yingp
 *
 */
public class AccessTokenInterceptor extends HandlerInterceptorAdapter {

	private static final String access_token_param = "access_token";
	private static final String client_type_param = "client_type";
	private static final String master = "master";

	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private AccessTokenService accessTokenService;
	@Autowired
	private EmployeeDao employeeDao;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("employee");
		if (obj != null) {
			return super.preHandle(request, response, handler);
		} else {
			String access_token = request.getParameter(access_token_param);
			String client_type = request.getParameter(client_type_param);
			String dbmaster = request.getParameter(master);
			Master master = null;
			
			if (access_token != null && client_type != null) {
				if ("b2b".equals(client_type)) {
					if(dbmaster!=null){
						master = enterpriseService.getMasterByName(dbmaster);
					}
					Map<String, Object> data = accessTokenService.validFormB2b(access_token,master);
					if (data.containsKey("user") && data.containsKey("bind")) {
						// 约定bind信息为供应商企业UU
						SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
						master = getMaster(Long.parseLong(data.get("bind").toString()));
						if (master != null) {
							SpObserver.putSp(master.getMa_name());
							Enterprise enterprise = enterpriseService.getEnterprise();
							Object cu_name =baseDao.getFieldDataByCondition("customer","cu_name","cu_uu="+data.get("enUU"));
							session.setAttribute("cu_name",cu_name);
							try {
								Object orcode = baseDao.getFieldDataByCondition("customer","orcode", "cu_uu="+data.get("enUU"));
								session.setAttribute("orcode",orcode == null?"":orcode);
							} catch (Exception e) {
								session.setAttribute("orcode","");
							}
							Employee emp=employeeDao.getEmployeeByConditon("em_uu="+data.get("userUU"));
							Employee employee = EmployeeCreater.createVirtual(String.valueOf(data.get("userUU")),
									String.valueOf(data.get("user")), String.valueOf(data.get("enUU")), enterprise, master,emp);
							logSession(session, enterprise, employee);
							return super.preHandle(request, response, handler);
						}
					}
				}else if("cc".equals(client_type)) {
					if(dbmaster!=null){
						master = enterpriseService.getMasterByName(dbmaster);
					}else{
						master = enterpriseService.getMasterByName(BaseUtil.getXmlSetting("defaultSob"));
					}
					Map<String, Object> data = accessTokenService.validFormCc(access_token,master);
					if (data.containsKey("user") && data.containsKey("bind")) {
						// 约定bind信息为供应商企业UU
						SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
						master = getMaster(Long.parseLong(data.get("bind").toString()));
						if (master != null) {
							SpObserver.putSp(master.getMa_name());
							Enterprise enterprise = enterpriseService.getEnterprise();
							Object cu_name =baseDao.getFieldDataByCondition("customer","cu_name","cu_uu="+data.get("enUU"));
							session.setAttribute("cu_name",cu_name);
							try {
								Object orcode = baseDao.getFieldDataByCondition("customer","orcode", "cu_uu="+data.get("enUU"));
								session.setAttribute("orcode",orcode == null?"":orcode);
							} catch (Exception e) {
								session.setAttribute("orcode","");
							}
							Employee emp=employeeDao.getEmployeeByConditon("em_uu="+data.get("userUU"));
							Employee employee = EmployeeCreater.createVirtual(String.valueOf(data.get("userUU")),
									String.valueOf(data.get("user")), String.valueOf(data.get("enUU")), enterprise, master,emp);
							logSession(session, enterprise, employee);
							return super.preHandle(request, response, handler);
						}
					}
				}
			}
		}
		return false;
	}

/*	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
System.out.println("preHandle");
		HttpSession session = request.getSession();
		Object obj = session.getAttribute("employee");
System.out.println(obj);
		if (obj != null && ((Employee) obj).isCustomerVirtual()) {
			return super.preHandle(request, response, handler);
		} else {
			String access_token = request.getParameter(access_token_param);
			String client_type = request.getParameter(client_type_param);
			if (access_token != null && client_type != null) {
				if ("b2b".equals(client_type)) {					
					Map<String, Object> data = accessTokenService.validFormB2b(access_token);
					if(data.containsKey("bind")){
						SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
						Master master = getMaster(Long.parseLong(data.get("bind").toString()));
						if (master != null) {
							SpObserver.putSp(master.getMa_name());
							if(data.containsKey("user")){
								Enterprise enterprise = enterpriseService.getEnterprise();
								Object cu_name =baseDao.getFieldDataByCondition("customer","cu_name","cu_uu="+data.get("enUU"));
								session.setAttribute("cu_name",cu_name);
								try {
									Object orcode = baseDao.getFieldDataByCondition("customer","orcode", "cu_uu="+data.get("enUU"));
									session.setAttribute("orcode",orcode == null?"":orcode);
								} catch (Exception e) {
									session.setAttribute("orcode","");
								}
								Employee emp=employeeDao.getEmployeeByConditon("em_uu="+data.get("userUU"));
								Employee employee = EmployeeCreater.createVirtual(String.valueOf(data.get("userUU")),
										String.valueOf(data.get("user")), String.valueOf(data.get("enUU")), enterprise, master,emp);
								logSession(session, enterprise, employee);
								return super.preHandle(request, response, handler);								
							}else{
								return true;
							}

						}						
					}
				}
			}
		}
		return true;
	}*/	
	
	/**
	 * 信息写到session
	 * 
	 * @param session
	 * @param enterprise
	 * @param employee
	 */
	private void logSession(HttpSession session, Enterprise enterprise, Employee employee) {
		session.setAttribute("employee", employee);
		session.setAttribute("en_uu", enterprise.getEn_uu());
		session.setAttribute("en_name", enterprise.getEn_Name());
		session.setAttribute("en_uu", enterprise.getEn_uu());
		session.setAttribute("en_name", enterprise.getEn_Name());
		session.setAttribute("em_uu", employee.getEm_uu());
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
		session.setAttribute("enUU", employee.getVirtual_enuu());
		session.setAttribute("language", "zh_CN");
		SystemSession.setUser(employee);
	}

	/**
	 * 用en_uu取当前账套信息
	 * 
	 * @param masters
	 * @param name
	 * @return
	 */
	public Master getMaster(long uu) {
		List<Master> masters = enterpriseService.getMasters();
		if (masters != null) {
			for (Master m : masters) {
				if (m.getMa_uu() != null && uu == m.getMa_uu()) {
					return m;
				}
			}
		}
		return null;
	}
}
