package com.uas.erp.controller.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.uas.erp.core.BaseController;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.ContextUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.Des;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.UserAgentUtil;
import com.uas.erp.core.encry.HmacUtils;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.core.support.EmployeeCreater;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Configs;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Form;
import com.uas.erp.model.Master;
import com.uas.erp.model.Token;
import com.uas.erp.model.UserSession;
import com.uas.erp.service.common.AccessTokenService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.ReportService;
import com.uas.erp.service.common.SingleFormItemsService;
import com.uas.erp.service.common.uu.UserService;
import com.uas.erp.service.ma.ConfigService;
import com.uas.erp.service.ma.LoginImgService;
import com.uas.sso.SSOHelper;

/**
 * 处理用户登录、注销等请求
 * 
 * @author yingp
 * @date 2012-7-26 0:37:45
 */
@Controller
public class LoginController extends BaseController {
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	@Autowired
	private ResourceBundleMessageSource source;
	@Autowired
	private SingleFormItemsService singleFormItemsService;
	@Autowired
	private ReportService reportService;
	@Autowired
	private AccessTokenService accessTokenService;
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private ConfigService configService;
	@Autowired
	private LoginImgService loginImgService;

	// 扫码登陆的token集合
	private static Map<String, Object> tokens = new ConcurrentHashMap<String, Object>();

	private ModelAndView loginWithSession(ModelMap modelMap, HttpSession session, UserSession userSession) {
		if (userSession != null) {
			if (session.getAttribute("employee") != null) {
				return new ModelAndView("/common/main", modelMap);
			}
			String sob = userSession.getSob();
			String period = baseDao.getDBSetting("changepsw", "period");
			Employee employee = employeeService.getEmployeeById(userSession.getEm_id(), sob);
			employee.setEm_master(sob);
			employee.setCurrentMaster(getMaster(sob));
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
			session.setAttribute("em_mobile", employee.getEm_mobile());
			session.setAttribute("em_type", employee.getEm_type());
			session.setAttribute("em_saledepart", employee.getEm_saledepart());
			session.setAttribute("username", employee.getEm_code());
			session.setAttribute("em_cop", employee.getEm_cop());
			session.setAttribute("language", "zh_CN");
			session.setAttribute("joborgnorelation", enterpriseService.checkJobOrgRelation());
			source.setBasename("i18n/messages_zh_CN");
			return new ModelAndView("/common/main", modelMap);
		}

		modelMap.put("masters", enterpriseService.getMasters());// 账套
		modelMap.put("defaultmaster_name", enterpriseService.getDefaultMasterName());
		modelMap.put("defaultmaster_fun", enterpriseService.getDefaultMasterFun());
		modelMap.put("defaultenterprise_name", enterpriseService.getDefaultEnterpriseName());
		return new ModelAndView("/common/login", modelMap);
	}

	/**
	 * token方式登录
	 * 
	 * @param access_token
	 * @param client_type
	 * @return
	 */
	private ModelAndView loginWithTokenInfo(ModelMap modelMap, HttpServletRequest request, String access_token, String client_type) {
		HttpSession session = request.getSession();
		if (session.getAttribute("employee") == null) {
			if ("manage".equals(client_type)) {
				Map<String, Object> data = null;
				try {
					data = accessTokenService.validFormManage(access_token);
				} catch (Exception e) {
					return new ModelAndView("/error/default", error(e.getMessage()));
				}
				if (null != data && data.containsKey("user") && data.containsKey("bind")) {
					Master master = getMaster(Long.parseLong(data.get("bind").toString()));
					if (master != null) {
						SpObserver.putSp(master.getMa_name());
						if (isDenyVirtualUser()) {
							SpObserver.back();
							return new ModelAndView("/error/unauthorized", error("不允许虚拟账户登录"));
						}
						Enterprise enterprise = enterpriseService.getEnterprise();
						if (enterprise != null) {
							session.setAttribute("en_admin", enterprise.getEn_Admin());
						}
						Employee employee = EmployeeCreater.createVirtual(String.valueOf(data.get("user")) + "@manage", enterprise, master);
						session.setAttribute("employee", employee);
						session.setAttribute("en_uu", enterprise.getEn_uu());
						session.setAttribute("en_name", enterprise.getEn_Name());
						session.setAttribute("en_uu", enterprise.getEn_uu());
						session.setAttribute("en_name", enterprise.getEn_Name());
						session.setAttribute("em_uu", employee.getEm_id());
						session.setAttribute("em_id", employee.getEm_id());
						session.setAttribute("em_name", employee.getEm_name());
						session.setAttribute("em_code", employee.getEm_code());
						session.setAttribute("em_type", employee.getEm_type());
						session.setAttribute("em_saledepart", employee.getEm_saledepart());
						session.setAttribute("username", employee.getEm_code());
						session.setAttribute("joborgnorelation", enterpriseService.checkJobOrgRelation());
						session.setAttribute("language", "zh_CN");
						source.setBasename("i18n/messages_zh_CN");
						return new ModelAndView("/common/main", modelMap);
					}
				}
			}
			return new ModelAndView("/common/login", modelMap);
		}
		return new ModelAndView("/common/main", modelMap);
	}

