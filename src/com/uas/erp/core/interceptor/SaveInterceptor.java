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
 * 自定义拦截器 查看用户是否有修改的权限
 * 
 * @author yingp
 */
public class SaveInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private PowerDao powerDao;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		String caller = req.getParameter("caller");
		Employee employee = (Employee) req.getSession().getAttribute("employee");
		if (caller != null && employee != null && !InterceptorUtil.isOpenSys(req)) {
			//基础资料维护设置账套权限
			/*修改为不对更新单据进行限制
			 * for(String powerCallers:powerDao.getUnEditableCallers(SpObserver.getSp())){		
				for(String powerCaller:powerCallers.split(",")){						
					boolean bool = caller.equals(powerCaller);
					if(bool){
						BaseUtil.showError("ERR_POWER_301:当前账套没有<修改>该单据的权限!");
					}
				}
			}*/
			// 不限制权限
			if (InterceptorUtil.noControl(req))
				return true;
			if (!"admin".equals(employee.getEm_type())) {
				boolean bool = checkJobPower(caller, PositionPower.SAVE, employee);
				if (!bool) {
					bool = powerDao.getSelfPowerByType(caller, PersonalPower.SAVE, employee);
					if (!bool) {
						BaseUtil.showError("ERR_POWER_023:您没有<修改>该单据的权限!");
					} else {
						String formStore = req.getParameter("formStore");
						if (formStore != null) {
							bool = powerDao.getSelfOtherPowerByType(caller, formStore, PositionPower.SAVE_OTHER, employee);
							if (!bool) {
								BaseUtil.showError("ERR_POWER_024:您没有<修改他人>单据的权限!");
							} else {
								return true;
							}
						}
						return true;
					}
				} else {
					String formStore = req.getParameter("formStore");
					if (formStore != null) {
						bool = powerDao.getOtherPowerByType(caller, formStore, PositionPower.SAVE_OTHER, employee);
						if (!bool) {
							BaseUtil.showError("ERR_POWER_024:您没有<修改他人>单据的权限!");
						} else {
							return true;
						}
					}
					return true;
				}
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
}
