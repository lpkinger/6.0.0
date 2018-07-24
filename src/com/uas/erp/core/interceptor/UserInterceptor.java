package com.uas.erp.core.interceptor;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.core.support.MobileSessionContext;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;

/**
 * 自定义拦截器 每次请求之前判断session里面的user是否存在
 * 
 * @author yingp
 */
public class UserInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private PowerDao powerDao;

	/**
	 * 处理前台请求之前执行
	 */
	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		Object obj = req.getSession().getAttribute("employee");
		if (obj == null) {
			if (MobileSessionContext.getInstance().getSessionById(req.getParameter("sessionId")) != null) {
				req.getSession().setAttribute("employee",
						MobileSessionContext.getInstance().getSessionById(req.getParameter("sessionId")).getAttribute("employee"));
				return true;
			} else {
				BaseUtil.showError("ERR_NETWORK_SESSIONOUT");
				return false;
			}
		} else {
			Employee em = (Employee) obj;
			if (InterceptorUtil.checkVirtual(req, em))
				return true;
			String sid = req.getSession().getId();
			int status = UserOnlineListener.isLocked(sid);
			Boolean isKicked = UserOnlineListener.isKicked(sid);
			if (status == 1) {
				req.getSession().invalidate();
				BaseUtil.showError("ERR_NETWORK_LOCKED");
				return false;
			} 
/*			else if (isKicked) {
				req.getSession().invalidate();
				BaseUtil.showError("ERR_NETWORK_KICKED");
				return false;
			} */
			else if (status == -1) {
				UserOnlineListener.addUser((Employee) obj, sid);
			} else {
				// 刷新最近访问时间
				UserOnlineListener.refresh(sid);
			}
			// 特殊权限控制
			if (!"admin".equals(em.getEm_type())) {
				boolean bool = validSpecial(req, req.getParameter("caller"), req.getRequestURI(), em);
				if (!bool) {
					BaseUtil.showError("ERR_POWER_100:您没有执行该特殊操作的权限!");
				}
			}
			return true;
		}
	}

	/**
	 * 处理完前台请求之后执行
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("employee") != null) {
			String err = SystemSession.getErrors();// 查看有没有记录的错误信息
			if (err != null) {
				ServletOutputStream out = response.getOutputStream();
				out.write(("<error>" + err + "</error>").getBytes("utf-8"));
				out.flush();
				out.close();
				SystemSession.clearErrors();
			}
		}
	}

	/**
	 * 所有请求处理完成之后执行
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}

	private boolean validSpecial(HttpServletRequest req, String caller, String action, Employee employee) {
		Map<String, Object[]> specials = powerDao.getSpecialActions(employee.getEm_master());
		if (specials != null) {
			caller = caller == null ? "" : caller;
			String key = caller + "@" + formatURI(action);
			if (specials.containsKey(key)) {
				Object[] objs = specials.get(key);
				if ("1".equals(String.valueOf(objs[1]))) {// 与业务相关
					req.setAttribute("_businessLimit", true);
				}
				String ssp_id = String.valueOf(objs[0]);
				boolean ok = checkJobPower(ssp_id, employee);
				if (!ok) {
					ok = powerDao.getSelfSpecialPowerByActionId(ssp_id, employee);
				}
				if (ok) {// 当例如singleFormItems.action作为特殊权限时
					req.setAttribute("_noc", 1);
				}
				return ok;
			}
		}
		return true;
	}
    
	private boolean checkJobPower(String ssp_id, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getSpecialPowerByActionId(ssp_id, employee.getEm_defaulthsid(), sob);
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getSpecialPowerByActionId(ssp_id, empsJob.getJob_id(), sob);
				if (bool)
					break;
			}
		}
		return bool;
	}
   
    private String formatURI(String action){
    	if(!action.startsWith("/ERP/")) {
    		action="/ERP/"+action.substring(action.indexOf("/", 2)+1);
    	}
    	return action;
    }
}
