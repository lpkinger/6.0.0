package com.uas.pda.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.uas.erp.core.exception.SystemException;
import com.uas.erp.core.listener.UserOnlineListener;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.model.UserSession;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.pda.service.PdaLoginService;

@Service("pdaLoginServiceImpl")
public class PdaLoginServiceImpl implements PdaLoginService {

	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public String login(String sob,String j_username, String j_password, String ip, boolean isMobile, HttpSession session) {
		if (sob != null)
			SpObserver.putSp(sob);
		Employee employee = employeeDao.getEmployeeByEmCode(j_username);
		if (employee == null) {
			return "员工账号不正确!";
		} else if (!j_password.equals(employee.getEm_password())) {
			return "密码不正确!";
		} else if (!isMobile && ip != null && employee.getEm_lastip() != null) {
			UserSession us = UserOnlineListener.isOnLine(employee.getEm_id(), SpObserver.getSp());
			if (us != null) {
				if (ip.equals(us.getIp())) {
					return "检测到您已登录本系统,请不要重复登录、打开空白页!";
				} else {
					return "账号: " + j_username + " 已于IP:" + us.getIp() + "登录,如非您本人操作，请及时联系管理员!";
				}
			}
		} else if (isMobile) {
			/*UserSession us = UserOnlineListener.getUserById(employee.getEm_id(), SpObserver.getSp());
			if (us != null) {
				if (us.getEm_pdamobilelogin() == 1)
					return "账号：" + j_username + " 已于移动端登录,如非您本人操作，请及时联系管理员!";
			}*/
		}
		if (isMobile) {
			baseDao.updateByCondition("Employee", "em_pdamobilelogin=1", "em_id=" + employee.getEm_id());
		}
		employee = employeeDao.getEmployeeByEmCode(j_username);
		employee.setEm_master(sob);
	    employee.setCurrentMaster(getMaster(sob));
		session.setAttribute("employee", employee);
		//UserOnlineListener.addUser(employee, session.getId());
		return null;
	}

	@Override
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Employee employee = (Employee) session.getAttribute("employee");
		/*UserSession u = UserOnlineListener.getUserBySId(session.getId());
		if (u != null) {
			// 移除在线用户
			UserOnlineListener.onLineUserList.remove(u);
		}*/
		// 移除session
		session.removeAttribute("employee");
		// 设置为getEm_pdamobilelogin0
		baseDao.updateByCondition("Employee", "em_pdamobilelogin=0", "em_id=" + employee.getEm_id());
		return null;
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
	
}
