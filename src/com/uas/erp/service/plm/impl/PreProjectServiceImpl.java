package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.plm.PreProjectService;


@Service("preProjectService")
public class PreProjectServiceImpl implements PreProjectService{
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private HandlerService handlerService;
	
	
	//发起一次任务
	public long LaunchBillTask(Map<Object, Object> store, Employee employee) {
		store.put("recorder", employee.getEm_name());
		store.put("recorderid", employee.getEm_id());
		store.put("class", "billtask");
		store.put("statuscode", store.get("statuscode") != null && !"".equals("statuscode") ? store.get("statuscode") : "AUDITED");
		store.put("status", store.get("status") != null && !"".equals("status") ? store.get("status") : "已审核");
		store.put("handstatus", "已启动");
		store.put("handstatuscode", "DOING");
		Master master = employee.getCurrentMaster();
		String mastername = master == null ? null : master.getMa_name();
		String resourcecode = String.valueOf(store.get("resourcecode"));
		String sourcelink = String.valueOf(store.get("sourcelink"));
		sourcelink = sourcelink.replaceAll("='", "IS").replaceAll("'", "");
		store.put("sourcelink", sourcelink);
		List<String> sqls = new ArrayList<String>();
		int detno = 1;
		long id = 0;
		SqlRowList rs = baseDao.queryForRowSet("select em_id,em_code,em_name from employee where em_code ='"+resourcecode+"'");
		while (rs.next()) {
			store.put("taskcode", baseDao.sGetMaxNumber("ProjectTask", 2));
			id = baseDao.getSeqId("PROJECTTASK_SEQ");
			StringBuffer sb = new StringBuffer();
			sb.append("<a style=\"color:blue\" ");//href=\"#\"
			sb.append("'')\">");
			sb.append("任务提醒&nbsp;&nbsp;&nbsp;&nbsp;[");
			sb.append(DateUtil.parseDateToString(null, "MM-dd HH:mm"));
			sb.append("]</a></br>#<a style=\"color:blue\" href=\"javascript:openUrl(''jsps/plm/record/billrecord.jsp?formCondition=idIS");
			sb.append(id);
			sb.append("&gridCondition=ra_taskidIS");
			sb.append(id);
			sb.append("'',''"+mastername+"'')\">");
			sb.append(store.get("name"));
			sb.append("</a>");
			sb.append("#&nbsp;&nbsp;&nbsp;&nbsp;");			
			store.put("id", id);
			sqls.add("insert into resourceassignment(ra_id,ra_taskid,ra_emid,ra_resourcecode,ra_resourcename,ra_detno,ra_status,ra_statuscode,ra_units,ra_type) values (resourceassignment_seq.nextval,'"
					+ id
					+ "','"
					+ rs.getInt("em_id")
					+ "','"
					+ rs.getString("em_code")
					+ "','"
					+ rs.getString("em_name")
					+ "',"
					+ detno++
					+ ",'进行中','START',100,'billtask')");
			store.put("resourcecode", rs.getString("em_code"));
			store.put("resourceemid", rs.getInt("em_id"));
			sqls.add(SqlUtil.getInsertSqlByMap(store, "ProjectTask"));
			sqls.add("update projecttask set recorddate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+" where id=" + id);
			sqls.add("update resourceassignment set (ra_taskname,ra_startdate,ra_enddate)=(select name,startdate,enddate from ProjectTask where id=ra_taskid) where ra_taskid="
					+ id);
		}
		baseDao.execute(sqls);
		return id;
	}

	@Override
	public void updatePreProjectById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的预立项任务书资料!
		Object status = baseDao.getFieldDataByCondition("PreProject", "pp_statuscode", "pp_id=" + store.get("pp_id"));
		StateAssert.updateOnlyEntering(status);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(prj_code) from Project where prj_name = ?", String.class,store.get("pp_prjtitle"));
		if (dets != null) {
			BaseUtil.showError("项目名称不允许重复！");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(pp_code) from PreProject where pp_prjtitle = ? and pp_id <> ?", String.class,store.get("pp_prjtitle"),store.get("pp_id"));
		if (dets != null) {
			BaseUtil.showError("项目名称不允许重复！");
		}
				
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改PreProject
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreProject", "pp_id");
		
		// 修改PreProjectDetail
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> map : gstore) {
			if (map.get("ppd_start").toString().compareTo(map.get("ppd_end").toString())>0) {
				BaseUtil.showError("明细表第" + map.get("ppd_detno")+ "行的计划完成日期小于计划开始日期");
			}
			if (DateUtil.getCurrentDate().compareTo(map.get("ppd_start").toString())>0) {
				BaseUtil.showError("明细表第" + map.get("ppd_detno")+ "行的计划开始日期小于当前日期");
			}
			Object ppdid = map.get("ppd_id");
			if (ppdid==null||"".equals(ppdid)||"0".equals(ppdid)) {
				sqls.add(SqlUtil.getInsertSql(map, "PreProjectDetail", "ppd_id"));
			}else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "PreProjectDetail", "ppd_id"));
			}
			
		}
		baseDao.execute(formSql);
		baseDao.execute(sqls);
		// 记录操作
		baseDao.logger.update(caller, "pp_id", store.get("pp_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}
	
	@Override
	public void deletePreProject(int pp_id, String caller) {
		// 只能删除在录入的预立项任务书!
		Object status = baseDao.getFieldDataByCondition("PreProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pp_id });
		baseDao.delCheck("PreProject", pp_id);	
		Object pr_code = baseDao.getFieldDataByCondition("PreProject", "pp_prcode", "pp_id=" + pp_id);
		//删除PreProjectDetail
		baseDao.deleteByCondition("PreProjectDetail", "ppd_ppid=" + pp_id);
		// 删除PreProject
		baseDao.deleteById("PreProject", "pp_id", pp_id);
		// 更新对应需求单中“转单状态”中为“空”
		baseDao.updateByCondition("PrjRequest", "pr_auditstatus =''", "pr_code = '"+pr_code+"'");
		// 记录操作
		baseDao.logger.delete(caller, "pp_id", pp_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pp_id });
	}
	
	@Override
	public void submitPreProject(int pp_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pp_id });
		// 执行提交操作
		baseDao.submit("PreProject", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pp_id", pp_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pp_id });
	}

	@Override
	public void resSubmitPreProject(int pp_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pp_id });
		// 执行反提交操作
		baseDao.resOperate("PreProject", "pp_id=" + pp_id, "pp_status", "pp_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pp_id", pp_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pp_id });
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditPreProject(int pp_id, String caller) {
		Employee employee = SystemSession.getUser();
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PreProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.auditOnlyCommited(status);
		
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pp_id });
		// 执行审核操作
		baseDao.audit("PreProject", "pp_id=" + pp_id, "pp_status", "pp_statuscode", "pp_auditdate", "pp_auditman");
		try {
			//发起任务
			SqlRowList rs = baseDao.queryForRowSet("select ppd_id,ppd_name,ppd_description,ppd_mancode,ppd_man,ppd_start,ppd_end,ppd_confirmor,ppd_confirmorid from PreProjectDetail where ppd_ppid="+pp_id);
			while (rs.next()) {
				Map<Object, Object> task = new HashMap<Object, Object>();
				Object start = rs.getObject("ppd_start");
				Object end = rs.getObject("ppd_end");
				float duration = (DateUtil.parse(end.toString(), Constant.YMD_HMS).getTime()-DateUtil.parse(start.toString(), Constant.YMD_HMS).getTime())/(1000*60*60);
				duration=duration==0?24:duration;
				task.put("name", rs.getObject("ppd_name"));
				task.put("resourcecode", rs.getObject("ppd_mancode"));
				task.put("resourcename", rs.getObject("ppd_man"));
				task.put("type",0); //不需要确认
				task.put("startdate", start);
				task.put("enddate", end);
				task.put("duration", duration);
				task.put("description", rs.getObject("ppd_description"));
				task.put("recorder", rs.getObject("ppd_confirmor"));
				task.put("recorderid", rs.getObject("ppd_confirmorid"));
				Employee emp  = employee;
				if(rs.getObject("ppd_confirmorid")!=null){
					if(!"".equals(rs.getObject("ppd_confirmorid").toString())&&!"0".equals(rs.getString("ppd_confirmorid"))){
						task.put("type",1); //需要确认
						
						emp = employeeDao.getEmployeeByEmId(rs.getInt("ppd_confirmorid"));
						Master master = employee.getCurrentMaster();
						emp.setCurrentMaster(master);
					}else{
					}
				}
				long taskid = LaunchBillTask(task,emp);
				baseDao.updateByCondition("PreProjectDetail", "ppd_taskid ="+taskid, "ppd_id="+rs.getObject("ppd_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("任务发起失败！");
		}
		// 记录操作
		baseDao.logger.audit(caller, "pp_id", pp_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pp_id });
	}

	@Override
	public void resAuditPreProject(int pp_id, String caller) {
		
		// 只能对状态为[已审核]的预立项任务书进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreProject", "pp_statuscode", "pp_id=" + pp_id);
		StateAssert.resAuditOnlyAudit(status);
		
		//已经存在关联已完成的任务单不允许反审核
		String tsskid = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ppd_taskid) from PreProjectDetail where ppd_ppid = ?", String.class,pp_id);
		
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ppd_detno) from PreProjectDetail left join ResourceAssignment on ppd_taskid =ra_taskid where ra_statuscode='FINISHED' and ra_taskid in ("+tsskid+")", String.class);
		
		if (dets != null) {
			BaseUtil.showError("该预立项任务书存在关联已完成的任务单，不允许反审核！");
		}		
		baseDao.resAuditCheck("PreProject", pp_id);
		// 执行反审核操作
		baseDao.resAudit("PreProject", "pp_id=" + pp_id, "pp_status", "pp_statuscode", "pp_auditdate", "pp_auditman");
		//产生的任务自动结案
		baseDao.updateByCondition("ResourceAsSignment", "ra_status='已结案',ra_statuscode='ENDED'", "ra_taskid in ("+tsskid+")");
		//清空明细行任务单号
		baseDao.updateByCondition("PreProjectDetail", "ppd_taskid = ''", "ppd_ppid ="+pp_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "pp_id", pp_id);
	}

	
	//预立项任务书转立项
	@Override
	public String turnProject(int pp_id, String caller,String title, Employee employee) {
		
		int prjId =0;
		String result ="";
		// 只能对状态为[已审核]的预立项任务书进行转立项操作!
		Object status = baseDao.getFieldDataByCondition("PreProject", "pp_statuscode", "pp_id="+pp_id);
		if(!"AUDITED".equals(status))
			BaseUtil.showError("未审核,不能转立项！");
		//对应任务都完成时此按钮才可以转单
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(ppd_detno) from PreProjectDetail left join ResourceAssignment on ppd_taskid =ra_taskid where ra_statuscode <> 'FINISHED' and ppd_ppid = ?", String.class,pp_id);
		
		if (dets != null) {
			BaseUtil.showError("有未完成的任务，不允许转立项！行号："+dets);
		}
		
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(prj_code) from Project where prj_sourcetype = '预立项' and prj_sourcecode in (select pp_code from PreProject where pp_id = ?)", String.class,pp_id);
		if (dets != null) {
			BaseUtil.showError("该预立项任务书已经转过项目申请单，请勿重复转单!");
		}
		
		try {
			prjId= baseDao.getSeqId("PROJECT_SEQ");
			Object prj_code = baseDao.sGetMaxNumber("Project", 2);
			Object [] pp = baseDao.getFieldsDataByCondition("PreProject", new String[]{"pp_code","pp_customercode","pp_customer"}, "pp_id="+pp_id);
			String sql = "insert into Project(prj_id,prj_code,prj_name,prj_sourcecode,prj_sourcetype,prj_customercode,prj_customername,prj_person,"
					+ "prj_recordate,prj_auditstatuscode,prj_auditstatus,prj_statuscode,prj_status,prj_class) values(?,?,?,?,'预立项',?,?,?,sysdate,'ENTERING','在录入','UNDOING','未启动','立项申请书')";
			
			boolean bool = baseDao.execute(sql, new Object[]{prjId,prj_code,title,pp[0],pp[1],pp[2],employee.getEm_name()});
			
			if (bool) {
				//写入正式立项编号
				baseDao.updateByCondition("PreProject", "pp_prjtitle = '"+title+"',pp_prjcode ='"+prj_code+"'","pp_id="+pp_id);
				result = "转立项成功,项目申请单号:" + "<a href=\"javascript:openUrl('jsps/plm/request/ProjectRequest.jsp?formCondition=prj_idIS"+prjId+"&gridCondition=pp_prjidIS"+prjId+"')\">" + prj_code + "</a>&nbsp;";
				//对应需求单状态变成“转立项”
				Object pr_code = baseDao.getFieldDataByCondition("PreProject", "pp_prcode", "pp_id=" + pp_id);
				baseDao.updateByCondition("PrjRequest", "pr_auditstatus ='转立项'", "pr_code = '"+pr_code+"'");
				baseDao.logger.turn("转项目申请单", caller, "pp_id", pp_id);
			}
				
		} catch (Exception e) {
			BaseUtil.showError("转项目申请单失败！错误"+e.getMessage());
		}
		return result;
	}

	@Override
	public Object getID(String formCondition) {
		// TODO Auto-generated method stub
		Object id = baseDao.getFieldDataByCondition("PreProject", "pp_id", formCondition);
		return id;
	}
	
	//变更责任人
	@Override
	public void changeResponsible(String caller,int id, String newman,Employee employee) {
		Map<Object, Object> map = BaseUtil.parseFormStoreToMap(newman);
		List<String> sqls = new ArrayList<String>();
		map.put("ppd_id", id);
		try {
			int resourceid = baseDao.getFieldValue("Employee", "em_id", "em_code ='"+map.get("ppd_mancode")+"'", Integer.class);
			Object [] detail = baseDao.getFieldsDataByCondition("PREPROJECTDETAIL", new String[]{"ppd_ppid","ppd_detno","ppd_mancode","ppd_man","ppd_taskid"}, "ppd_id ="+id);			
			sqls.add("UPDATE PROJECTTASK SET RESOURCECODE ='"+map.get("ppd_mancode")+"',RESOURCENAME='"+map.get("ppd_man")+"',RESOURCEEMID="+resourceid+" WHERE ID ="+detail[4]);
			sqls.add("UPDATE RESOURCEASSIGNMENT SET RA_RESOURCECODE ='"+map.get("ppd_mancode")+"',RA_RESOURCENAME='"+map.get("ppd_man")+"',RA_EMID="+resourceid+" WHERE RA_TASKID ="+detail[4]);		
			sqls.add(SqlUtil.getUpdateSqlByFormStore(map, "PREPROJECTDETAIL", "ppd_id"));
			baseDao.execute(sqls);
			baseDao.execute("insert into workrecord(wr_id,wr_raid,wr_redcord,wr_recorder,wr_recorderemid,wr_recorddate,wr_status,wr_statuscode,wr_taskpercentdone,wr_percentdone) select workrecord_seq.nextval,"
					+ "ra_id,'【"+ employee.getEm_name()+ "】将任务委托给【"
					+ map.get("ppd_man")
					+ "】','"
					+ employee.getEm_name()
					+ "',"
					+ employee.getEm_id()
					+ ","+DateUtil.parseDateToOracleString(Constant.YMD_HMS,new Date())+",'已审核','AUDITED',100,100 from RESOURCEASSIGNMENT where RA_TASKID="+detail[4]);			
			//记录日志
			baseDao.logger.others("变更责任人", "明细行序号："+detail[1]+",变更成功，原责任人："+detail[2]+" "+detail[3], caller, "pp_id", detail[0]);
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("变更责任人失败！错误："+e.getMessage());
			
		}
	}
}