	/**
	 * 根据session判断用户是否已登录
	 */
	@RequestMapping("/common/checkLogin.action")
	public ModelAndView checkLogin(HttpServletRequest request, HttpSession session, ModelMap modelMap, String u, String t, String d,
			Integer mobile, String _sid, String master, String access_token, String client_type) {

		boolean isSaas = isSaas();
		if (isSaas)
			session.setAttribute("isSaas", true);
		if (_sid != null) {
			return loginWithSession(modelMap, session, UserOnlineListener.getUserBySId(_sid));
		}
		// Object language = session.getAttribute("language");
		// if (language == null) {// session没有，就从cookie里面取
		// Cookie[] cookies = request.getCookies();
		// if (cookies != null) {
		// for (Cookie c : cookies) {
		// if (c.getName().equals("language")) {
		// language = c.getValue();
		// }
		// }
		// }
		// }// 这样可以实现在用户下次登录时，显示的是用户上次选择的语言
		// language = language == null ? "zh_CN" : (String) language;
		// source.setBasename("i18n/messages_" + language);
		String language = "zh_CN";
		source.setBasename("i18n/messages_" + language);
		/*
		 * boolean _mobile =
		 * UserAgentUtil.isMobile(request.getHeader("user-agent")); if (_mobile
		 * && mobile != null && 0 == mobile) _mobile = false;
		 */
		/**
		 * 免登录，直接进入主页
		 * 
		 * @param u
		 *            userName
		 * @param t
		 *            encryptedPassword
		 * @param d
		 *            passwordKey
		 */
		if (access_token != null && client_type != null) {
			return loginWithTokenInfo(modelMap, request, access_token, client_type);
		} else if (u != null && t != null) {
			try {
				return fastLogin(request, session, modelMap, language.toString(), u, t, d, master, false);
			} catch (Exception e) {
				if (isSaas)
					return saasLogin(request);
				return new ModelAndView("/common/login", modelMap);
			}
		} else {
			// session.setAttribute("_mobile", _mobile);
			if (session.getAttribute("employee") != null) {
				Employee employee = (Employee) session.getAttribute("employee");
				if (master != null && !master.equals(employee.getCurrentMaster().getMa_name())) {
					employee = changeMaster(session, master);
				}
				/*
				 * if (_mobile) { modelMap.put("masters",
				 * enterpriseService.getMasters());// 账套
				 * modelMap.put("mobileinfo",
				 * userService.getMobileInfo(employee)); return new
				 * ModelAndView("/mobile/nav", modelMap); } else
				 */
				if (UserOnlineListener.isOnLine(session.getId())) {
					if (isSaas) {
						if (!employee.getCurrentMaster().isEnable())
							return new ModelAndView("/saas/error_disable");
						/*
						 * if (!employee.getCurrentMaster().isInit()) return new
						 * ModelAndView("/sys/sysinit");
						 */
					}
					return new ModelAndView("/common/main", modelMap);
				} else {
					UserOnlineListener.addUser(employee, session.getId());
					if (isSaas) {
						if (!employee.getCurrentMaster().isEnable())
							return new ModelAndView("/saas/error_disable");
						/*
						 * if (!employee.getCurrentMaster().isInit()) return new
						 * ModelAndView("/sys/sysinit");
						 */
					}
					return new ModelAndView("/common/main", modelMap);
				}
			} else {
				modelMap.put("masters", enterpriseService.getAbleMasters());// 获取未被禁用的账套账套
				modelMap.put("defaultmaster_name", enterpriseService.getDefaultMasterName());
				modelMap.put("defaultmaster_fun", enterpriseService.getDefaultMasterFun());
				modelMap.put("defaultenterprise_name", enterpriseService.getDefaultEnterpriseName());
				//判断是否设置有背景图片路径  且 该路径下能找到文件
				Map<String,Object> LoginImg = loginImgService.hasLoginImg();
				if((Boolean)LoginImg.get("success") == false){
					modelMap.put("hasLoginImg", "false");
				}else{					
					InputStream in = null;
					try{
						File file = new File(LoginImg.get("filepath").toString());
						in = new FileInputStream(file);
						modelMap.put("hasLoginImg", "true");
					}catch(Exception e){
						modelMap.put("hasLoginImg", "false");
					}
					try {
						in.close();
					} catch (Exception e) {
					}
				}
				/*
				 * if (_mobile) { return new ModelAndView("/mobile/login",
				 * modelMap); } else {
				 */
				if (isSaas)
					return saasLogin(request);
				/*
				 * String reqUrl=request.getRequestURL().toString();
				 * if(reqUrl.indexOf("192.168.253.103:8080/ERP")>0) return new
				 * ModelAndView("/common/loginTest", modelMap);
				 */
				/*
				 * if(reqUrl.indexOf("192.168.253.111:8090/ERP")>0 ||
				 * reqUrl.indexOf("218.18.115.198:8888/ERP")>0 ||
				 * reqUrl.indexOf("192.168.253.199:8099/ERP")>0 ||
				 * reqUrl.indexOf("10.1.1.168:8099/ERP")>0) return new
				 * ModelAndView("/common/loginTest", modelMap);
				 */
				/* return new ModelAndView("/common/login", modelMap); */
				return new ModelAndView("/common/loginTest", modelMap);
				/* return null; */
				// }
			}
		}
	}

