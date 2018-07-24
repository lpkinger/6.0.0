package com.uas.erp.core.interceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;

/**
 * 自定义拦截器 查看用户是否有提交的权限
 * 
 * @author yingp
 */
public class SubmitInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private PowerDao powerDao;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		String caller = req.getParameter("caller");
		Employee employee = (Employee) req.getSession().getAttribute("employee");
		if (caller != null && employee != null && !"admin".equals(employee.getEm_type()) && !InterceptorUtil.isOpenSys(req)) {
			// 不限制权限
			if (InterceptorUtil.noControl(req))
				return true;
			boolean bool = checkJobPower(caller, PositionPower.SUBMIT, employee);
			if (!bool) {
				bool = powerDao.getSelfPowerByType(caller, PersonalPower.SUBMIT, employee);// 个人权限
				if (!bool) {
					BaseUtil.showError("ERR_POWER_028:您没有<提交>该单据的权限!");
				} else {
					Integer id = Integer.parseInt(req.getParameter("id"));
					if (id != null) {
						bool = powerDao.getOtherSelfPowerByType(caller, id, PersonalPower.SUBMIT_OTHER, employee);
						if (!bool) {
							BaseUtil.showError("ERR_POWER_029:您没有<提交他人>单据的权限!");
						} else {
							return true;
						}
					}
				}
			} else {
				Integer id = Integer.parseInt(req.getParameter("id"));
				if (id != null) {
					bool = powerDao.getOtherPowerByType(caller, id, PositionPower.SUBMIT_OTHER, employee);
					if (!bool) {
						bool = powerDao.getOtherSelfPowerByType(caller, id, PersonalPower.SUBMIT_OTHER, employee);
						if (!bool) {
							BaseUtil.showError("ERR_POWER_029:您没有<提交他人>单据的权限!");
						} else {
							return true;
						}
					} else {
						return true;
					}
				}
				return true;
			}
		}
		return true;
	}

	private boolean checkJobPower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, powerType, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, powerType, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
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
}
