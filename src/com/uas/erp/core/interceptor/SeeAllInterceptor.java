package com.uas.erp.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.common.PowerDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.EmpsJobs;
import com.uas.erp.model.PersonalPower;
import com.uas.erp.model.PositionPower;

/**
 * 自定义拦截器 查看用户是否有查看列表的权限
 * 
 * @author yingp
 */
public class SeeAllInterceptor extends HandlerInterceptorAdapter {

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
			// 先看是否有查看所有的权限
			boolean bool = checkJobPower(caller, PositionPower.ALL_LIST, employee);// 岗位权限表--all
			if (!bool) {
				//是否可以查看岗位下属数据
				boolean checkDefaultHrJobPowerExists = powerDao.checkDefaultHrJobPowerExists();
				if(checkDefaultHrJobPowerExists){
					req.setAttribute("_jobemployee", true);
				}else{
					bool = checkJobEmployeePower(caller,PersonalPower.JOBEMPLOYEE_LIST,employee);
					if(bool){
						req.setAttribute("_jobemployee", true);
					}else{
						bool = powerDao.getSelfPowerByType(caller, PersonalPower.ALL_LIST, employee);// 个人权限表--all
						if (!bool) {
							// 是否可以查看录入人为自己的数据
							bool = checkJobPower(caller, PositionPower.SELF_LIST, employee);// 岗位权限表--self
							if (!bool) {
								bool = powerDao.getSelfPowerByType(caller, PersonalPower.SELF_LIST, employee);// 个人权限表--self
								if (!bool) {
									BaseUtil.showError("ERR_POWER_025:您没有执行查看列表的权限!");
								} else {
									req.setAttribute("_self", true);
								}
							} else {
								req.setAttribute("_self", true);
							}
						}					
					}					
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
	
	//检查是否有"浏览岗位下属"权限
	private boolean checkJobEmployeePower(String caller, String powerType, Employee employee) {
		String sob = employee.getEm_master();
		Integer jobId = employee.getEm_defaulthsid();
		boolean jobEmployeeExists = powerDao.checkJobEmployeeExists(jobId);
		boolean bool = false;
		if(!jobEmployeeExists){
			for (EmpsJobs empsJob : employee.getEmpsJobs()) {
				jobEmployeeExists = powerDao.checkJobEmployeeExists(empsJob.getJob_id());
				if (jobEmployeeExists)
					break;
			}			
		}
		if(!jobEmployeeExists){
			return false;
		}else{
			// 默认岗位设置
			bool = powerDao.getJobEmployeePowerByType(caller,powerType,sob,employee.getEm_defaulthsid());
			if (!bool && employee.getEmpsJobs() != null) {
				// 按员工岗位关系取查找权限
				for (EmpsJobs empsJob : employee.getEmpsJobs()) {
					bool = powerDao.getJobEmployeePowerByType(caller,powerType,sob,empsJob.getJob_id());
					if (bool)
						break;
				}
			}			
			if(!bool){
				//检查个人权限
				bool = powerDao.getJobEmployeePowerByType(caller, PersonalPower.JOBEMPLOYEE_LIST, employee.getEm_master(), employee);
			}			
		}
		return bool;
	}
}
