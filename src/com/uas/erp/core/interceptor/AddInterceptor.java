package com.uas.erp.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;

/**
 * 自定义拦截器 查看用户是否有新增的权限
 * 
 * @author yingp
 */
public class AddInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private PowerDao powerDao;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		String caller = req.getParameter("caller");
		Employee employee = (Employee) req.getSession().getAttribute("employee");
		if (caller != null && employee != null && !InterceptorUtil.isOpenSys(req)) {
			//基础资料维护设置账套权限
			for(String powerCallers:powerDao.getUnEditableCallers(SpObserver.getSp())){		
				for(String powerCaller:powerCallers.split(",")){						
					boolean bool = caller.equals(powerCaller);
					if(bool){
						BaseUtil.showError("ERR_POWER_301:当前账套没有<新增>该单据的权限!");
					}
				}
			}
			// 不限制权限
			if (InterceptorUtil.noControl(req))
				return true;
			if (!"admin".equals(employee.getEm_type())) {
				boolean bool = checkJobPower(caller, employee);
				if (!bool) {
					// 查看是否有个人权限
					bool = powerDao.getSelfPowerByType(caller, PersonalPower.ADD, employee);
					if (!bool) {
						BaseUtil.showError("ERR_POWER_001:您没有<新增>单据的权限!");
					}
				} else {
					return true;
				}
			}
		}
		return true;
	}

	private boolean checkJobPower(String caller, Employee employee) {
		String sob = employee.getEm_master();
		// 默认岗位设置
		boolean bool = powerDao.getPowerByType(caller, PositionPower.ADD, sob, employee.getEm_defaulthsid());
		if (!bool && employee.getEmpsJobs() != null) {
			// 按员工岗位关系取查找权限
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				bool = powerDao.getPowerByType(caller, PositionPower.ADD, sob, empsJob.getJob_id());
				if (bool)
					break;
			}
		}
		return bool;
	}

}
