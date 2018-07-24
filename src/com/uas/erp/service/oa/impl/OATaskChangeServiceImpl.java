package com.uas.erp.service.oa.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.OATaskChangeService;

@Service
public class OATaskChangeServiceImpl implements OATaskChangeService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void auditOATaskChange(int ptc_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("ProjectTaskChange", "ptc_statuscode", "ptc_id=" + ptc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] { ptc_id});
		Object[] data = baseDao.getFieldsDataByCondition("ProjectTaskChange", new String[] { "ptc_name",
				"ptc_startdate", "ptc_enddate", "ptc_taskman",
				"ptc_tasklevel", "ptc_standtime", "ptc_description", "ptc_oldtaskid" }, "ptc_id=" + ptc_id);
		// 将更变的内容反应到ProjectTask的记录上
		StringBuffer sb = new StringBuffer();
		sb.append("update ProjectTask set name='" + data[0] + "',startdate="
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, data[1] + "") + ",");
		sb.append("enddate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, data[2] + "") + ",taskman='"
				+ data[3] + "',tasklevel='" + data[4] + "',");
		sb.append("standtime=" + data[5] + ",description='" + data[6] + "' where id=" + data[7]);
		// 执行审核操作
		baseDao.audit("ProjectTaskChange", "ptc_id=" + ptc_id, "ptc_status", "ptc_statuscode");
		baseDao.execute(sb.toString());
		// 记录操作
		baseDao.logger.audit(caller, "ptc_id", ptc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { ptc_id});
	}

	@Override
	public void resAuditOATaskChange(int ptc_id, String  caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectTaskChange", "ptc_statuscode", "ptc_id=" + ptc_id);
		StateAssert.resAuditOnlyAudit(status);
		Object[] data = baseDao.getFieldsDataByCondition("ProjectTaskChange", new String[] { "ptc_oldtaskname",
				"ptc_oldstartdate", "ptc_oldenddate", "ptc_oldtaskman",
				"ptc_oldtasklevel", "ptc_oldstandtime", "ptc_olddescription", "ptc_oldtaskid" }, "ptc_id=" + ptc_id);
		// 将更变的内容反应到ProjectTask的记录上
		StringBuffer sb = new StringBuffer();
		sb.append("update ProjectTask set name='" + data[0] + "',startdate="
				+ DateUtil.parseDateToOracleString(Constant.YMD_HMS, data[1] + "") + ",");
		sb.append("enddate=" + DateUtil.parseDateToOracleString(Constant.YMD_HMS, data[2] + "") + ",taskman='"
				+ data[3] + "',tasklevel='" + data[4] + "',");
		sb.append("standtime=" + data[5] + ",description='" + data[6] + "' where id=" + data[7]);
		// 执行反审核操作
		baseDao.resOperate("ProjectTaskChange", "ptc_id=" + ptc_id, "ptc_status", "ptc_statuscode");
		baseDao.execute(sb.toString());
		// 记录操作
		baseDao.logger.resAudit(caller, "ptc_id", ptc_id);
	}

}
