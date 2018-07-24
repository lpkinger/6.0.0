package com.uas.erp.service.common;

import java.util.List;

import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.sso.entity.UserView;

public interface AccountCenterService {

	/**
	 * 个人信息同步到账户中心
	 * 
	 * @param employee
	 * @param master
	 * @return
	 * @throws Exception
	 */
	UserView sync(Employee employee, Master master) throws Exception;

	/**
	 * 删除个人云账号
	 * 
	 * @param employee
	 * @param master
	 * @return
	 * @throws Exception
	 */
	void unbind(Employee employee, Master master) throws Exception;

	/**
	 * 修改密码
	 * 
	 * @param employee
	 * @param master
	 * @param newPassword
	 * @throws Exception
	 */
	void resetPassword(Employee employee, Master master, String newPassword) throws Exception;

	/**
	 * 校验密码
	 * 
	 * @param employee
	 * @param master
	 * @param password
	 * @throws Exception
	 */
	boolean checkPassword(Employee employee, Master master, String password) throws Exception;

	/**
	 * 校验密码
	 * 
	 * <pre>
	 * 全匹配模式，不论任何应用和企业
	 * </pre>
	 * 
	 * @param employee
	 * @param master
	 * @param password
	 * @throws Exception
	 */
	boolean fuzzyCheckPassword(Employee employee, Master master, String password) throws Exception;

	/**
	 * 通过账号中心的token获取到对应的用户信息
	 * @param token
	 * @return
	 */
	UserView getUserByToken(String token) throws Exception;
}
