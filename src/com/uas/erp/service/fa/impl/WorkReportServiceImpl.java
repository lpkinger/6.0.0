package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fa.WorkReportService;

@Service("WorkReportService")
public class WorkReportServiceImpl implements WorkReportService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveWorkReport(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore); // 从表grid数据
		String wr_code = store.get("wr_code").toString();
		//根据录入人和录入日期判断一个人一天只能录入一张工作日报单
		String wr_emcode=store.get("wr_emcode").toString();    
		String wr_recorddate=store.get("wr_recorddate").toString();
		String wc=(String) baseDao.getFieldDataByCondition("WorkReport","wr_code","wr_emcode='"+wr_emcode+"' and WR_RECORDDATE=to_date('"+wr_recorddate+"','yyyy-mm-dd')");
		if(wc!=null&&!"".equals(wc)){
			BaseUtil.showError("你今天已经录入了一张工作日报，该工作日报的编号为:"+wc);
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("WorkReport", "wr_code='"
				+ wr_code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存主表
		String formSql = SqlUtil.getInsertSqlByFormStore(store,
				"WorkReport", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存明细表
		for (Map<Object, Object> map : grid) {
			map.put("wrd_id", baseDao.getSeqId("WorkReportDetail_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"WorkReportDetail");
		baseDao.execute(gridSql);
		//更新工作日报的工作时数
		//baseDao.execute("update workreportdetail set WRD_WORKTIME=(select wrs_usetime from workreportset where wrs_code=wrd_wrscode)where wrd_wrscode is not null and wrd_wrid='"+store.get("wr_id").toString()+"'");
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteWorkReport(int wr_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("WorkReport",
				"wr_statuscode", "wr_id=" + wr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.delete_onlyEntering"));
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { wr_id });
		// delete WorkReport
		baseDao.deleteById("WorkReport", "wr_id", wr_id);
		// delete WorkReportDetail
		baseDao.deleteById("WorkReportDetail", "wrd_id", wr_id);

		// 记录操作
		baseDao.logger.delete(caller, "wr_id", wr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, wr_id);
	}

	@Override
	public void updateWorkReportById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("WorkReport",
				"wr_statuscode", "wr_id=" + store.get("wr_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改主表
		String formSql = SqlUtil.getUpdateSqlByFormStore(store,
				"WorkReport", "wr_id");
		baseDao.execute(formSql);
		// 修改明细表
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore,
				"WorkReportDetail", "wrd_id");
		for (Map<Object, Object> s : grid) {
			if (s.get("wrd_id") == null || s.get("wrd_id").equals("")
					|| s.get("wrd_id").equals("0")
					|| Integer.parseInt(s.get("wrd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("WorkReportDetail_SEQ");
				s.put("wrd_id", id);
				gridSql.add(SqlUtil.getInsertSqlByMap(s, "WorkReportDetail"));
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "wr_id", store.get("wr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void auditWorkReport(int wr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("WorkReport",
				"wr_statuscode", "wr_id=" + wr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, wr_id);
		// 执行审核操作
		baseDao.updateByCondition(
				caller,
				"wr_statuscode='AUDITED',wr_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "wr_id="
						+ wr_id);
		// 记录操作
		baseDao.logger.audit(caller, "wr_id", wr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wr_id);
	}

	@Override
	public void resAuditWorkReport(int wr_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WorkReport",
				"wr_statuscode", "wr_id=" + wr_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, wr_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				caller,
				"wr_statuscode='ENTERING',wr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "wr_id="
						+ wr_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "wr_id", wr_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, wr_id);
	}

	@Override
	public void submitWorkReport(int wr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkReport",
				"wr_statuscode", "wr_id=" + wr_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		//工作日报提交时判断明细行标准工作编码是否相同
		List<Object> list=baseDao.getFieldDatasByCondition("WorkReportDetail", "wrd_wrscode", "wrd_wrscode  is not null and wrd_wrid='"+wr_id+"' group by wrd_wrscode  having count(wrd_wrscode)>1");
		if(list.size()>0){
			BaseUtil.showError("明细行的标准工作内容编号不能相同！");
		}
		handlerService.beforeSubmit(caller, wr_id);
		String sob = BaseUtil.getXmlSetting("defaultSob");
		baseDao.callProcedure("SP_WORKREPORTCOUNT", new Object[] {wr_id,sob});
		// 执行提交操作
		baseDao.updateByCondition(
				caller,
				"wr_statuscode='COMMITED',wr_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "wr_id="
						+ wr_id);
		// 记录操作
		baseDao.logger.submit(caller, "wr_id", wr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, wr_id);
	}

	@Override
	public void resSubmitWorkReport(int wr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkReport",
				"wr_statuscode", "wr_id=" + wr_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, wr_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"WorkReport",
				"wr_statuscode='ENTERING',wr_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "wr_id="
						+ wr_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "wr_id", wr_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, wr_id);
	}
	
	@Override
	public List<Map<String, Object>> getJobWork(String code) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		//载入工作内容申请
		SqlRowList rs=baseDao.queryForRowSet("select ewd_worktype,ewd_jobduty,ewd_jobcontent,ewd_worktime,ewd_wrscode from EmpWorkDetail left join EmpWork on ewd_ewid=ew_id where ew_applicantcode='"+code+"'  order by ewd_wrscode,ewd_detno");
		String jobduty="", content="",type="",time="" ,wrscode="";
		while(rs.next()){
			Map<String, Object> map = new HashMap<String, Object>();
			if(!jobduty.equals(rs.getString("ewd_jobduty"))){
				if(!"".equals(jobduty)){
					map.put("wrd_worktype",type);
					map.put("wrd_jobduty",jobduty);
					map.put("wrd_jobcontent",content);
					map.put("wrd_worktime",time);
					map.put("wrd_wrscode",wrscode);
				}
				wrscode=rs.getString("ewd_wrscode")==null?"":rs.getString("ewd_wrscode");
				jobduty=rs.getString("ewd_jobduty");
				content=rs.getString("ewd_jobcontent");
				type=rs.getString("ewd_worktype");
				time=rs.getString("ewd_worktime");
			}else if(!"".equals(wrscode)){
				content+=" -"+rs.getString("ewd_jobcontent");;
			}else{
				map.put("wrd_worktype",type);
				map.put("wrd_jobduty",jobduty);
				map.put("wrd_jobcontent",content);
				map.put("wrd_worktime",time);
				map.put("wrd_wrscode",wrscode);
				wrscode=rs.getString("ewd_wrscode")==null?"":rs.getString("ewd_wrscode");
				jobduty=rs.getString("ewd_jobduty");
				content=rs.getString("ewd_jobcontent");
				type=rs.getString("ewd_worktype");
				time=rs.getString("ewd_worktime");
			}
			if(map.size()!=0){
				datas.add(map);
			}
		}
		//载入任务
		SqlRowList rs1=baseDao.queryForRowSet("select name,description,REPLACE(remindmsg,'<br/>',' ') from resourceAssignment "
				+ "left join ProjectTask on ra_taskid=id where (handstatuscode<>'FINISHED' or "
				+ "(handstatuscode='FINISHED' and realenddate=sysdate)) and resourcecode='"+code+"' order by taskcode");
		while(rs1.next()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("wrd_worktype","临时任务");
			map.put("wrd_jobduty",rs1.getString("name"));
			map.put("wrd_jobcontent",rs1.getString("description"));
			map.put("wrd_remark",rs1.getString("REPLACE(remindmsg,'<br/>',' ')"));
			datas.add(map);
		}
		return datas;
	}

}
