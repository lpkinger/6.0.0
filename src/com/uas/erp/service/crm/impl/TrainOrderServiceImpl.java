package com.uas.erp.service.crm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.crm.TrainOrderService;

@Service
public class TrainOrderServiceImpl implements TrainOrderService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	@Transactional
	public void auditTrainOrder(int to_id, String language, Employee employee,
			String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition(caller,
				"to_statuscode", "to_id=" + to_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.audit_onlyCommited", language));
		}
		// 执行审核前的其它逻辑
		handlerService.handler("TrainOrder", "audit", "before", new Object[] {
				to_id, language });
		// 执行审核操作
		baseDao.updateByCondition(
				"TrainOrder",
				"to_statuscode='AUDITED',to_status='"
						+ BaseUtil.getLocalMessage("AUDITED", language) + "',to_auditer='"+employee.getEm_name()+"',to_auditdate=sysdate",
				"to_id=" + to_id);
		//Object code=baseDao.getFieldDataByCondition("TrainOrder", "to_code", "to_id="+to_id);
		Object[] formData=baseDao.getFieldsDataByCondition("TrainOrder", new String[]{"to_code","to_tpcode"}, "to_id="+to_id);
		List<String> sqls=new ArrayList<String>();
		//给每个成员发信息,任务,同时直接插入一条记录到考核报告中
		List<Object[]> detailData=baseDao.getFieldsDatasByCondition("TrainOrderdet left join employee on em_code=TD_EMCODE",
				new String[]{"em_id","em_code","em_name"}, "td_toid="+to_id);
		for(Object[] os:detailData){
			int taskId = baseDao.getSeqId("PROJECTTASK_SEQ");
			int reportId=baseDao.getSeqId("TrainReport_seq");
			int ra_id=baseDao.getSeqId("resourceassignment_seq");
			String reportCode=baseDao.sGetMaxNumber(formData[1].toString(), 2);
			String taskCode=baseDao.sGetMaxNumber("ProjectTask", 2);
			String url="jsps/crm/marketmgr/resourcemgr/trainReport.jsp?whoami="+formData[1]+"&cond=to_idIS"+to_id+"&formCondition=tr_idIS"+reportId;
			//生成一条考核报告记录
			String insertReport="insert into TrainReport(tr_id,tr_code,tr_status,tr_statuscode,tr_templatecode,tr_tocode,tr_taskid,tr_recorder) values" +
					"("+reportId+",'"+reportCode+"','在录入','ENTERING','"+formData[1]+"','"+formData[0]+"',"+taskId+",'"+os[2]+"')";
			//挂在首页
			String insertTask="insert into ProjectTask(id,name,tasktype,handstatus,handstatuscode,status,statuscode,recorddate,class," +
					"recorder,resourcecode,resourcename,resourceemid,taskcode,sourcecode,sourcelink)"+
			"values ("+taskId+",'产品培训考核报告','normal','已启动','DOING','已审核','AUDITED',"+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'billtask'," +
					"'"+employee.getEm_name()+"','"+os[1]+"','"+os[2]+"',"+os[0]+",'"+taskCode+"','"+formData[0]+"','"+url+"')";
			String insertTaskDetail="insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,ra_status," +
					"ra_statuscode,ra_units,ra_type,ra_taskname) values " +
					"("+ra_id+","+taskId+","+os[0]+",'"+os[1]+"','"+os[2]+"',1,'进行中','START',100,'billtask','产品培训考核报告')";
			sqls.add(insertReport);
			sqls.add(insertTask);
			sqls.add(insertTaskDetail);
		}
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.audit", language), BaseUtil
				.getLocalMessage("msg.auditSuccess", language),
				"TrainOrder|to_id=" + to_id));
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { to_id,
				language });

	}

	@Override
	public void resAuditTrainOrder(int to_id, String language,
			Employee employee, String caller) {
		// 执行反审核前的其它逻辑
		handlerService.handler(caller, "resAudit", "before", new Object[] {
				to_id, language, employee });
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("TrainOrder",
				"to_statuscode", "to_id=" + to_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.resAudit_onlyAudit", language));
		}
		// 执行反审核操作
		baseDao.updateByCondition(
				"TrainOrder",
				"to_statuscode='ENTERING',to_status='"
						+ BaseUtil.getLocalMessage("ENTERING", language) + "'",
				"to_id=" + to_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.resAudit", language), BaseUtil
				.getLocalMessage("msg.resAuditSuccess", language),
				"TrainOrder|to_id=" + to_id));
		// 执行反审核后的其它逻辑
		handlerService.handler(caller, "resAudit", "after", new Object[] {
				to_id, language, employee });
	}

}
