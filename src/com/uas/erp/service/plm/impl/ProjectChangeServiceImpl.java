package com.uas.erp.service.plm.impl;

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
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.plm.ProjectChangeService;

@Service
public class ProjectChangeServiceImpl implements ProjectChangeService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProjectChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		//检查是否有未审核的项目变更申请单
		boolean bool = baseDao.checkIf("projectchange", "pc_oldprjcode='"+store.get("pc_oldprjcode")+"' and pc_statuscode<>'AUDITED'");
		if(bool){
			BaseUtil.showError("一个项目只能存在一张未审核的项目变更申请单!");
		}
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "ProjectChange"));
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateProjectChange(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		Object projectCode= store.get("pc_oldprjcode");
		//查看项目申请单的状态，如果是非已启动，暂停中，未启动，则不进行更新 
		Object projectStatusCode = baseDao.getFieldDataByCondition("project", "prj_statuscode", "prj_code='" + projectCode + "'");
		if("UNDOING".equals(projectStatusCode)||"DOING".equals(projectStatusCode)||"STOP".equals(projectStatusCode)){
			Object newStatus = store.get("pc_changetype");
			if("UNDOING".equals(projectStatusCode)){				
				if("已启动".equals(newStatus)){
					BaseUtil.showError("项目状态为未启动，不允许变更项目状态为已启动");
				}
			}else if("STOP".equals(projectStatusCode)){
				if("未启动".equals(newStatus)){
					boolean bool = baseDao.checkIf("projectmaintask", "pt_prjcode='" + projectCode + "' and pt_statuscode='DOING'");
					if(bool){
						BaseUtil.showError("项目已存在已启动的任务书，不允许变更为未启动");
					}
				}
			}
		}else{
			BaseUtil.showError("只有未启动，已启动或暂停中的项目才能变更！");
		}
				
		handlerService.handler(caller, "update", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "ProjectChange", "pc_id"));
		handlerService.handler(caller, "update", "after", new Object[] { store });
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
	}

	@Override
	public void deleteProjectChange(int id, String caller) {
		handlerService.handler(caller, "delete", "before", new Object[] { id });
		// 删除ProjectChange
		baseDao.deleteById("ProjectChange", "pc_id", id);
		// 记录操作
		baseDao.logger.delete(caller, "pc_id", id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { id });
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditProjectChange(int id, String caller) {
		List<String> sqls = new ArrayList<String>();
		
		SqlRowList uprs = baseDao.queryForRowSet("select * from projectchange left join project on prj_code=pc_oldprjcode where pc_id=?",id);
		StringBuffer sb = new StringBuffer();
		sb.append("update project set ");
		while(uprs.next()){
			String prjcode = uprs.getString("pc_oldprjcode");
			String oldname = uprs.getString("pc_oldprjname");
			String newname = uprs.getString("pc_name");
			String oldstart = uprs.getString("pc_oldstartdate");
			String newstart = uprs.getString("pc_newstartdate");
			String oldend = uprs.getString("pc_oldenddate");
			String newend = uprs.getString("pc_newenddate");
			String oldstatus = uprs.getString("pc_prjstatus");
			String newstatus = uprs.getString("pc_changetype");
			
			if(!oldname.equals(newname)&&newname!=null&&!"".equals(newname)){
				//检查新项目名称是否重复
				boolean reName = baseDao.checkIf("project", "prj_name='"+newname+"' and prj_code<>'"+prjcode+"'");
				if(reName){
					BaseUtil.showError("新项目名称已存在，请重新修改");
				}else{				
					//更新项目名称
					sb.append("prj_name='"+newname+"',");
					
					//更新任务的名称 
					sqls.add("update projectmaintask set pt_prjname='" + newname + "' where pt_prjid=" + uprs.getString("prj_id"));
					sqls.add("update resourceassignment set ra_prjname='" + newname + "' where ra_prjid=" + uprs.getString("prj_id"));
					sqls.add("update team set team_prjname='" + newname + "'  where team_prjid=" + uprs.getString("prj_id"));
					sqls.add("update projecttask set prjplanname='" + newname + "' where prjplanid=" + uprs.getString("prj_id"));					
				}
			}
			
			if(newstart!=null&&!"".equals(newstart)){
				if(!oldstart.equals(newstart)){
					if(newstart.length()>10){
						newstart = newstart.substring(0,10);
					}
					sb.append("prj_start=to_date('" + newstart + "','yyyy-mm-dd'),");
				}
			}
			
			if(newend!=null&&!"".equals(newend)){
				if(!oldend.equals(newend)){
					if(newend.length()>10){
						newend = newend.substring(0,10);
					}
					sb.append("prj_end=to_date('" + newend + "','yyyy-mm-dd'),");
				}
			}
			
			if(newstatus!=null&&!"".equals(newstatus)&&!"不变".equals(newstatus)){
				if(oldstatus==null){
					oldstatus = "";
				}
				if(!oldstatus.equals(newstatus)){
					//查看项目申请单的状态，如果是非已启动，暂停中，未启动，则不进行更新 
					Object[] projectStatusCode = baseDao.getFieldsDataByCondition("project left join projectmaintask on prj_code=pt_prjcode", new String[]{"prj_statuscode","pt_statuscode"}, "prj_code='" + prjcode + "'");
					if(!"UNDOING".equals(projectStatusCode[0])&&!"DOING".equals(projectStatusCode[0])&&!"STOP".equals(projectStatusCode[0])){
						BaseUtil.showError("只有未启动，已启动或暂停中的项目才能变更！");
					}	
					
					//更新项目申请单和任务书的状态
					String changeStatus = "";
					String changeStatusCode = "";
					String changeTaskStatus = "";
					String changeTaskStatusCode = "";
					if("重启".equals(newstatus)&&"DOING".equals(projectStatusCode[1])){ //要变更的项目有已启动的任务书，则项目变更为已启动
						changeStatus = "已启动";
						changeStatusCode = "DOING";
						changeTaskStatusCode = "AUDITED";
						changeTaskStatus = "已审核";
					}else if("暂停".equals(newstatus)){
						changeStatus = "暂停中";
						changeStatusCode = "STOP";
						changeTaskStatus = "暂停中";
						changeTaskStatusCode = "STOP";		
					}else if("重启".equals(newstatus)&&!"DOING".equals(projectStatusCode[1])){ //要变更的项目有已启动的任务书，则项目变更为已启动
						changeStatus = "未启动";
						changeStatusCode = "UNDOING";		
					}
					
					sb.append("prj_status='" + changeStatus + "',prj_statuscode='" + changeStatusCode + "',");
					
					//更新任务书中的任务的状态(projecttask)
					String projectUpdate = "update project set prj_status='" + changeStatus + "',prj_statuscode='" + changeStatusCode + "' where prj_code='" + prjcode + "'";
					sqls.add(projectUpdate);
					Object ptid = baseDao.getFieldDataByCondition("(projectmaintask left join project on prj_code=pt_prjcode)", "pt_id", "pt_prjcode='"+prjcode+"'");
					String taskUpdate = "update projecttask set status='" + changeTaskStatus + "',statuscode='" + changeTaskStatusCode + "' where ptid="+(ptid==null?-1:ptid) + " or prjplanid=(select prj_id from project where prj_code='"+prjcode+"')";		
					sqls.add(taskUpdate);						

			}
		}
			if(sb.toString().endsWith(",")){
				String update = sb.substring(0,sb.lastIndexOf(",")) + " where prj_code='"+prjcode+"'";
				sqls.add(update);
			}
			sb.setLength(0);
		}
		SqlRowList checkrs = baseDao.queryForRowSet("select * from ProjectChange where pc_id=?",id);
		if(checkrs.next()){
			if(checkrs.getString("pc_newassignto")!=null&&!checkrs.getString("pc_newassignto").equals(checkrs.getString("pc_oldassignto"))){
				baseDao.updateByCondition("Project", "prj_assigntocode='"+checkrs.getString("pc_newassigntocode")+"',prj_assignto='"+checkrs.getString("pc_newassignto")+"'", "prj_code='"+checkrs.getString("pc_oldprjcode")+"'");
				baseDao.updateByCondition("ProjectMainTask", "pt_orger='"+checkrs.getString("pc_newassignto")+"'", "pt_prjcode='"+checkrs.getString("pc_oldprjcode")+"'");
			}
		}
		baseDao.execute(sqls);
		
		Object status = baseDao.getFieldDataByCondition("projectchange", "pc_statuscode", "pc_id=" + id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { id });
		// 执行审核操作
		baseDao.audit("ProjectChange", "pc_id=" + id, "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditer");
		baseDao.logger.audit(caller, "pc_id", id);
		
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { id });
	}

	@Override
	public void submitProjectChange(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectChange", "pc_statuscode", "pc_id=" + id);
		StateAssert.submitOnlyEntering(status);
		handlerService.handler(caller, "commit", "before", new Object[] { id });
		// 执行反审核操作
		baseDao.submit("ProjectChange", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", id);
		handlerService.handler(caller, "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitProjectChange(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectChange", "pc_statuscode", "pc_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		handlerService.handler(caller, "resCommit", "before", new Object[] { id });
		baseDao.resOperate("ProjectChange", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { id });
	}

	@Override
	public void resAuditProjectChange(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("ProjectChange", "pc_statuscode", "pc_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.handler(caller, "resAudit", "before", new Object[] { id });
		// 执行反审核操作
		baseDao.resOperate("ProjectChange", "pc_id=" + id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pc_id", id);
		handlerService.handler(caller, "resAudit", "after", new Object[] { id });
	}

}