package com.uas.erp.service.plm.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.FormDao;
import com.uas.erp.service.plm.ProjectTaskChangeService;

@Service
public class ProjectTaskChangeServiceImpl implements ProjectTaskChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private FormDao formDao;
	@Autowired
	private TaskUtilService taskUtilService;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProjectTaskChange(String formStore, String gridStore) {
		Map<Object, Object> formstore = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 执行保存前的其它逻辑
		handlerService.handler("ProjectTaskChange", "save", "before", new Object[] { formStore, gridstore });
		String formSql = SqlUtil.getInsertSqlByFormStore(formstore, "ProjectTaskChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		Object[] rc_id = new Object[gridstore.size()];
		for (int i = 0; i < gridstore.size(); i++) {
			Map<Object, Object> map = gridstore.get(i);
			rc_id[i] = baseDao.getSeqId("RESOURCEASSIGNMENTCHANGE_SEQ");
			map.put("rc_id", rc_id[i]);
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridstore, "ResourceAssignmentChange");
		baseDao.execute(gridSql);
		baseDao.logger.save("ProjectTaskChange", "ptc_id", formstore.get("ptc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("ProjectTaskChange", "save", "after", new Object[] { formStore, gridstore });
	}

	@Override
	public void updateProjectTaskChange(String formStore, String param) {
		Map<Object, Object> formstore = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gridstore = BaseUtil.parseGridStoreToMaps(param);
		handlerService.handler("ProjectTaskChange", "save", "before", new Object[] { formStore, gridstore });
		String formSql = SqlUtil.getUpdateSqlByFormStore(formstore, "ProjectTaskChange", "ptc_id");
		baseDao.execute(formSql);
		List<String> girdSql = SqlUtil.getUpdateSqlbyGridStore(gridstore, "ResourceAssignmentChange", "rc_id");
		baseDao.execute(girdSql);
		// 记录操作
		baseDao.logger.update("ProjectTaskChange", "ptc_id", formstore.get("ptc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("ProjectTaskChange", "save", "after", new Object[] { formStore, gridstore });
	}

	@Override
	public void deleteProjectTaskChange(int id) {
		// 执行删除前的其它逻辑
		handlerService.handler("ProjectTaskChange", "delete", "before", new Object[] { id });
		// 删除purchase
		baseDao.deleteById("ProjectTaskChange", "ptc_id", id);
		// 记录操作
		baseDao.logger.delete("ProjectTaskChange", "ptc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler("ProjectTaskChange", "delete", "after", new Object[] { id });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditProjectTaskChange(int id) {
		Map<Object, Object> form = BaseUtil.parseFormStoreToMap(baseDao.getDataStringByForm(
				formDao.getForm("ProjectTaskChange", SystemSession.getUser().getEm_master()), "ptc_id=" + id));
		handlerService.beforeAudit("ProjectTaskChange", form.get("ptc_oldtaskid"));
		// 进行变更 变更当前任务 变更父任务。。。 以及任务的分配情况 如果该任务存在里程碑的话 变更任务之前必须要变更里程碑 主要是结束时间的判断
		// {主表 : 任务 名称 开始时间 ，结束时间 设置里程碑 交附件 上级任务的变更 从表 主要包括 分配人员 分配比率的变更}
		int stone = Integer.parseInt(form.get("ptc_milestone").toString());
		boolean bool1 = false;
		boolean bool2 = false;
		List<String> sqls=new LinkedList<String>();
		List<Map<String, Object>> maps=new LinkedList<Map<String,Object>>();
		StringBuffer sb = new StringBuffer();
		Object prjid=form.get("ptc_prjid");
		Object	 taskid=form.get("ptc_oldtaskid");
		Object proposer=form.get("ptc_proposer");
		String enddate = form.get("ptc_enddate").toString();
		String startdate = form.get("ptc_startdate").toString();
		String name = form.get("ptc_name").toString();
		String duration = String.valueOf(form.get("ptc_newduration"));
		int oldduration = Integer.parseInt(form.get("ptc_newduration").toString());
		int needattach = Integer.parseInt(form.get("ptc_needattach").toString());
		String delayReason=form.get("ptc_delayreason")==null?"":form.get("ptc_delayreason").toString();
		String solution=form.get("ptc_solution")==null?"":form.get("ptc_solution").toString();
		if (stone == 1) {
			// 说明存在了历程碑 对于该任务的变化需要 一般就结束时间为基准 先变更里程碑
			String href = "<br/><a href=\"javascript:openUrl('jsps/plm/chang/milestone.jsp?formCondition=idIS" + form.get("ptc_oldtaskid")
					+ "')\">" + form.get("ptc_oldtaskname") + "</a>&nbsp;";
			if (!enddate.equals("") && !enddate.equals(form.get("ptc_oldenddate")))
				BaseUtil.showError("该任务存在里程碑需先变更历程碑,先变更里程碑!" + href);
		}
		sb.append(name.equals("") ? "" : "name='" + name + "',");
		sb.append(needattach == 0 ? "" : "isneedattach='" + needattach + "',");
		//延迟原因 和解决方案 更新到projecttask中（研发任务书从表）
		sb.append("".equals(delayReason)?"":"delayreason='"+delayReason+"',");
		sb.append("".equals(solution)?"":"solution='"+solution+"',"); 
		if (!duration.equals("null") && !duration.equals("") && !duration.equals(form.get("ptc_oldduration"))) {
			Double duration1 = Double.parseDouble(duration);
			sb.append("duration=" + duration1 + ",");
			if (oldduration < duration1) {
				// 保持得分不变的情况下更新 占用时间
				sb.append("resourcetimerate=" + NumberUtil.formatDouble(duration1 * 100 / oldduration, 2) + ",");
			}
		}
		if(!duration.equals("null") && !duration.equals("")&&!duration.equals("0")){
			//本身任务时间变更
			String end=getNewDate(enddate,true);
			Object[] date = baseDao.getFieldsDataByCondition("projecttask", "startdate,enddate", "id="+taskid);
			if(compareDate(startdate.toString(),date[0].toString())&&compareDate(end.toString(),date[1].toString())){
			}
			else {
				bool2=true;
				String start=getNewDate(startdate,false);	
				Map<String,Object> map=new HashMap<String,Object>();
				List<String> update=new LinkedList<String>();
				map.put("ID", form.get("ptc_oldtaskid"));
				map.put("TO_CHAR(ENDDATE,'YYYY-MM-DD')",end);
				map.put("DE_FROM","");
				maps.add(map);
				update.add("update projecttask set startdate=to_date('"+start+"','yyyy-mm-dd'),enddate=to_date('"+end+"','yyyy-mm-dd'), duration="+duration+" where id="+form.get("ptc_oldtaskid"));
				update.add("update resourceassignment set (ra_startdate,ra_enddate)=(select startdate,enddate from projecttask where id="+ form.get("ptc_oldtaskid")+") where ra_taskid="+form.get("ptc_oldtaskid"));
				update.add("insert into tasklog (tl_id,tl_date,tl_recordman,tl_type,tl_startdate,tl_enddate,tl_resource,tl_resoccupy,tl_name,tl_taskid,tl_planid,tl_docname)"
				+ " select tasklog_seq.nextval,sysdate,'"+proposer+"','计划日期变更',startdate,enddate,resourcename,resourceunits,name,id,prjplanid,prjdocname from projecttask where id="+form.get("ptc_oldtaskid"));
				baseDao.execute(update);
				// 更新所有关联任务的时间
				// 判断是否有与到关联的任务与父任务
				bool1 = baseDao.checkIf("dependency", "de_from=" + form.get("ptc_oldtaskid") + " AND de_prjid=" + form.get("ptc_prjid"));	
			}
		}
		/*
		 * if (stone == 0 && milestone == 1) { // 说明变更重新设置历程碑 int stoneid =
		 * baseDao.getSeqId("PROJECTTASK_SEQ"); // 将关联的里程碑更新进去
		 * sb.append("relateid=" + stoneid); // 保存里程碑 Map<Object, Object>
		 * milestonestore = new HashMap<Object, Object>();
		 * milestonestore.put("id", stoneid); milestonestore.put("type", 1);
		 * milestonestore.put("relateid", milestonestore.get("id"));
		 * milestonestore.put("startdate", enddate);
		 * milestonestore.put("enddate", enddate);
		 * milestonestore.put("baselineenddate", enddate);
		 * milestonestore.put("baselinestartdate", enddate);
		 * milestonestore.put("recorder", employee.getEm_name());
		 * milestonestore.put("recorddate", new
		 * SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		 * milestonestore.put("status", "已审核"); milestonestore.put("statuscode",
		 * "AUDITED"); // 可能存在 上级任务的变更 。。再说 String mileStoreSql =
		 * SqlUtil.getInsertSqlByFormStore(milestonestore, "ProjectTask", new
		 * String[] {}, new Object[] {}); baseDao.execute(mileStoreSql); }
		 */
		String condition = "id=" + form.get("ptc_oldtaskid");
		// 执行主表变更
		baseDao.updateByCondition("ProjectTask", sb.toString().substring(0, sb.toString().length() - 1), condition);
		if (bool1) {
			/*Object[] data = baseDao.getFieldsDataByCondition("ProjectTask left join projectmaintask on ptid=pt_id",
					"ptid,detno,pt_taskstartdate", "id=" + form.get("ptc_oldtaskid"));
			taskUtilService.updateDate(data[0], data[2].toString().substring(0, 10), null);*/
			//关联任务时间变更
			 maps.addAll( baseDao.queryForList("select de_from,id,duration,to_char(enddate,'yyyy-mm-dd') from "
					+ "(SELECT * FROM  dependency tn where tn.DE_PRJID="+form.get("ptc_prjid")+" START WITH tn.DE_FROM="+form.get("ptc_oldtaskid")
					+ " CONNECT BY  tn.de_from= prior tn.de_to) tab left join projecttask on de_to=id order by de_from,detno"));
			sqls.addAll( taskUtilService.changeTime(maps, form.get("ptc_oldtaskid"),proposer));
			baseDao.execute(sqls);
		}
		if(bool2){
			//更新父任务的时间
			List<Object> parentIds = baseDao.getFieldDatasByCondition("projecttask", "DISTINCT parentid,TASKLEVEL", " prjplanid="+prjid+" and  nvl(parentid,0)<>0 order by tasklevel desc,parentid desc");
			if(parentIds.size()>0){
				sqls.clear();
				List<Map<String,Object>> tasks = baseDao.queryForList("select id,to_char(startdate,'yyyy-mm-dd'),to_char(enddate,'yyyy-mm-dd'),parentid from projecttask where prjplanid="+form.get("ptc_prjid"));
				sqls.addAll(taskUtilService.changeFatherTime(parentIds,tasks));
				baseDao.execute(sqls);
			}
			//消息模板
			Object mmid=baseDao.getFieldDataByCondition("MESSAGEMODEL left join MESSAGEROLE on mm_id=mr_mmid", "distinct mm_id", "MR_ISUSED=-1 AND MM_ISUSED=-1 and mm_caller='ProjectTaskChange'");
			//调用生成消息的存储过程
			if (mmid != null) {
			Object emcode = baseDao.getFieldDataByCondition("employee", "em_code", "em_name='"+proposer+"'");
			baseDao.callProcedure("SP_CREATEINFO",new Object[] { mmid,emcode, taskid,DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) });
			}	
	}
		// 明细表也可能存在变更
		// List<String> updatedetail = new ArrayList<String>();
		/*
		 * for (Map<String, Object> gridMap : grid) { StringBuffer updatesb =
		 * new StringBuffer();
		 * updatesb.append("Update ResourceAssignment set ");
		 * updatesb.append("ra_resourceid=" + gridMap.get("rc_resourceid") +
		 * ","); updatesb.append("ra_resourcecode='" +
		 * gridMap.get("rc_resourcecode") + "',");
		 * updatesb.append("ra_resourcename='" + gridMap.get("rc_resourcename")
		 * + "',"); updatesb.append(name.equals("") ? "" : "ra_taskname='" +
		 * name + "',"); updatesb.append("ra_units=" + gridMap.get("rc_units") +
		 * ","); updatesb.append("ra_holdtime=" + gridMap.get("rc_holdtime"));
		 * updatesb.append(" where ra_detno=" + gridMap.get("rc_olddetno") +
		 * " AND ra_taskid=" + form.get("ptc_oldtaskid"));
		 * updatedetail.add(updatesb.toString()); }
		 */
		// baseDao.execute(updatedetail);	
		
		// 执行审核操作
		baseDao.audit("ProjectTaskChange", "ptc_id=" + id, "ptc_status", "ptc_statuscode");
		// 记录操作
		baseDao.logger.audit("ProjectTaskChange", "ptc_id", id);
		// 执行审核后的其它逻辑 处理
		handlerService.afterAudit("ProjectTaskChange", form.get("ptc_oldtaskid"));
	}
	
	private String getNewDate(String date,boolean flag){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d = sdf.parse(date);
			Calendar cal=Calendar.getInstance();
			cal.setTime(d);
			int i=cal.get(Calendar.DAY_OF_WEEK);
			if(i==1){
				cal.add(Calendar.DATE, 1);
			}else if(i==7){
				cal.add(Calendar.DATE, 2);
			}
			if(flag){
				cal.add(Calendar.DATE, 1);}
			return sdf.format(cal.getTime());
		} catch (ParseException e) {
			return date;
		}	
	}
	
	private boolean compareDate(String d1,String d2){
		if(d1!=null&&d2!=null){
		Long t1=DateUtil.parseStringToDate(d1, Constant.YMD).getTime();
		Long t2=DateUtil.parseStringToDate(d2, Constant.YMD).getTime();
		return t1.equals(t2)?true:false;
			}
		return false;
	}
	
	@Override
	public void resAuditProjectTaskChange(int id) {
		// 执行反审核操作
		baseDao.resOperate("ProjectTaskChange", "ptc_id=" + id, "ptc_status", "ptc_statuscode");
		// 记录操作
		baseDao.logger.resAudit("ProjectTaskChange", "ptc_id", id);
	}

	@Override
	public void submitProjectTaskChange(int id) {
		// 执行提交前的其它逻辑
		handlerService.handler("ProjectTaskChange", "commit", "before", new Object[] { id });
		// 执行提交操作
		baseDao.submit("ProjectTaskChange", "ptc_id=" + id, "ptc_status", "ptc_statuscode");
		// 记录操作
		baseDao.logger.submit("ProjectTaskChange", "ptc_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler("ProjectTaskChange", "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProjectTaskChange(int id) {
		// 执行反提交操作
		handlerService.handler("ProjectTaskChange", "resCommit", "before", new Object[] { id });
		baseDao.resOperate("ProjectTaskChange", "ptc_id=" + id, "ptc_status", "ptc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("ProjectTaskChange", "ptc_id", id);
		handlerService.handler("ProjectTaskChange", "resCommit", "after", new Object[] { id });
	}	

}
