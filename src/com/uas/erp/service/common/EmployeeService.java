package com.uas.erp.service.common;

import java.util.Date;
import java.util.List;

import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;

public interface EmployeeService {

	void setDeskTopRemind(Employee employee, int remind);
	
	/**
	 * 生成用户
	 * 
	 * @return
	 */
	Employee generate(String sob);

	/**
	 * @param sob
	 * @param username
	 * @param password
	 * @param ip
	 * @param isMobile
	 *            是否移动客户端登录
	 * @param string
	 * @return
	 */
	String loginWithEm(String sob, String username, String password, String ip, boolean isMobile, String string);
	
	String loginWithEmQrcode(String sob, String username,String sid,String ip, boolean isMobile, String string);
	

	Employee getEmployeeByName(String username);
	
	void updateChangeStatues(int id);

	Employee getEmployeeById(long id);

	Employee getEmployeeByUu(long uu);

	Employee getEmployeeById(int id, String sob);

	Employee getByCondition(String condition, String caller);

	List<Employee> getEmployeesByCondition(String condition);

	Master getMaster(Employee employee);

	List<Employee> getEmployeesByOrId(int or_id);

	List<Employee> getHrorgEmployeesByEmcode(String emcode);

	String updatePwd(String caller, String em_oldpassword, String em_newpassword, String emid, String emcode, String empassword, String synchronize);

	/**
	 * 修改移动客户端的最新登录时间
	 * 
	 * @param employee
	 * @param remind
	 */
	void setMsgRemaind(Employee employee, int remind);

	void setMoLastLoginTime(Employee employee, Date date);

	void updateEmployeeList(String formStore, String caller);

	String checkWithMac(String smac, String username, String ip);

	void logWithEm(String sip, String username);

	List<Employee> getEmployees();

	void saveEmployees(String jsonData);

	void updateEmployees(String jsonData);

	void deleteEmployees(String jsonData);

	Employee getEmployeeByEmTel(String username);

	Employee getEmployeeByEmcode(String emcode);

	boolean checkAppToken(String sessionId, String newSessionId, String emcode, int type);

	/**
	 * 同步到账户中心
	 * 
	 * @param employee
	 * @return 错误
	 */
	String postToAccountCenter(Employee employee);

	/**
	 * 同步到账户中心
	 * 
	 * @param employee
	 * @param master
	 * @return 错误
	 */
	String postToAccountCenter(Employee employee, Master master);

	/**
	 * 删除云账户
	 * 
	 * @param employee
	 * @return 错误
	 */
	String deleteFromAccountCenter(Employee employee);
	
	/**
	 * 通过条件获取解密的密码
	 * @param condition
	 * @return
	 */
	String getPassword(String condition);
	
	/**
	 * 通过条件获取判断密码是否是默认密码111111
	 * 是则返回true
	 * @param condition
	 * @return
	 */
	boolean checkInitpwd(String condition);
	/**
	 * 通过账号中心token登录
	 * @param sob 账套
	 * @param token 
	 * @param ip 
	 * @param isMobile
	 * @param webSite
	 * @return
	 */
	String loginWithToken(String sob, String token, String ip, boolean isMobile, String webSite);
}
