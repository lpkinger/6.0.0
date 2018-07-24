package com.uas.erp.service.hr.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.hr.AttendanceManageService;
@Service
public class AttendanceManageServiceImpl implements AttendanceManageService {
 @Autowired
 private BaseDao baseDao;
 @Autowired
 private HandlerService handlerService;
	@Override
	public void result(String startdate, String enddate,boolean toAttendanceConfirm) {
		// toAttendanceConfirm 更新配置 是否生成考勤确认单
		if(toAttendanceConfirm){
			baseDao.updateByCondition("configs", "data = '1'", "caller = 'AttendanceManage' and code ='attendanceConfirm'");
		}else{
			baseDao.updateByCondition("configs", "data = '0'", "caller = 'AttendanceManage' and code ='attendanceConfirm'");
		}
		String res = baseDao.callProcedure("YT_HR.kqupdatefx01", new Object[] {startdate,enddate});
		if (StringUtil.hasText(res)) {
			BaseUtil.showError(res);
		}
	}
	@Override
	public void AttendConfirm(String caller, int id) {
		handlerService.handler(caller, "confirm", "before", new Object[] { id });
		baseDao.updateByCondition("attendconfirm", "AC_CONFIRMSTATUSCODE ='CONFIRMED' , AC_CONFIRMSTATUS='已确认'", "ac_id = "+id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), BaseUtil.getLocalMessage("msg.onConfirm"), BaseUtil
					.getLocalMessage("msg.onConfirmSuccess"), caller + "|ac_id=" + id));
		handlerService.handler(caller, "confirm", "after", new Object[] { id });
	}
	@Override
	public void AttendResConfirm(String caller, int id) {
		handlerService.handler(caller, "resConfirm", "before", new Object[] { id });
		baseDao.updateByCondition("attendconfirm", "AC_CONFIRMSTATUSCODE ='UNCONFIRMED' , AC_CONFIRMSTATUS='未确认'", "ac_id = "+id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(), 
				"取消确认", 
				"取消确认成功", caller + "|ac_id=" + id));
		handlerService.handler(caller, "resConfirm", "after", new Object[] { id });		
	}

}
