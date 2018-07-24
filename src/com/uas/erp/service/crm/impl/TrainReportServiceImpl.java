package com.uas.erp.service.crm.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.crm.TrainReportService;

@Service
public class TrainReportServiceImpl implements TrainReportService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void updateTrainReportById(String formStore, String language,
			Employee employee, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("TrainReport",
				"tr_statuscode", "tr_id=" + store.get("tr_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.update_onlyEntering", language));
		}
		if(store.get("tr_recorddate")==null||"".equals(store.get("tr_recorddate").toString().trim())){
			store.put("tr_recorddate", DateUtil.parseDateToString(new Date(), null));
		}
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store,
				language });
		// 修改TrainReport
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "TrainReport",
				"tr_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.update", language), BaseUtil
				.getLocalMessage("msg.updateSuccess", language),
				"TrainReport|tr_id=" + store.get("tr_id")));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store,
				language });

	}

	@Override
	public void submitTrainReport(int tr_id, String language,
			Employee employee, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("TrainReport",
				"tr_statuscode", "tr_id=" + tr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.submit_onlyEntering", language));
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] {
				tr_id, language, employee });
		// 执行提交操作
		baseDao.updateByCondition(
				"TrainReport",
				"tr_statuscode='COMMITED',tr_status='"
						+ BaseUtil.getLocalMessage("COMMITED", language) + "'",
				"tr_id=" + tr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.submit", language), BaseUtil
				.getLocalMessage("msg.submitSuccess", language),
				"TrainReport|tr_id=" + tr_id));
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { tr_id,
				language, employee });

	}

	@Override
	public void resSubmitTrainReport(int tr_id, String language,
			Employee employee, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("TrainReport",
				"tr_statuscode", "tr_id=" + tr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resSubmit_onlyCommited", language));
		}
		handlerService.handler(caller, "resCommit", "before", new Object[] {
				tr_id, language, employee });
		// 执行反提交操作
		baseDao.updateByCondition(
				"TrainReport",
				"tr_statuscode='ENTERING',tr_status='"
						+ BaseUtil.getLocalMessage("ENTERING", language) + "'",
				"tr_id=" + tr_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.resSubmit", language), BaseUtil
				.getLocalMessage("msg.resSubmitSuccess", language),
				"TrainReport|tr_id=" + tr_id));
		handlerService.handler(caller, "resCommit", "after", new Object[] {
				tr_id, language, employee });

	}

	@Override
	public void auditTrainReport(int tr_id, String language, Employee employee,
			String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("TrainReport",
				"tr_statuscode", "tr_id=" + tr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited", language));
		}
		// 执行审核前的其它逻辑
		handlerService.handler("TrainReport", "audit", "before", new Object[] {
				tr_id, language });
		// 执行审核操作
		baseDao.updateByCondition(
				"TrainReport",
				"tr_statuscode='AUDITED',tr_status='"
						+ BaseUtil.getLocalMessage("AUDITED", language) + "'",
				"tr_id=" + tr_id);
		//任务已完成
		Object taskId=baseDao.getFieldDataByCondition("TrainReport", "tr_taskid", "tr_id="+tr_id);
		baseDao.execute("update resourceassignment set ra_taskpercentdone=100,ra_status='已完成',ra_statuscode='FINISHED',ra_enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where ra_taskid="
				+ taskId);
		baseDao.execute("update ProjectTask set handstatus='已完成',handstatuscode='FINISHED',percentdone=100 where id="+taskId);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.audit", language), BaseUtil
				.getLocalMessage("msg.auditSuccess", language),
				"TrainReport|tr_id=" + tr_id));
		// 执行审核后的其它逻辑
		handlerService.handler("TrainReport", "audit", "after", new Object[] {
				tr_id, language });

	}

	@Override
	public void resAuditTrainReport(int tr_id, String language,
			Employee employee, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.handler("TrainReport", "resAudit", "before",
				new Object[] { tr_id, language, employee });
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("TrainReport",
				"tr_statuscode", "tr_id=" + tr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit", language));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"TrainReport",
				"tr_statuscode='ENTERING',tr_status='"
						+ BaseUtil.getLocalMessage("ENTERING", language) + "'",
				"tr_id=" + tr_id);
		Object taskId=baseDao.getFieldDataByCondition("TrainReport", "tr_taskid", "tr_id="+tr_id);
		baseDao.execute("update resourceassignment set ra_taskpercentdone=0,ra_status='进行中',ra_statuscode='START',ra_enddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where ra_taskid="
				+ taskId);
		baseDao.execute("update ProjectTask set handstatus='已启动',handstatuscode='DOING',percentdone=0 where id="+taskId);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.resAudit", language), BaseUtil
				.getLocalMessage("msg.resAuditSuccess", language),
				"TrainReport|tr_id=" + tr_id));
		// 执行反审核后的其它逻辑
		handlerService.handler("TrainReport", "resAudit", "after",
				new Object[] { tr_id, language, employee });

	}

}