	/**
	 * 是否saas域名访问
	 * 
	 * @return
	 */
	private boolean isSaas() {
		return BaseUtil.getXmlSetting("saas.domain") != null;
	}

	/**
	 * saas登录
	 * 
	 * @param request
	 * @return
	 */
	private ModelAndView saasLogin(HttpServletRequest request) {
		// 域名通过http_proxy重定向后无法从request得到客户端请求路径
		return new ModelAndView("/saas/login");
	}

	private ModelAndView fastLogin(HttpServletRequest request, HttpSession session, ModelMap modelMap, String language, String u, String t,
			String d, String master, boolean _mobile) {
		try {
			String sob = master == null ? BaseUtil.getXmlSetting("defaultSob") : master;
			String res = employeeService.loginWithEm(sob, u, userService.decryptPassword(t, d), UserAgentUtil.getIpAddr(request), _mobile,
					request.getHeader(""));
			boolean changepsw = false;
			if (res == null) {// 表示登录成功
				modelMap.put("success", true);
				Employee employee = employeeService.getEmployeeByName(u);
				try {
					// 判断是否需要定期提醒修改密码
					String period = baseDao.getDBSetting("changepsw", "period");
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					if (period != null && !"".equals(period) && !"-1".equals(period)) {
						Date pwdupdate = employee.getEm_pwdupdatedate();
						if (pwdupdate != null) {
							int count = DateUtil.countDates(simpleDateFormat.format(pwdupdate), simpleDateFormat.format(new Date()));
							if (count >= Integer.parseInt(period)) {
								changepsw = true;
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				Enterprise enterprise = enterpriseService.getEnterpriseById(employee.getEm_enid());
				if (enterprise != null) {
					session.setAttribute("en_admin", enterprise.getEn_Admin());
				}
				employee.setEm_master(sob);
				employee.setCurrentMaster(getMaster(sob));
				session.setAttribute("employee", employee);
				session.setAttribute("en_uu", enterprise.getEn_uu());
				session.setAttribute("en_name", enterprise.getEn_Name());
				session.setAttribute("en_email", enterprise.getEn_Email());
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
				session.setAttribute("em_saledepart", employee.getEm_saledepart());
				session.setAttribute("em_mobile", employee.getEm_mobile());
				session.setAttribute("username", u);
				session.setAttribute("changepsw", changepsw);
				session.setAttribute("language", language);
				session.setAttribute("joborgnorelation", enterpriseService.checkJobOrgRelation());
				source.setBasename("i18n/messages_zh_CN");
				modelMap.put("em_name", employee.getEm_name());
				return new ModelAndView("/common/main", modelMap);
			} else {
				modelMap.put("defaultmaster_name", enterpriseService.getDefaultMasterName());
				modelMap.put("defaultmaster_fun", enterpriseService.getDefaultMasterFun());
				modelMap.put("defaultenterprise_name", enterpriseService.getDefaultEnterpriseName());
				modelMap.put("masters", enterpriseService.getMasters());// 账套
				return new ModelAndView("/common/login", modelMap);
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 用户点击登录之后的处理方法
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @param sob
	 *            账套
	 * @throws Exception
	 */
	@RequestMapping("/common/login.action")
	@ResponseBody
	public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response, HttpSession session, String username,
			String password, String language, String sob, Integer uu) throws Exception {
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String res = null;
		boolean changepsw = false;
		if (uu != null && sob == null) {
			sob = enterpriseService.getMasterByUU(uu);
			if (sob == null) {
				modelMap.put("success", false);
				modelMap.put("reason", "企业UU不存在或未开通SAAS服务");
				return modelMap;
			}
			modelMap.put("_sid", session.getId());
		}
		res = employeeService.loginWithEm(sob, username, password, UserAgentUtil.getIpAddr(request), false, request.getHeader("referer"));
		if (res == null) {// 表示登录成功
			/**
			 * 登陆成功 则清除em_counterror;
			 */
			baseDao.updateByCondition("employee", "EM_COUNTERROR=0", "lower(em_code)=lower('" + username + "') or em_mobile='" + username
					+ "'");
			if (BaseUtil.getXmlSetting("saas.domain") == null) {
				String b = BaseUtil.getXmlSetting("defaultSob");
				String codes = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ma_user) from " + b + ".master where nvl(ma_enable,0)<>0", String.class);
				if (codes != null) {
					String[] sobs = codes.split(",");
					for (String s : sobs) {
						baseDao.updateByCondition(s + ".employee", "EM_COUNTERROR=0", "(lower(em_code)=lower('" + username
								+ "') or em_mobile='" + username + "') and nvl(EM_COUNTERROR,0)<>0");
					}
				}
			}
			modelMap.put("success", true);
			Employee employee = employeeService.getEmployeeByName(username);
			try {
				// 判断是否需要定期提醒修改密码
				String period = baseDao.getDBSetting("changepsw", "period");
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (period != null && !"".equals(period) && !"-1".equals(period)) {
					Date pwdupdate = employee.getEm_pwdupdatedate();
					if (pwdupdate != null) {
						int count = DateUtil.countDates(simpleDateFormat.format(pwdupdate), simpleDateFormat.format(new Date()));
						if (count >= Integer.parseInt(period)) {
							changepsw = true;
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			employee.setEm_master(sob);
			employee.setCurrentMaster(getMaster(sob));
			Enterprise enterprise = enterpriseService.getEnterpriseById(employee.getEm_enid());
			session.setAttribute("employee", employee);
			if (enterprise != null) {
				session.setAttribute("en_uu", enterprise.getEn_uu());
				session.setAttribute("en_name", enterprise.getEn_Name());
				session.setAttribute("en_email", enterprise.getEn_Email());
				session.setAttribute("en_admin", enterprise.getEn_Admin());
			}
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
			session.setAttribute("em_saledepart", employee.getEm_saledepart());
			session.setAttribute("em_mobile", employee.getEm_mobile());
			session.setAttribute("username", username);
			session.setAttribute("em_cop", employee.getEm_cop());
			session.setAttribute("changepsw", changepsw);
			language = "zh_CN";
			session.setAttribute("language", language);
			session.setAttribute("joborgnorelation", enterpriseService.checkJobOrgRelation());
			source.setBasename("i18n/messages_" + language);
			modelMap.put("em_name", employee.getEm_name());
		} else {
			baseDao.updateByCondition("employee", "EM_COUNTERROR=nvl(EM_COUNTERROR,0)+1", "lower(em_code)=lower('" + username
					+ "') or em_mobile='" + username + "' ");
			if (BaseUtil.getXmlSetting("saas.domain") == null) {
				String b = BaseUtil.getXmlSetting("defaultSob");
				String codes = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ma_user) from " + b + ".master", String.class);
				if (codes != null) {
					String[] sobs = codes.split(",");
					for (String s : sobs) {
						if (!s.equals(sob)) {
							baseDao.updateByCondition(s + ".employee", "EM_COUNTERROR=nvl(EM_COUNTERROR,0)+1", "lower(em_code)=lower('"
									+ username + "') or em_mobile='" + username + "'");
						}

					}
				}
			}
			modelMap.put("success", false);
			modelMap.put("reason", res);
		}
		return modelMap;
	}

	/**
	 * 从session拿到验证码
	 * 
	 * @return code 验证码
	 */
	@RequestMapping("/common/validCode.action")
	@ResponseBody
	public Map<String, Object> validCode(HttpSession session, @RequestParam(required = true) String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object vCode = session.getAttribute("validcode");
		if (vCode != null && code.toUpperCase().equals(vCode.toString().toUpperCase())) {
			modelMap.put("success", true);
		}
		return modelMap;
	}

	/**
	 * 用户名输入三次以上需要输入验证码common/countError.action
	 */
	@RequestMapping("/common/countError.action")
	@ResponseBody
	public Map<String, Object> countError(HttpSession session, @RequestParam(required = true) String em_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object count = baseDao.getFieldDataByCondition("employee", "nvl(EM_COUNTERROR,0)", "lower(em_code)=lower('" + em_code
				+ "') or em_mobile='" + em_code + "'");
		modelMap.put("count", count == null ? 0 : count);
		return modelMap;
	}

	/**
	 * 系统所有账套
	 */
	@RequestMapping("/common/getMasters.action")
	@ResponseBody
	public Map<String, Object> getMasters(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("masters", enterpriseService.getMasters());
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("currentMaster", employee.getEm_master());
		modelMap.put("_type", employee.getEm_type());// 当前人的员工类型(admin默认可支配全部账套)
		modelMap.put("_master", employee.getEm_masters());// 当前人的可支配账套
		modelMap.put("group", BaseUtil.getXmlSetting("group"));
		modelMap.put("defaultSob", BaseUtil.getXmlSetting("defaultSob"));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取未被禁用的账套
	 * */
	@RequestMapping("/common/getAbleMasters.action")
	@ResponseBody
	public Map<String, Object> getAbleMasters(HttpSession session,Boolean isOwnerMaster) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 特殊要求，全选时不要选中；可单独勾选
		Configs config = configService.getConfig("sys", "specialMastersWhenSelectAll");
		modelMap.put("special", null == config ? null : config.getData());
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("masters", enterpriseService.getAbleMastersByEmMasters(employee.getEm_masters(),isOwnerMaster));
		modelMap.put("currentMaster", employee.getEm_master());
		modelMap.put("_type", employee.getEm_type());// 当前人的员工类型(admin默认可支配全部账套)
		modelMap.put("_master", employee.getEm_masters());// 当前人的可支配账套
		modelMap.put("group", BaseUtil.getXmlSetting("group"));
		SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		Configs syncControl = configService.getConfig("sys", "sys_syncControl");
		modelMap.put("syncControl", null == syncControl ? null : syncControl.getData());
		modelMap.put("defaultSob", BaseUtil.getXmlSetting("defaultSob"));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 切换账套
	 */
	@RequestMapping("/common/changeMaster.action")
	@ResponseBody
	public Map<String, Object> changeMaster(HttpSession session, HttpServletResponse response, String to) {
		response.setContentType("application/json");
		session.removeAttribute("hasReminded");// 登录提醒使用session属性
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee em_from = (Employee) session.getAttribute("employee");
		Master fromMaster=em_from.getCurrentMaster();//反馈编号2018030047 取出master 避免session被修改导致master也被修改
		Employee em = changeMaster(session, to);
		modelMap.put("typeChange", MasterTypeChange(fromMaster, em.getCurrentMaster()));
		modelMap.put("currentMaster", em.getCurrentMaster() != null ? em.getCurrentMaster().getMa_function() : null);
		modelMap.put("success", true);
		return modelMap;
	}

	private Employee changeMaster(HttpSession session, String to) {
		Employee employee = (Employee) session.getAttribute("employee");
		SpObserver.putSp(to);
		// 兼容虚拟账户
		if (!employee.isAdminVirtual())
			employee = employeeService.getEmployeeByName(employee.getEm_code());
		else if (isDenyVirtualUser()) {
			SpObserver.back();
			throw new SystemException("权限缺失：不允许虚拟账户登录，请使用员工账户");
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
			session.setAttribute("em_name", employee.getEm_name());
			session.setAttribute("em_code", employee.getEm_code());
			session.setAttribute("em_position", employee.getEm_position());
			session.setAttribute("em_defaulthsid", employee.getEm_defaulthsid());
			session.setAttribute("em_defaultorid", employee.getEm_defaultorid());
			session.setAttribute("em_defaultorname", employee.getEm_defaultorname());
			session.setAttribute("em_depart", employee.getEm_depart());
			session.setAttribute("em_departmentcode", employee.getEm_departmentcode());
			session.setAttribute("em_type", employee.getEm_type());
			session.setAttribute("em_saledepart", employee.getEm_saledepart());
			session.setAttribute("em_mobile", employee.getEm_mobile());
			return employee;
		} else
			throw new SystemException("缺少企业信息");
	}

	private boolean MasterTypeChange(Master FromMaster, Master ToMaster) {
		//直接使用master参数
		/*Master FromMaster = from.getCurrentMaster();
		Master ToMaster = to.getCurrentMaster();*/
		if (FromMaster != null && ToMaster != null) {
			/**
			 * 不同类型帐套切换时才需要刷新左侧导航栏
			 * */
			if (FromMaster.getMa_type() != ToMaster.getMa_type() || FromMaster.getMa_soncode() != null || ToMaster.getMa_soncode() != null)
				return true;
		}
		return false;
	}

	/**
	 * 当前账套
	 */
	@RequestMapping("/common/getMasterByEm.action")
	@ResponseBody
	public Map<String, Object> getMasterByEm(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("master", employeeService.getMaster((Employee) session.getAttribute("employee")));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * logout
	 */
	@RequestMapping("/common/logout.action")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	@CacheEvict(value = "tree", allEntries = true)
	public void logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		session.invalidate();
		if (isSaas()) {
			// 账户中心一起退出
			SSOHelper.clearLogin(request, response);
		}
	}

	/**
	 * 打印地址
	 */
	@RequestMapping("/common/enterprise/getprinturl.action")
	@ResponseBody
	public Map<String, Object> getPrintUrl(HttpServletRequest request, HttpSession session, String caller, String reportName) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		Enterprise enterprise = enterpriseService.getEnterpriseById(employee.getEm_enid());
		if (enterprise == null) {
			enterprise = enterpriseService.getEnterprise();// 企业信息
		}
		try {
			modelMap.put("printurl", enterprise.getEn_printurl());
			modelMap.put("whichsystem", enterprise.getEn_whichsystem());
			// 判断是否外网表示
			boolean accessible = UserAgentUtil.accessible(request, enterprise.getEn_printurl());
			if (accessible) {
				modelMap.put("printurl", enterprise.getEn_printurl());
			} else {
				modelMap.put("printurl", enterprise.getEn_Url());
			}
		} catch (Exception ex) {

		}
		modelMap.put("ErpPrintLargeData", userService.getDBSetting("ErpPrintLargeData"));
		modelMap.put("reportName", userService.getDBSetting("reportName"));
		if (caller != null) {
			String rptName = null;
			if (!StringUtil.hasText(reportName)) {
				Object[] reportinfo = reportService.getReportPathAndCondition(caller);
				if (reportinfo != null) {
					rptName = reportinfo[0] == null ? null : reportinfo[0].toString();
					String defaultcondition = reportinfo[1] == null ? "" : reportinfo[1].toString();
					modelMap.put("defaultcondition", defaultcondition);
				}
				String printtype = baseDao.getFieldValue("reportfiles", "printtype", "caller='" + caller + "'", String.class);
				modelMap.put("printtype", printtype);
			} else {
				if ("customzl".equals(caller) && "ESTIMATE_custom".equals(reportName)// 应付暂估
						&& baseDao.isDBSetting("sys", "autoCreateApBill") && baseDao.isDBSetting("sys", "useBillOutAP")) {
					reportName = "ESTIMATE_custom_auto";
				}
				if ("customzl".equals(caller) && "GOODSSEND_custom".equals(reportName) && // 发出商品账龄
						baseDao.isDBSetting("sys", "autoCreateArBill") && baseDao.isDBSetting("sys", "useBillOutAR")) {
					reportName = "GOODSSEND_custom_auto";
				}
				rptName = reportService.getReportPath(caller, reportName);
				String printtype = baseDao.getFieldValue("reportfiles", "printtype", "caller='" + caller + "' and file_name='" + rptName
						+ "'", String.class);
				modelMap.put("printtype", printtype);
			}
			Form form = singleFormItemsService.getForm(caller);
			if (form != null) {
				modelMap.put("condition", form.getFo_detailcondition());
				rptName = rptName == null ? form.getFo_reportname() : rptName;
			}
			/*
			 * String printtype=baseDao.getFieldValue("reportfiles",
			 * "printtype", "caller='" + caller + "' and file_name='" + rptName
			 * + "'", String.class); modelMap.put("printtype",printtype);
			 */
			modelMap.put("reportname", rptName);
			if (rptName != null) {
				Des des = new Des();
				try {
					String report = des.toHexString(des.encrypt(rptName, "12345678")).toUpperCase();
					modelMap.put("report", report);
				} catch (UnsupportedEncodingException e) {

				} catch (Exception e) {

				}
			}
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取当前账套信息
	 * 
	 * @param masters
	 * @param name
	 * @return
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
	 * 用ma_manageid取当前账套信息
	 * 
	 * @param masters
	 * @param name
	 * @return
	 */
	public Master getMaster(long manageId) {
		List<Master> masters = enterpriseService.getMasters();
		if (masters != null) {
			for (Master m : masters) {
				if (m.getMa_manageid() != null && manageId == m.getMa_manageid()) {
					return m;
				}
			}
		}
		// 新添加账套，缓存里面不存在的情况下，刷新缓存+创建数据源
		Master master = enterpriseService.getMasterByManage(manageId);
		if (master != null) {
			enterpriseService.clearMasterCache();
			if (ContextUtil.getBean(master.getMa_name()) == null)
				BaseUtil.createDataSource(master);
		}
		return master;
	}

	/**
	 * 取外部账套信息
	 * */
	@RequestMapping("/common/getOutMasters")
	@ResponseBody
	public Map<String, Object> getOutMasters(HttpSession session) {
		Employee employee = SystemSession.getUser();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", enterpriseService.getOutMasters());
		map.put("currentMaster", employee.getEm_master());
		return map;
	}

	/**
	 * 获取b2c商城的链接地址
	 * */
	@RequestMapping("/common/getB2CUrl.action")
	@ResponseBody
	public Map<String, Object> getB2CUrl() {
		Employee employee = SystemSession.getUser();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("b2curl", employee.getCurrentMaster().getB2CUrl());
		map.put("success", true);
		return map;
	}

	/**
	 * {@config sys.denyVirtualUser} 拒绝虚拟账户登录权限
	 */
	private boolean isDenyVirtualUser() {
		return baseDao.isDBSetting("denyVirtualUserLogin");
	}

	/**
	 * 获取二维码
	 * 
	 * @param req
	 * @param clientId
	 *            前端生成的唯一id
	 * @param res
	 * @throws WriterException
	 * @throws IOException
	 * @author zhoudw & chenW
	 */
	@RequestMapping(value = "/common/qrcode.action", method = RequestMethod.GET)
	@ResponseBody
	public void qrcode(HttpServletRequest request, String clientId, HttpServletResponse response) {
		// 检查是否有失效,但未被清除的token,执行移除
		Token token = null;
		long nowtime = new Date().getTime() / 1000;
		long tokentime;
		for (Map.Entry<String, Object> entry : tokens.entrySet()) {
			token = (Token) entry.getValue();
			tokentime = token.getTime().getTime() / 1000;
			if ((nowtime - tokentime) > 120) {
				tokens.remove(entry.getKey());
			}
		}
		try {
			OutputStream out = response.getOutputStream();
			// 生成二维码图像到输出流中
			buildQrcode(clientId, request, out);
			// 添加token
			tokens.put(clientId, new Token(new Date()));
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查二维码登录
	 * 
	 * @param clientId
	 *            前端生成的唯一id
	 * @param session
	 * @param request
	 * @param language
	 * @return
	 * @author zhoudw & chenW
	 */
	@RequestMapping(value = "/common/checkQrcodeLogin.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> checkQrcodeLogin(String clientId, HttpSession session, HttpServletRequest request, String language) {
		Map<String, Object> modelmap = new HashMap<String, Object>();
		Boolean bool = tokens.containsKey(clientId);
		if (bool) {
			Token token = (Token) tokens.get(clientId);
			if ("201".equals(token.getStatus())) {
				// 切换到对应套账
				String res = employeeService.loginWithEmQrcode(token.getSob(), token.getEm_code(),session.getId(),UserAgentUtil.getIpAddr(request), false, request.getHeader("referer"));
				//lastip地址修改后的employee
				SpObserver.putSp(token.getSob());
				Employee employee = employeeService.getEmployeeByName(token.getEm_code());
				// 去除tokens集合里想对应的token对象
				tokens.remove(clientId);
				if (res != null) {
					// ip地址已登录，局域网之类的错误特殊原因
					// 通知前台展示原因
					modelmap.put("success", false);
					modelmap.put("reason", res);
				} else {
					// 进行session添加
					// 保存套账状态
					employee.setEm_master(token.getSob());
					employee.setCurrentMaster(getMaster(token.getSob()));
					Enterprise enterprise = enterpriseService.getEnterpriseById(employee.getEm_enid());
					session.setAttribute("employee", employee);
					if (enterprise != null) {
						session.setAttribute("en_uu", enterprise.getEn_uu());
						session.setAttribute("en_name", enterprise.getEn_Name());
						session.setAttribute("en_email", enterprise.getEn_Email());
						session.setAttribute("en_admin", enterprise.getEn_Admin());
					}
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
					session.setAttribute("em_saledepart", employee.getEm_saledepart());
					session.setAttribute("username", token.getEm_code());
					session.setAttribute("em_mobile", employee.getEm_mobile());
					// session.setAttribute("changepsw", changepsw);
					language = "zh_CN";
					session.setAttribute("language", language);
					session.setAttribute("joborgnorelation", enterpriseService.checkJobOrgRelation());
					// 通知前台停止论询
					modelmap.put("success", true);
				}
			}
		} else {
			// 只是clientID找不到，无特殊原因
			modelmap.put("success", false);
			modelmap.put("reason", null);
		}
		return modelmap;
	}

	/**
	 * 取消二维码登录
	 * 
	 * @param clientId
	 * @param res
	 * @return
	 * @author zhoudw & chenW
	 */
	@RequestMapping(value = "/common/cancelQrcodeLogin.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> cancelQrcodeLogin(String clientId, HttpServletResponse res) {
		res.setHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Boolean bool = tokens.containsKey(clientId);
		if (bool) {
			tokens.remove(clientId);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
		}
		return modelMap;
	}

	/**
	 * 检查二维码扫描
	 * 
	 * @param modelMap
	 * @param request
	 * @param clientId
	 *            前端生成的唯一id
	 * @param em_code
	 *            用户名
	 * @param sob
	 *            帐套
	 * @param password
	 *            密码
	 * @return
	 * @author zhoudw & chenW
	 */
	@RequestMapping(value = "/common/checkQrcodeScan.action", method = RequestMethod.GET)
	public ModelAndView checkQrcodeScan(ModelMap modelMap, HttpServletRequest request, String clientId, String em_code, String sob,
			String password) {
		if (clientId == null || em_code == null || sob == null || password == null) { // 如果传过来的参数中含有空值，返回错误页面
			return new ModelAndView("/common/loginMobileError", modelMap);
		} else {
			// 判断clientId是否存在
			Boolean bool = tokens.containsKey(clientId);
			if (bool) {
				Master master = enterpriseService.getMasterByName(sob);
				if (master == null) {
					// 如果该账套在库中不存在
					return new ModelAndView("/common/loginMobileError", modelMap);
				}
				// 切换到对应套账
				SpObserver.putSp(sob);
				Employee employee = employeeService.getEmployeeByName(em_code);
				// 校验密码
				if (employee != null && password.equals(employee.getEm_password())) {
					Date time = new Date();
					modelMap.put("clientId", clientId);
					modelMap.put("em_code", em_code);
					modelMap.put("sob", sob);
					modelMap.put("time", time);
					// 生成密钥
					modelMap.put("key", HmacUtils.encode(em_code + employee.getEm_password() + time));
					return new ModelAndView("/common/loginMobileConfirm", modelMap);
				} else {
					return new ModelAndView("/common/loginMobileError", modelMap);
				}
			} else {
				return new ModelAndView("/common/loginMobileError", modelMap);
			} // end of bool
		} // end of 空值判断
	}

	/**
	 * 手机确认登录
	 * 
	 * @param res
	 * @param clientId
	 *            前端生成的唯一id
	 * @param em_code
	 *            用户名
	 * @param sob
	 *            帐套
	 * @return
	 * @author zhoudw & chenW
	 */
	@RequestMapping(value = "/common/checkQrcodeConfirm.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> checkQrcodeConfirmLogin(HttpServletResponse res, String clientId, String em_code, String sob, String time,
			String key) {
		res.setHeader("Access-Control-Allow-Origin", "*");
		// 根据clientId改变对应token的状态
		Boolean bool = tokens.containsKey(clientId);
		String psw = employeeService.getEmployeeByName(em_code).getEm_password();
		Map<String, Object> modelmap = new HashMap<String, Object>();
		if (bool) {
			// 比较密钥
			if (key.equals(HmacUtils.encode(em_code + psw + time))) {
				Token token = (Token) tokens.get(clientId);
				token.setStatus("201");
				token.setEm_code(em_code);
				token.setSob(sob);
				// 执行操作成功后，返回数据给手机页面，使其关闭
				modelmap.put("success", true);
			}
		} else {
			modelmap.put("success", false);
		}
		return modelmap;
	}

	/**
	 * 生成二维码
	 * 
	 * @param clientId
	 * @param req
	 * @param out
	 * @throws WriterException
	 * @throws IOException
	 * @author zhoudw & chenW
	 */
	public void buildQrcode(String clientId, HttpServletRequest req, OutputStream out) throws WriterException, IOException {
		JSONObject object = new JSONObject();
		object.put("clientId", clientId);
		int width = 140; // 图像宽度
		int height = 140; // 图像高度
		String format = "png";// 图像类型
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(object.toJSONString(), BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
		MatrixToImageWriter.writeToStream(bitMatrix, format, out);// 输出图像
	}

}
