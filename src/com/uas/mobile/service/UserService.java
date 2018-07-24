package com.uas.mobile.service;

import java.util.List;
import java.util.Map;



import com.uas.mobile.model.CardLog;
import com.uas.mobile.model.Enterprise;
import com.uas.mobile.model.WorkDate;



/**
 * 客户端用户管理及支持服务
 * @author suntg
 * @date 2014-10-30 17:17:08
 *
 */
public interface UserService {

	/**
	 * 获取所有使用移动客户端登陆过的用户
	 * @return 使用移动客户端登陆过的用户
	 */
	public List<Map<String, Object> > getLoginedEmployees();
	//根据员工号 获取班次信息
	public  WorkDate getWorkDates(String en_code);
	//保存签到信息
	public void saveCardLogs(CardLog  cardLog);
	//查询签到信息
	public List<CardLog> selectCardLogsForEnCode(String en_code,int startid,int endid);
	//获取定位信息
	public Enterprise getEnterprise(Integer en_id);
	
	
	
	
	
}
