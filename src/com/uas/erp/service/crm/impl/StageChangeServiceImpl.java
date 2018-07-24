package com.uas.erp.service.crm.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.ChanceService;
import com.uas.erp.service.crm.StageChangeService;

@Service
public class StageChangeServiceImpl implements StageChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private ChanceService chanceService;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void audit(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("StageChange",
				"sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sc_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"StageChange",
				"sc_statuscode='AUDITED',sc_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',sc_auditname='"
						+ SystemSession.getUser().getEm_name()
						+ "',sc_auditdate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
								new Date()), "sc_id=" + sc_id);
		// 修改chance表中的进度
		Object[] data = baseDao.getFieldsDataByCondition("StageChange",
				"sc_newstage,sc_chcode", "sc_id=" + sc_id);
		baseDao.updateByCondition("Chance", "ch_stage='" + data[0] + "'",
				"ch_code= '" + data[1] + "'");
		Object ch_id = baseDao.getFieldDataByCondition("Chance", "ch_id",
				"ch_code= '" + data[1] + "'");
		chanceService.haveAllChancestatus(Integer.parseInt(ch_id.toString()),
				caller);
		// 记录操作
		baseDao.logger.audit(caller, "sc_id", sc_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sc_id);
	}

	@Override
	public void resAudit(int sc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("StageChange",
				"sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, sc_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				"StageChange",
				"sc_statuscode='COMMITED',sc_status='"
						+ BaseUtil.getLocalMessage("COMMITED")
						+ "',sc_auditname='',sc_auditdate=''", "sc_id=" + sc_id);
		// 修改chance表中的进度
		Object[] data = baseDao.getFieldsDataByCondition("StageChange",
				"sc_stage,sc_chcode", "sc_id=" + sc_id);
		baseDao.updateByCondition("Chance", "ch_stage='" + data[0] + "'",
				"ch_code= '" + data[1] + "'");
		Object ch_id = baseDao.getFieldDataByCondition("Chance", "ch_id",
				"ch_code= '" + data[1] + "'");
		chanceService.haveAllChancestatus(Integer.parseInt(ch_id.toString()),
				caller);
		// 记录操作
		// 记录操作
		baseDao.logger.resAudit(caller, "sc_id", sc_id);
		handlerService.afterResAudit(caller, sc_id);
	}

}
