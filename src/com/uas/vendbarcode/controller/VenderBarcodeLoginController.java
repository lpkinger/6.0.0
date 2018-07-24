package com.uas.vendbarcode.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.formula.functions.Count;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.dao.SpObserver;
import com.sun.org.apache.regexp.internal.recompile;
import com.sun.tools.internal.ws.processor.model.Request;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.core.support.EmployeeCreater;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.sso.SSOHelper;

@Controller
public class VenderBarcodeLoginController {

	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private BaseDao baseDao;
	
	final static String INSERT_LOGINFO = "insert into loginfo (id,indate,sip,usname,uscode,versioncode,terminaltype)values (loginfo_seq.nextval,sysdate,?,?,?,?,?)";
	
	@RequestMapping(value = "vendbarcode/login.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> login(HttpServletRequest request, HttpSession session, String versioncode,
			String username, String password, @RequestParam("sob") String master, HttpServletResponse response) throws IOException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		SqlRowList rs = null;
		// 如果账套不为空则切换账套校验
		if (master != "") {
			SpObserver.putSp(master);
		} else {
			modelMap.put("success", false);
			modelMap.put("reason", "未选择账套");
			return modelMap;
		}
		// 校验用户名密码是否正确
		try {
			rs = baseDao.queryForRowSet("select * from vendoruser where vu_usercode=? and vu_userpassword=?", username,
					password);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("reason", "用户名或密码错误");
			return modelMap;
		}
		if (rs.next()) {
			// 用户名和密码正确
			if (username != null && password != null && master != null) {
				// 查询供应商简称
				Object shortName = null;
				try {
					shortName = baseDao.getFieldDataByCondition("vendor", "ve_shortname",
							"ve_code='" + rs.getString("vu_code") + "'");
					if (shortName == null) {
						shortName = baseDao.getFieldDataByCondition("vendor", "ve_name",
								"ve_code='" + rs.getString("vu_code") + "'");
					}
				} catch (Exception ex) {
					modelMap.put("success", false);
					modelMap.put("reason", "请检查该账号对应的供应商编号");
					return modelMap;
				}
				// 创建虚拟用户,session中放置供应商编号
				Enterprise enterprise = enterpriseService.getEnterprise();
				if (enterprise != null) {
					session.setAttribute("en_admin", enterprise.getEn_Admin());
				}
				Employee employee = EmployeeCreater.createVirtual(username + "@vendor", enterprise, getMaster(master));
				employee.setEm_name(shortName.toString());
				session.setAttribute("employee", employee);
				session.setAttribute("en_uu", enterprise.getEn_uu());
				session.setAttribute("en_name", enterprise.getEn_Name());
				session.setAttribute("en_uu", enterprise.getEn_uu());
				session.setAttribute("en_name", enterprise.getEn_Name());
				session.setAttribute("em_uu", employee.getEm_id());
				session.setAttribute("em_id", employee.getEm_id());
				session.setAttribute("em_name", shortName.toString());
				session.setAttribute("em_code", employee.getEm_code());
				session.setAttribute("em_type", employee.getEm_type());
				session.setAttribute("username", username);
				session.setAttribute("password", password);
				session.setAttribute("language", "zh_CN");
				// session中放置供应商编号
				session.setAttribute("ve_code", rs.getString("vu_code"));
				//
				modelMap.put("success", true);
				modelMap.put("sessionId", session.getId());
				modelMap.put("ve_code", rs.getString("vu_code"));
				modelMap.put("em_code", username + "@vendor");
				modelMap.put("em_name", shortName.toString());
				//记录登录日志
				Resource resource = ContextUtil.getApplicationContext().getResource("classpath:VERSION");
				String version="";
				if (resource.exists()) {
					// 当前版本
					version = StringUtil.trimBlankChars(FileUtils.readFileToString(resource.getFile(), "UTF-8"));
				}
				baseDao.execute(INSERT_LOGINFO,getIpAddr(request),shortName.toString(),username,version,"供应商条码打印");
			} else {
				modelMap.put("success", false);
				modelMap.put("reason", "非正常请求");
			}
			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("reason", "用户名或密码错误");
			return modelMap;
		}
	}

	@RequestMapping(value = "vendbarcode/getAllMasters.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAllMasters(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("masters", enterpriseService.getMasters());
		request.setAttribute("masters", enterpriseService.getMasters());
		return modelMap;
	}

	/**
	 * logout
	 */
	@RequestMapping("vendbarcode/logout.action")
	public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		session.invalidate();
	}

	@RequestMapping("vendbarcode/getInfoCount.action")
	@ResponseBody
	public Map<String, Object> getInfoCount(HttpSession session, String timestr) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("emp",employee==null);
		modelMap.put("data", baseDao.getFieldDataByCondition("dual", "'INFO'", "1=1"));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 切换账套
	 */
	@RequestMapping("vendbarcode/changeMaster.action")
	@ResponseBody
	public Map<String, Object> changeMaster(HttpSession session, HttpServletResponse response, String to) {
		response.setContentType("application/json");
		session.removeAttribute("hasReminded");// 登录提醒使用session属性
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee em_from = (Employee) session.getAttribute("employee");
		Master fromMaster = em_from.getCurrentMaster();// 反馈编号2018030047
														// 取出master
														// 避免session被修改导致master也被修改
		Employee em = changeMaster(session, to);
		modelMap.put("typeChange", MasterTypeChange(fromMaster, em.getCurrentMaster()));
		modelMap.put("currentMaster", em.getCurrentMaster() != null ? em.getCurrentMaster().getMa_function() : null);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 登陆成功
	 */
	@RequestMapping("vendbarcode/loginSuc.action")
	public ModelAndView loginSuc(HttpServletResponse response) {
		ModelAndView mView = new ModelAndView("vendbarcode/main");
		return mView;
	}

	/**
	 * 重新登录
	 */
	@RequestMapping("vendbarcode/relogin.action")
	public ModelAndView relogin() {
		ModelAndView mView = new ModelAndView("vendbarcode/login");
		return mView;
	}

	private Employee changeMaster(HttpSession session, String to) {
		Employee employee = (Employee) session.getAttribute("employee");
		SpObserver.putSp(to);
		// 判断新账套中是否有账户密码
		SqlRowList rs = baseDao.queryForRowSet(
				"select * from vendoruser where vu_usercode=? and vu_userpassword=? and vu_code =?",
				session.getAttribute("username").toString(), session.getAttribute("password").toString(),
				session.getAttribute("ve_code").toString());
		if (!rs.next()) {
			SpObserver.back();
			throw new SystemException("切换失败，请检查您在该账套的账号和密码.");
		}
		// 查询供应商简称
		Object shortName = "";
		try {
			shortName = baseDao
					.getFieldDataByCondition("vendor", "ve_shortname", "ve_code='" + rs.getString("vu_code") + "'");
			if (shortName == null) {
				shortName = baseDao.getFieldDataByCondition("vendor", "ve_name",
						"ve_code='" + rs.getString("vu_code") + "'");
			}
		} catch (Exception ex) {
			SpObserver.back();
			throw new SystemException("切换失败，请检查您在该账套的供应商编号.");
		}
		employee.setEm_master(to);
		employee.setCurrentMaster(getMaster(to));
		UserOnlineListener.changeMaster(session.getId(), to);
		Enterprise enterprise = enterpriseService.getEnterprise();
		if (enterprise != null) {
			employee.setEm_enid(enterprise.getEn_Id());
			session.setAttribute("en_admin", enterprise.getEn_Admin());
			session.setAttribute("employee", employee);
			session.setAttribute("en_uu", enterprise.getEn_uu());
			session.setAttribute("en_name", enterprise.getEn_Name());
			// 重新绑定session信息
			session.setAttribute("em_uu", employee.getEm_id());
			session.setAttribute("em_id", employee.getEm_id());
			session.setAttribute("em_name", shortName.toString());
			session.setAttribute("em_code", employee.getEm_code());
			session.setAttribute("em_type", employee.getEm_type());
			return employee;
		} else
			throw new SystemException("缺少企业信息");
	}

	private boolean MasterTypeChange(Master FromMaster, Master ToMaster) {
		if (FromMaster != null && ToMaster != null) {
			/**
			 * 不同类型帐套切换时才需要刷新左侧导航栏
			 */
			if (FromMaster.getMa_type() != ToMaster.getMa_type() || FromMaster.getMa_soncode() != null
					|| ToMaster.getMa_soncode() != null)
				return true;
		}
		return false;
	}

	/**
	 * 取当前账套信息
	 * 
	 * @param name
	 * @return Master
	 */
	public Master getMaster(String name) {
		List<Master> masters = enterpriseService.getMasters();
		if (masters != null && name != null) {
			for (Master m : masters) {
				if (name.equals(m.getMa_name())) {
					return m;
				}
			}
		}
		return null;
	}

	/**
	 * 获取客户端IP
	 */
	public String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		if (ipAddress != null && "0:0:0:0:0:0:0:1".equals(ipAddress)) {// window7系统下,用localhost访问时,ip会变成0:0:0:0:0:0:0:1
			ipAddress = "127.0.0.1";
		}
		return ipAddress;
	}
}
