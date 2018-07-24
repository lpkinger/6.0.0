package com.uas.erp.service.plm.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.TaskResourceChangeService;

@Service(value = "taskResourceChangeService")
public class TaskResourceChangeServiceImpl implements TaskResourceChangeService {
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TaskUtilService taskUtilService;
	@Autowired
	private BaseDao baseDao;

	@Override
	public void saveTaskResourceChange(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.handler("TaskResourceChange", "save", "before", new Object[] { store });
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectTaskChange", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save("TaskResourceChange", "ptc_id", store.get("ptc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("TaskResourceChange", "save", "after", new Object[] { store });
	}

	@Override
	public void deleteTaskResourceChange(int id) {
		handlerService.handler("TaskResourceChange", "delete", "before", new Object[] { id });
		baseDao.deleteById("ProjectTaskChange", "ptc_id", id);
		// 记录操作
		baseDao.logger.delete("TaskResourceChange", "ptc_id", id);
		// 执行保存后的其它逻辑
		handlerService.handler("TaskResourceChange", "delete", "after", new Object[] { id });
	}

	@Override
	public void updateTaskResourceChange(String formStore, String param) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.handler("TaskResourceChange", "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProjectTaskChange", "ptc_id"));
		// 记录操作
		baseDao.logger.update("TaskResourceChange", "ptc_id", store.get("ptc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("TaskResourceChange", "save", "after", new Object[] { store });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditTaskResourceChange(int id,String caller) {
		handlerService.handler("TaskResourceChange", "audit", "before", new Object[] { id });
		List<String> sqls = new ArrayList<String>();
		String resourcename[], resourcecode[], resourceemid[] = null;

		// 执行变更
		Object[] datas = baseDao.getFieldsDataByCondition("ProjectTaskChange",
				"ptc_oldtaskid,ptc_type,PTC_OLDTASKMAN,PTC_TASKMAN,PTC_PRJID", "ptc_id=" + id);
		Object taskid = datas[0];
		String changekind = String.valueOf(datas[1]);
		String condition = "";
		if (changekind.equals("全部移交")) {
			condition = "handstatuscode <>'FINISHED'  AND handstatuscode<>'FINISH' and nvl(prjplanid,0)<>0";
		} else if (changekind.equals("部分移交")) {
			if (taskid == null && datas[4] == null) {
				BaseUtil.showError("部分移交需要设置 相应的项目或者对应的任务");
			} else {
				condition = taskid != null ? "id=" + taskid : "prjplanid=" + datas[4]
						+ " and handstatuscode <>'FINISHED'  AND handstatuscode<>'FINISH'";
			}
		}
		String conditionsingle = condition + " and  resourcecode ='" + datas[2] + "'";
		String conditionmore = condition + " and resourcecode <>'" + datas[2] + "' and resourcecode like '%" + datas[2] + "%'";
		Object[] employeedata = baseDao.getFieldsDataByCondition("Employee", "em_code,em_name,em_id", "em_code ='" + datas[3] + "'");

		// resourceAssignmentUpdateSql="update resourceAssignment set ra_resourcecode='"+employeedata[0]+"',ra_resourcename='"+employeedata[1]+"',ra_emid="+employeedata[2]+" where ra_taskid in ( select id from projecttask where "+conditionsingle+" and handstatuscode <>'UNACTIVE' ) and  ra_resourcecode='"+datas[2]+"'";
		// 变更所有
		String resourceAssignmentUpdateSql = "update resourceAssignment set ra_resourcecode='" + employeedata[0] + "',ra_resourcename='"
				+ employeedata[1] + "',ra_emid=" + employeedata[2] + " where ra_taskid in ( select id from projecttask where "
				+ conditionsingle + "  ) and  ra_resourcecode='" + datas[2] + "'";
		sqls.add(resourceAssignmentUpdateSql);
		String SingleUpdateSql = "update projecttask set resourcecode='" + employeedata[0] + "',resourcename='" + employeedata[1]
				+ "',resourceemid=" + employeedata[2] + " where " + conditionsingle;
		sqls.add(SingleUpdateSql);
		String QuerySql = " select resourcecode,resourcename,resourceemid,id from projectTask where " + conditionmore;
		SqlRowList sl = baseDao.queryForRowSet(QuerySql);
		while (sl.next()) {
			boolean flag=false;
			String newresourcename = "", newresourcecode = "", newresourceemid = "";
			resourcecode = sl.getString(1).split(",");
			if(sl.getString(2)==null)BaseUtil.showError("任务资源名称缺失,请修改检查任务资源名称数据是否正常!");
			resourcename = sl.getString(2).split(",");
			resourceemid = sl.getString(3).split(",");
			for (int i = 0; i < resourcecode.length; i++) {
				if (resourcecode[i].equals(datas[2].toString())) {
					flag=true;
					newresourcename += employeedata[1];
					newresourcecode += employeedata[0];
					newresourceemid += employeedata[2];
				} else {
					newresourcename += resourcename[i];
					newresourcecode += resourcecode[i];
					newresourceemid += resourceemid[i];
				}
				if (i < resourcecode.length - 1) {
					newresourcename += ",";
					newresourcecode += ",";
					newresourceemid += ",";
				}
			}
			// 包含不一定正确 如 A00023,0023A 匹配023 其实没有匹配 所以更新resourceAssignment
			// 也需要这样去判定
			if (flag) {
				sqls.add("update projecttask set resourcecode='" + newresourcecode + "',resourcename='" + newresourcename
						+ "',resourceemid='" + newresourceemid + "' where id=" + sl.getInt(4));
				sqls.add("update resourceAssignment set ra_resourcecode='" + employeedata[0] + "',ra_resourcename='" + employeedata[1]
						+ "',ra_emid=" + employeedata[2] + " where ra_taskid=" + sl.getInt(4) + " and ra_resourcecode='" + datas[2] + "'");
			}
		}
		
		//项目团队人员变更
		List<Object> prjids = baseDao.getFieldDatasByCondition("(select prjplanid from projecttask where "+conditionsingle +"union select prjplanid from projecttask where "+conditionmore+") ", "prjplanid", "1=1");
		for(Object prjid:prjids){
			int i = baseDao.getCount("select count(*) from resourceassignment where ra_prjid="+prjid+" and  RA_RESOURCECODE='"+datas[2]+"'");
			if(i>0){
				int count = baseDao.getCount("select count(*) from Teammember left join Team on team_id=tm_teamid left join project on team_pricode=prj_code where prj_id="+prjid+" and TM_EMPLOYEECODE='"+datas[3]+"'");
				if(count==0){
					int detno=0;
					Object[] teamdata = baseDao.getFieldsDataByCondition("Teammember left join Team on team_id=tm_teamid left join project on team_pricode=prj_code",new String[]{"max(nvl(tm_detno,0))","max(team_id)"}, "prj_id="+prjid);//最大序号
					Object functionaldata = baseDao.getFieldDataByCondition("Teammember left join Team on team_id=tm_teamid left join project on team_pricode=prj_code", "wm_concat(tm_functional)", "tm_employeecode='"+datas[2]+"' and prj_id="+prjid);//项目角色
					detno=teamdata[0]==null?0:(Integer.parseInt(teamdata[0].toString())+1);
					sqls.add(" insert into Teammember(tm_id,tm_detno,tm_employeecode,tm_employeename,tm_employeejob,tm_teamid,tm_employeeid,tm_prjid,tm_functional) "
							+ "select Teammember_seq.nextval,"+detno+",em_code,em_name,em_defaulthsid,"+teamdata[1]+",em_id,"+prjid+",'"+functionaldata+"' from employee where em_code='"+datas[3]+"'");
					//sqls.add("update teammember set (TM_EMPLOYEECODE,TM_EMPLOYEEID,TM_EMPLOYEENAME,TM_EMPLOYEEJOB)=(select em_code,em_id,em_name,EM_DEFAULTHSID from EMPLOYEE WHERE em_code='"+employeedata[0]+"') where tm_prjid="+prjid+" and tm_employeecode='"+datas[2]+"'");
				}
			}
		}
		baseDao.execute(sqls);
		baseDao.audit("ProjectTaskChange", "ptc_id=" + id, "ptc_status", "ptc_statuscode");
		baseDao.logger.audit("TaskResourceChange", "ptc_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler("TaskResourceChange", "audit", "after", new Object[] { id });
	}

	@Override
	public void submitTaskResourceChange(int id) {
		handlerService.handler("TaskResourceChange", "commit", "before", new Object[] { id });
		// 执行提交操作
		Object[] obj= baseDao.getFieldsDataByCondition("projecttaskchange", new String[]{"ptc_type","ptc_prjid","ptc_oldtaskman"}, "ptc_id="+id);
		if("部分移交".equals(obj[0])){
			if(obj[1]==null)BaseUtil.showError("部分移交时需要设置相应的项目或者对应的任务!");
			String sql="select count(*) from teammember where tm_prjid="+obj[1]+" and TM_EMPLOYEECODE='"+obj[2]+"'";
			int count = baseDao.getCount(sql);
			if(count==0)BaseUtil.showError("部分移交时原移交人必须在该项目中!");
		}else{
			//全部移交时，检测原移交人是否有任务存在
			String sql="select count(*) from projecttask where handstatuscode <>'FINISHED'  AND handstatuscode<>'FINISH' and nvl(prjplanid,0)<>0";
			int c1 = baseDao.getCount(sql+" and  resourcecode ='" + obj[2]+ "'");
			int c2=baseDao.getCount(sql+" and resourcecode <>'" + obj[2] + "' and resourcecode like '%" + obj[2] + "%'");
			if((c1+c2)==0)BaseUtil.showError("该移交人在当前所有项目中未有任务存在!");
		}
		baseDao.submit("ProjectTaskChange", "ptc_id=" + id, "ptc_status", "ptc_statuscode");
		baseDao.logger.submit("TaskResourceChange", "ptc_id", id);
		// 执行提交后的其它逻辑
		handlerService.handler("TaskResourceChange", "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitTaskResourceChange(int id) {
		handlerService.handler("TaskResourceChange", "resCommit", "before", new Object[] { id });
		baseDao.resOperate("ProjectTaskChange", "ptc_id=" + id, "ptc_status", "ptc_statuscode");
		baseDao.logger.resSubmit("TaskResourceChange", "ptc_id", id);
		handlerService.handler("TaskResourceChange", "resCommit", "after", new Object[] { id });
	}

	@Override
	public void resAuditTaskResourceChange(int id) {

	}

	@Override
	public void batchRescourceChange(String data) {
		List<Map<Object, Object>> gridStore = BaseUtil.parseGridStoreToMaps(data);
		List<String> sqls = null;
		int ra_id = 0;
		Object resourcecode;
		String resourcename[], resourcecodes[], resourceemid[] = null;
		String code = null;
		Employee employee = SystemSession.getUser();
		
		for (Map<Object, Object> res : gridStore) {
			sqls = new ArrayList<String> ();
			ra_id = (int) res.get("id");
			code = baseDao.sGetMaxNumber("ProjectTaskChange", 2);
			resourcecode = res.get("resourcecode");
			String remark = res.get("remark") == null ? "" : res.get("remark").toString();
			Object[] task = baseDao.getFieldsDataByCondition("resourceassignment", "ra_taskid,ra_emid,RA_RESOURCECODE,RA_RESOURCENAME,ra_prjid,RA_PRJNAME,RA_TASKNAME", "ra_id=" + ra_id);
			Object[] emp = baseDao.getFieldsDataByCondition("employee", "em_id,em_code,em_name", "em_code='" + resourcecode + "'");
			if (emp != null) {
				//更新resourceassignment表
				sqls.add("update resourceassignment set (ra_resourcecode,ra_resourcename,ra_emid) = ( select em_code,em_name,em_id from employee "
						+ "where em_code='" + resourcecode + "') where  ra_id=" + ra_id);
				//更新projecttask
				String QuerySql = " select resourcecode,resourcename,resourceemid,id from projectTask where id=" + task[0] ;
				SqlRowList sl = baseDao.queryForRowSet(QuerySql);
				while (sl.next()) {
					boolean flag = false;
					String newresourcename = "", newresourcecode = "", newresourceemid = "";
					resourcecodes = sl.getString(1).split(",");
					if (sl.getString(2) == null)
						BaseUtil.showError("任务资源名称缺失,请修改检查任务资源名称数据是否正常!");
					resourcename = sl.getString(2).split(",");
					resourceemid = sl.getString(3).split(",");
					for (int i = 0; i < resourcecodes.length; i++) {
						if (resourcecodes[i].equals(task[2].toString())) {
							flag = true;
							newresourcename += emp[2];
							newresourcecode += emp[1];
							newresourceemid += emp[0];
						} else {
							newresourcename += resourcename[i];
							newresourcecode += resourcecodes[i];
							newresourceemid += resourceemid[i];
						}
						if (i < resourcecodes.length - 1) {
							newresourcename += ",";
							newresourcecode += ",";
							newresourceemid += ",";
						}
					}
					// 包含不一定正确 如 A00023,0023A 匹配023 其实没有匹配
					// 也需要这样去判定
					if (flag) {
						sqls.add("update projecttask set resourcecode='" + newresourcecode + "',resourcename='" + newresourcename + "',resourceemid='"
								+ newresourceemid + "' where id=" + sl.getInt(4));
					}
				}
				//判断项目团队是否有该成员
				int count = baseDao.getCount("select count(1) from Teammember left join Team on team_id=tm_teamid left join project on team_pricode=prj_code where prj_id=" + task[4] + " and TM_EMPLOYEECODE='" + resourcecode + "'");
				if(count==0){
					int detno=0;
					Object[] teamdata = baseDao.getFieldsDataByCondition("Teammember left join Team on team_id=tm_teamid left join project on team_pricode=prj_code", new String[]{"max(nvl(tm_detno,0))","max(team_id)"}, "prj_id=" + task[4] );//最大序号
					Object functionaldata = baseDao.getFieldDataByCondition("Teammember left join Team on team_id=tm_teamid left join project on team_pricode=prj_code", "wm_concat(tm_functional)", "tm_employeecode='" + task[2] + "' and prj_id=" + task[4]);//项目角色
					detno = teamdata[0] == null ? 0 : (Integer.parseInt(teamdata[0].toString()) + 1);
					sqls.add(" insert into Teammember(tm_id,tm_detno,tm_employeecode,tm_employeename,tm_employeejob,tm_teamid,tm_employeeid,tm_prjid,tm_functional) "
							+ "select Teammember_seq.nextval," + detno + ",em_code,em_name,em_defaulthsid," + teamdata[1] + ",em_id," +  task[4] + ",'" + functionaldata + "' from employee where em_code='" + resourcecode + "'");
					}	
				sqls.add(log("批量资源更新", task[0].toString(),""));
				//生成审核的任务资源变更单
				sqls.add("insert into ProjectTaskChange (ptc_id,ptc_code,ptc_class,ptc_prjid,ptc_prjname,ptc_type,ptc_oldtaskid,ptc_oldtaskname,ptc_oldtaskmanname,ptc_oldtaskman,ptc_taskmanname,ptc_taskman,ptc_status,ptc_statuscode,ptc_recorder,ptc_recorddate,ptc_remark)"
					+ "select ProjectTaskChange_seq.nextval,'" + code + "','资源变更单','" + task[4] + "','" + task[5] + "','批量移交','" + task[0] + "','" + task[6] + "', '" + task[3] + "', '" + task[2] + "','" + emp[2] + "', '" + emp[1] + "', '已审核' , 'AUDITED', '" + employee.getEm_name() + "',sysdate,'" + remark.replace("'", "''") + "'  from dual");
			}
			baseDao.execute(sqls);
		}
	}
	
	private String log(String type,String id,String remark ){
		StringBuffer sql=new StringBuffer("insert into tasklog (tl_id,tl_date,tl_recordman,tl_type,tl_startdate,tl_enddate,tl_resource,tl_resoccupy,tl_name,tl_taskid,tl_planid,tl_docname,tl_remark) select tasklog_seq.nextval,");
		Date date=new Date();
		Employee employee = SystemSession.getUser();
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sql.append("to_date('"+sf.format(date)+"' ,'yyyy-mm-dd hh24:mi:ss'),");
		sql.append("'"+employee.getEm_name()+"','"+type+"',");
		sql.append("startdate,enddate,RESOURCENAME,resourceunits,name,id,prjplanid,PRJDOCNAME,");
		if(remark!=null&&!"".equals(remark))
			sql.append("'"+remark+"'");
		else
			sql.append("null");
		sql.append(" from projecttask where id in ("+id+") ");
		return sql.toString();
	}
	
}
