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
 * 自定义拦截器 查看用户是否有查看的权限
 * 
 * @author yingp
 */
public class SeeInterceptor extends HandlerInterceptorAdapter {

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
			//注：权限判断流程：判断岗位及个人是否有浏览权限，如果没有则限制，有则判断单据录入人是否是自己，如果不是则判断岗位或个人是否有浏览所有权限，没有则限制
			boolean bool = checkJobPower(caller, PositionPower.SEE, employee); //岗位浏览权限
			if(!bool){
				bool = powerDao.getSelfPowerByType(caller, PersonalPower.SEE, employee);// 个人浏览权限
				if (!bool) {
					BaseUtil.showError("ERR_POWER_026:您没有<查看>该单据的权限!");
				} 
			}
			
			Object condition = req.getParameter("condition");
			if (condition != null && !"".equals(condition)) {
				boolean checkDefaultHrJobPowerExists = powerDao.checkDefaultHrJobPowerExists();
				boolean isMyForm = false; 
				
				if(checkDefaultHrJobPowerExists){
					isMyForm = powerDao.isMyForm(caller, employee, (condition==null?"":condition.toString()), true); //判断单据录入人是否是自己或岗位下属
				}else{
					isMyForm = powerDao.isMyForm(caller, employee, condition.toString()); //判断单据录入人是否是自己
				}
				
				if(!isMyForm){
					bool = powerDao.getPowerByTypeIncludeExtraJob(caller, PositionPower.SEE_OTHER, employee); //查看岗位及兼职岗位是否有查看所有的权限
					if(!bool){
						bool = powerDao.getSelfPowerByType(caller, PersonalPower.SEE_OTHER, employee); //查看个人是否有查看所有的权限
						if(!bool){
							BaseUtil.showError("ERR_POWER_027:您没有<查看他人>单据的权限!");
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
}
