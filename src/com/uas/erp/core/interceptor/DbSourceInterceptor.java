package com.uas.erp.core.interceptor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.encry.HmacUtils;
import com.uas.erp.core.logging.BufferedLoggerManager;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.impl.DebugBufferedLogger;

/**
 * 自定义拦截器 每次请求之前根据session里面的账套信息切换数据源
 * 
 * @author yingp
 */
public class DbSourceInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private EnterpriseService enterpriseService;
	/**
	 * 异步记录debug日志的工具
	 */
	private DebugBufferedLogger debugLogger = BufferedLoggerManager.getLogger(DebugBufferedLogger.class);

	/**
	 * 处理前台请求之前执行
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		Object obj = req.getSession().getAttribute("employee");
		String sessionId = req.getParameter("sessionId");
		String sessionUser = req.getParameter("sessionUser");
		String referer = req.getHeader("referer");
		String master = req.getParameter("master");
		if (obj == null && referer != null && referer.indexOf("_signature") > 0) {
			String _signature = referer.substring(referer.indexOf("_signature") + 11, referer.length());
			String urlMessage = referer.substring(0, referer.indexOf("_signature") - 1);
			if (_signature.equals(HmacUtils.encode(urlMessage))) {
				if (master == null && referer.indexOf("&master=") > 0) {
					int index = referer.indexOf("&master=");
					master = referer.substring(index + 8, referer.indexOf("&", index + 8));
				}
				obj = CreateDemoEmployee(master);
				req.getSession().setAttribute("employee", obj);
				req.getSession().setAttribute("language", "zh_CN");
			}
		}
		if (!StringUtils.isEmpty(master)) {
			SpObserver.putSp(master);
		}
		if (obj == null && sessionId != null)
			try {
				if (sessionUser != null && employeeService.checkAppToken(sessionId, req.getSession().getId(), sessionUser, 1)) {
					obj = CreateSessionEmployee(sessionUser);
					req.getSession().setAttribute("employee", obj);
					req.getSession().setAttribute("language", "zh_CN");
					req.getSession().setMaxInactiveInterval(1800);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (obj != null) {
			Employee employee = (Employee) obj;
			// 记录到当前线程里面
			SystemSession.setUser(employee);
			SystemSession.setLang(req.getSession().getAttribute("language"));
			String db = employee.getEm_master();
			if (StringUtils.isEmpty(master) && !StringUtils.isEmpty(db)) {
				SpObserver.putSp(db);
			}
			
			if ("true".equals(BaseUtil.getXmlSetting("debug"))) {
				debugLogger.log(req, employee, SpObserver.getSp());
			}
		} else {
			if (StringUtils.isEmpty(master))
				SpObserver.putSp(BaseUtil.getXmlSetting("defaultSob"));
		}

		return true;
	}

	/**
	 * 处理完前台请求之后执行
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		if ("true".equals(BaseUtil.getXmlSetting("debug"))) {
			Object debugId = request.getAttribute(DebugBufferedLogger.debugAttribute);
			if (debugId != null)
				debugLogger.success(request, debugId.toString());
		}
		try{			
			//判断批处理界面是否配置了消息模板
			if(request.getParameter("caller")!=null && request.getParameter("data")!=null){
			Object obj = request.getSession().getAttribute("employee");
			Employee em = (Employee) obj;
			Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='"+request.getParameter("caller")+"' and MM_OPERATE='batchDeal' and mm_action='"+request.getRequestURI()+"'");
				//调用生成消息的存储过程
				if (mmid != null) {
					Object keyfield = baseDao.getFieldDataByCondition("detailgrid", "dg_field" , "dg_caller='"+ request.getParameter("caller") + "' AND dg_logictype='keyField'");
					if(keyfield!=null){
						String keyValue ="";
						List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(request.getParameter("data"));
						for (Map<Object, Object> map : grid) {
							if(map.get(keyfield)!=null && !"".equals(map.get(keyfield)))
							keyValue =keyValue+map.get(keyfield)+",";
						}
						if(keyValue.length()>0){
							baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid, em.getEm_code(), keyValue.substring(0, keyValue.length()-1),DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
						}
					}
				}
			}
		}catch(Exception e) {         
	        System.out.println("Got a Exception：" + e.getMessage());
	        e.printStackTrace();
	    }
	}

	/**
	 * 所有请求处理完成之后执行
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		SystemSession.clear();
		SpObserver.clear();
	}

	private Employee CreateDemoEmployee(String master) {
		Employee em = new Employee();
		em.setEm_master(master);
		em.setEm_type("admin");
		em.setEm_code(System.currentTimeMillis() + "_USER");
		em.setEm_class("virtual");
		em.setEm_id(1);
		return em;
	}

	private Employee CreateSessionEmployee(String sessionUser) {
		return employeeService.getEmployeeByName(sessionUser);
	}
	
	
}
