package com.uas.erp.service.pm.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.MODYSReportDao;
import com.uas.erp.service.pm.MODYSReportService;

@Service("MODYSReportService")
public class MODYSReportServiceImpl implements MODYSReportService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private MODYSReportDao MODYSReportDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void auditYSReport(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("MOD_YSREPORT", "mo_statuscode", "mo_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { id });
		// 执行审核操作
		baseDao.audit("MOD_YSREPORT", "mo_id = " + id, "mo_status", "mo_statuscode", "mo_auditdate", "mo_auditman");
		baseDao.updateByCondition("MOD_YSBGDETAIL", "yd_ysdate=sysdate", "yd_moid=" + id);
		baseDao.logger.audit(caller, "mo_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { id });
		if(baseDao.isDBSetting(caller, "MouldAudit")){
			postYSReport(caller,id);
		}
	}

	@Override
	public void postYSReport(String caller, int mo_id) {
		Object status = baseDao.getFieldDataByCondition("MOD_YSReport", "mo_statuscode", "mo_id=" + mo_id);
		if (status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.post_onlyUnPost"));
		}
		// 过账前的其它逻辑
		handlerService.beforePost(caller, new Object[] { mo_id });
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITYSREPORT",
				new Object[] { mo_id, String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("MOD_YSReport", "mo_postman='" + SystemSession.getUser().getEm_name() + "',mo_postdate=sysdate", "mo_id="
				+ mo_id);
		baseDao.updateByCondition("MOD_YSBGDETAIL", "yd_ysdate=sysdate", "yd_moid=" + mo_id);
		// 记录操作
		baseDao.logger.post(caller, "mo_id", mo_id);
		// 执行过账后的其它逻辑
		handlerService.afterPost(caller, new Object[] { mo_id });
	}

	@Override
	public void resPostYSReport(String caller, int mo_id) {
		Object status = baseDao.getFieldDataByCondition("MOD_YSReport", "mo_statuscode", "mo_id=" + mo_id);
		if (!status.equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resPost_onlyPost"));
		}
		// 过账前的其它逻辑
		handlerService.beforeResPost(caller, new Object[] { mo_id });
		// 执行过账操作
		// 存储过程
		String res = baseDao.callProcedure("SP_UNCOMMITYSREPORT",
				new Object[] { mo_id, String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		// 反过账清除过账人过账日期，审核人，审核日期
		baseDao.execute("update MOD_YSReport set mo_statuscode='AUDITED', mo_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',mo_postman=null,mo_postdate=null,mo_auditman=null,mo_auditdate=null WHERE MO_ID=" + mo_id);
		// 记录操作
		baseDao.logger.resPost(caller, "mo_id", mo_id);
		// 执行过账后的其它逻辑
		handlerService.afterResPost(caller, new Object[] { mo_id });
	}

	@Override
	public int turnMJProject(int mo_id, String caller) {
		int ws_id = 0;
		// 已经
		Object code = baseDao.getFieldDataByCondition("MOD_YSREPORT", "mo_code", "mo_id=" + mo_id);
		code = baseDao.getFieldDataByCondition("MOD_MJPROTECT", "ws_code", "ws_sourcecode='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError("该模具验收报告书已转入过模具委托保管书,托保管书编号[" + code + "]");
		} else {
			// 转模具模具委托保管书
			ws_id = MODYSReportDao.turnMJProject(mo_id);
			baseDao.updateByCondition("MOD_YSREPORT", "mo_turnstatuscode='TURNMJP',mo_turnstatus='" + BaseUtil.getLocalMessage("TURNMJP")
					+ "'", "mo_id=" + mo_id);
			baseDao.logger.turn("转模具委托保管书", caller, "mo_id", mo_id);
		}
		return ws_id;
	}

	@Override
	public void updatestf(int ws_id, String vend) {
		baseDao.execute("update MOD_MJPROTECT set ws_stf='" + vend + "' where ws_id=" + ws_id);
		baseDao.logger.others("移模操作", "msg.updateSuccess", "MJProject!Mould", "ws_id", ws_id);
	}

	@Override
	public void resAuditYSReport(int id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MOD_YSREPORT", "mo_statuscode", "mo_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 判断该收料通知单是否已经转入过采购验收单
		Object code = baseDao.getFieldDataByCondition("MOD_YSREPORT", "mo_code", "mo_id=" + id);
		code = baseDao.getFieldDataByCondition("MOD_MJPROTECT", "ws_code", "ws_sourcecode='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError("该模具验收报告书已转入过模具委托保管书,不允许反审核！托保管书编号[" + code + "]");
		}
		handlerService.beforeResAudit(caller, new Object[] { id });
		// 执行反审核操作
		baseDao.resAudit("MOD_YSREPORT", "mo_id=" + id, "mo_status", "mo_statuscode", "mo_auditman", "mo_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "mo_id", id);
		handlerService.afterResAudit(caller, new Object[] { id });
	}

	@Override
	public void deleteYSReport(int mo_id, String caller) {
		// 只能删除在录入的单据!
		Object[] status = baseDao.getFieldsDataByCondition("MOD_YSREPORT", new String[] { "mo_statuscode", "mo_code" }, "mo_id=" + mo_id);
		StateAssert.delOnlyEntering(status[0]);
		// 判断是否已经
		String dets = baseDao.getJdbcTemplate().queryForObject("select str_concat(ws_code) from MOD_MJPROTECT where ws_sourcecode=?",
				String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("该模具验收报告书已转入过模具委托保管书[" + dets + "],不允许反审核！");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { mo_id });
		// 删除MOD_YSREPORT
		baseDao.deleteById("MOD_YSREPORT", "mo_id", mo_id);
		// 删除MOD_YSBGDETAIL
		baseDao.deleteById("MOD_YSBGDETAIL", "yd_moid", mo_id);
		// 记录操作
		baseDao.logger.delete(caller, "mo_id", mo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { mo_id });
	}
}
