package com.uas.mes.api.pda;

/**
 * PDA 登录接口
 * @data  2016年12月21日 下午2:19:44
 */

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.mes.api.core.BaseApiController;
import com.uas.pda.service.PdaLoginService;


@Controller("api.pdaLoginController")
public class PdaLoginController extends BaseApiController{
	@Autowired 
	private PdaLoginService  pdaLoginService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private BaseDao baseDao;
	
	final static String INSERT_LOGINFO = "insert into loginfo (id,indate,sip,usname,uscode,versioncode,terminaltype)values (loginfo_seq.nextval,sysdate,?,?,?,?,?)";
	@RequestMapping(value="api/pda/login.action",method = RequestMethod.POST)
	@ResponseBody
	public  Map<String, Object> login(HttpServletRequest request,HttpSession session,String versioncode,String j_username,String j_password,String master,HttpServletResponse response){
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (j_username != null && j_password != null && master != null) {
			String res = null;
			res = employeeService.loginWithEm(master, j_username, j_password, getIpAddr(request), true,null);
			if (res == null) {
				Employee employee = employeeService.getEmployeeByName(j_username);
				employeeService.setMoLastLoginTime(employee, new Date());
				employee.setEm_master(master);
				employee.setCurrentMaster(getMaster(master));
				session.setAttribute("employee", employee);
				modelMap.put("sessionId", session.getId());
				modelMap.put("success", true);
			//获取权限，配置
			SqlRowList rs = baseDao.queryForRowSet("select sn_caller caller  from PDA$SYSNAVIGATION where nvl(sn_using,0)<>0 and sn_caller in('ProdInOut','Shop')");
			if(rs.next()){
				modelMap.put("power",rs.getResultList());
			}else{
				modelMap.put("power","");
			}
			//仓库
			rs = baseDao.queryForRowSet("select wh_code, wh_description,wh_type  from warehouse where nvl(wh_statuscode,' ')='AUDITED'");
			if(rs.next()){
				modelMap.put("whcode",rs.getResultList());
			}else{
				modelMap.put("whcode","");
			}
			baseDao.execute(INSERT_LOGINFO,getIpAddr(request),employee.getEm_name(),j_username,versioncode,"PDA");
			} else {
				modelMap.put("success", false);
				modelMap.put("reason", res);
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("reason", "非正常请求");
		}
		return modelMap;
	}
	
	/*
	 * 20180514新增了登陆接口
	 * */
	@RequestMapping(value="api/pda/loginGetMasters.action",method = RequestMethod.POST)
	@ResponseBody
	public  Map<String, Object> loginGetMaster(HttpServletRequest request,HttpSession session,String versioncode,String j_username,String j_password,String master,HttpServletResponse response){
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (j_username != null && j_password != null && master != null) {
			String res = null;
			res = employeeService.loginWithEm(master, j_username, j_password, getIpAddr(request), true,null);
			if (res == null) {
				Employee employee = employeeService.getEmployeeByName(j_username);
				employeeService.setMoLastLoginTime(employee, new Date());
				employee.setEm_master(master);
				employee.setCurrentMaster(getMaster(master));
				session.setAttribute("employee", employee);
				modelMap.put("sessionId", session.getId());
				modelMap.put("success", true);
			//获取权限，配置
			SqlRowList rs = baseDao.queryForRowSet("select sn_caller caller  from PDA$SYSNAVIGATION where nvl(sn_using,0)<>0 and sn_caller in('ProdInOut','Shop')");
			if(rs.next()){
				modelMap.put("power",rs.getResultList());
			}else{
				modelMap.put("power","");
			}
			//仓库
			rs = baseDao.queryForRowSet("select wh_code, wh_description,wh_type  from warehouse where nvl(wh_statuscode,' ')='AUDITED'");
			if(rs.next()){
				modelMap.put("whcode",rs.getResultList());
			}else{
				modelMap.put("whcode","");
			}
			//获取所有的账套信息
			    modelMap.put("allMasters", getMasters());
			baseDao.execute(INSERT_LOGINFO,getIpAddr(request),employee.getEm_name(),j_username,versioncode,"PDA");
			} else {
				modelMap.put("success", false);
				modelMap.put("reason", res);
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("reason", "非正常请求");
		}
		return modelMap;
	}
	
	@RequestMapping(value="api/pda/authentication.action",method = RequestMethod.GET)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public Employee getAuthentication(HttpSession session) {
		if(session.getAttribute("employee") == null){
			return null;						
		}
		return SystemSession.getUser();
		
	}	
	
		
	@RequestMapping(value="api/pda/logout.action",  method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> logout(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		pdaLoginService.logout(request);
		modelMap.put("success",true);
		modelMap.put("masters", enterpriseService.getAbleMasters());
		return modelMap;					
	}	
	
	@RequestMapping(value="api/pda/offLine.action")
	@ResponseBody
	public void offLine(HttpSession session, ModelMap modelMap) {
		session.invalidate();						
	}	
	/**
	 * 获取客户端IP
	 */
	public String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
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
	 * 切换账套
	 */
	@RequestMapping("api/pda/changeMaster.action")
	@ResponseBody
	public Map<String, Object> changeMaster(HttpSession session, HttpServletResponse response, String master) {
		session.removeAttribute("hasReminded");// 登录提醒使用session属性
		changeMaster(session, master);		
		Master mast = getMaster(master);
		Map<String,Object> map=new HashMap<String, Object>();
		map.put("sessionId",session.getId());
		map.put("em_code", session.getAttribute("em_code"));
		map.put("em_name",  session.getAttribute("em_name"));
		map.put("em_master",master);
		map.put("ma_function",mast.getMa_function());
		return success(map);
	}
	/**
	 * 获取系统所有账套
	 */
	@RequestMapping("api/pda/getAllMasters.action")
	@ResponseBody
	public Map<String, Object> getMasters() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (isSaas()) {// saas独立使用，不需返回账套信息
			modelMap.put("masters", new JSONArray());
		} else {
			modelMap.put("masters", enterpriseService.getMasters());
		}
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
		try {			
			employee.setEm_master(to);
		} catch (Exception ex) {
			throw new SystemException("切换账套失败");
		}
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
			return employee;
		} else
			throw new SystemException("缺少企业信息");
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
	 * {@config sys.denyVirtualUser} 拒绝虚拟账户登录权限
	 */
	private boolean isDenyVirtualUser() {
		return baseDao.isDBSetting("denyVirtualUserLogin");
	}
}
