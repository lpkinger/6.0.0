package com.uas.erp.service.plm.impl;

import java.util.Date;
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
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.plm.ProjectCloseService;


@Service
public class ProjectCloseServiceImpl implements ProjectCloseService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProjectClose(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(prj_code) from Project where prj_statuscode = 'FINISHED' and prj_code = ?", String.class,store.get("pc_prjcode"));
		if (dets != null) {
			BaseUtil.showError("当前项目已结案，无需再次结案！");
		}
		
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(pc_prjcode) from ProjectClose where pc_prjcode = ?", String.class,store.get("pc_prjcode"));
		if (dets != null) {
			BaseUtil.showError("当前项目已进行项目结案申请，不允许重复申请！");
		}
		String code = baseDao.sGetMaxNumber("ProjectClose", 2);
		store.put("pc_code", code);
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProjectClose", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "pc_id", store.get("pc_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateProjectCloseById(String formStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(prj_code) from Project where prj_statuscode = 'FINISHED' and prj_code = ?", String.class,store.get("pc_prjcode"));
		if (dets != null) {
			BaseUtil.showError("当前项目已结案，无需再次结案！");
		}
		
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wmsys.wm_concat(pc_prjcode) from ProjectClose where pc_id <> ? and pc_prjcode = ?", String.class,store.get("pc_id"),store.get("pc_prjcode"));
		if (dets != null) {
			BaseUtil.showError("当前项目已进行项目结案申请，不允许重复申请！");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProjectClose", "pc_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "pc_id", store.get("pc_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteProjectClose(int pc_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{pc_id});
		//删除主表内容
		baseDao.deleteById("ProjectClose", "pc_id", pc_id);
		baseDao.logger.delete(caller, "pc_id", pc_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{pc_id});
	}

	@Override
	public void submitProjectClose(int pc_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("ProjectClose", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.submitOnlyEntering(status);
		
		//如果是正常结案，则要判断项目阶段计划及项目任务是否都已完成
		checkPhaseAndTaskComplete(pc_id);
		
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pc_id });
		// 执行提交操作
		baseDao.submit("ProjectClose", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pc_id", pc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pc_id });
	}
	
	private void checkPhaseAndTaskComplete(int pc_id){
		SqlRowList rs = baseDao.queryForRowSet("select * from projectclose left join project on prj_code=pc_prjcode where pc_id=" + pc_id);
		if(rs.next()){
			if("正常结案".equals(rs.getString("pc_closetype"))){
				//检查项目是否已启动
				if(!"DOING".equals(rs.getString("prj_statuscode"))){
					BaseUtil.showError("当前项目处于非已启动状态，不能正常结案!");
				}
				int prjId = rs.getInt("prj_id");
				checkTask(prjId);
				//判断子项目
				List<Object[]> prjIds = baseDao.getFieldsDatasByCondition("Project", new String[]{"prj_id"}, "PRJ_MAINPROID="+prjId);
				for(Object[] id:prjIds){ 
					checkTask(Integer.parseInt(id[0].toString()));
				}
			}
		}
	}
	
	private void checkTask(int prjId){
		//检查项目阶段计划是否已完成
		boolean bool = baseDao.checkIf("projectphase", "pp_prjid=" + prjId + " and nvl(pp_status,' ')<>'已完成'");
		if(bool){
			BaseUtil.showError("当前项目的阶段计划未全部完成，不能正常结案！");
		}else{
			//检查项目任务是否全部完成
			boolean bl = baseDao.checkIf("projecttask", "nvl(parentid,0)<>0 and parentid in (select id from projecttask where prjplanid="+prjId+") and nvl(handstatuscode,' ')<>'FINISHED' and id not in (select parentid from projecttask where prjplanid="+prjId+")");
			if(bl){
				BaseUtil.showError("当前项目的任务未全部完成，不能正常结案！");
			}
		}
	}
	
	@Override
	public void resSubmitProjectClose(int pc_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("ProjectClose", "pc_statuscode", "pc_id=" + pc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pc_id });
		// 执行反提交操作
		baseDao.resOperate("ProjectClose", "pc_id=" + pc_id, "pc_status", "pc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pc_id", pc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pc_id });
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditProjectClose(int pc_id, String caller) {
		//只能对已提交进行审核操作
		Object[] status = baseDao.getFieldsDataByCondition("ProjectClose", "pc_statuscode,pc_prjcode", "pc_id=" + pc_id);
		StateAssert.auditOnlyCommited(status[0]);
		
		//如果是正常结案，则要判断项目阶段计划及项目任务是否都已完成
		checkPhaseAndTaskComplete(pc_id);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{pc_id});
		//更新项目状态为已结案
		baseDao.updateByCondition("Project", "prj_status='已结案',prj_statuscode='FINISHED',prj_closedate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "prj_code='"+status[1]+"'");
		//更新任务状态为已结案		
		int prjid = baseDao.getFieldValue("Project", "prj_id", "prj_code='"+status[1]+"'", Integer.class);
		baseDao.updateByCondition("resourceassignment", "ra_status='已结案',ra_statuscode='ENDED'", "ra_taskid in (select id from projecttask where prjplanid="+prjid+")");
		baseDao.updateByCondition("ProjectTask", "status='已结案',statuscode='FINISHED'","prjplanid="+prjid);
		//更新子项目为已结案
		List<Object[]> prjIds = baseDao.getFieldsDatasByCondition("Project", new String[]{"prj_id"}, "PRJ_MAINPROID="+prjid);
  		List<String> updateSqls=new LinkedList<String>();
  		for(Object[] id:prjIds){  		//更新子项目状态	
  			updateSqls.add("update Project set prj_status='已结案',prj_statuscode='FINISHED',prj_closedate="+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date())+" where prj_id='"+id[0]+"'");
  			//更新任务状态为已结案		
  			updateSqls.add("update resourceassignment set ra_status='已结案',ra_statuscode='ENDED' where ra_taskid in (select id from projecttask where prjplanid="+id[0]+")");
  			updateSqls.add("update ProjectTask set status='已结案',statuscode='FINISHED' where prjplanid="+id[0]);
  		}
  		baseDao.execute(updateSqls);
  		
		baseDao.audit("ProjectClose", "pc_id=" + pc_id, "pc_status", "pc_statuscode","pc_auditdate","pc_auditman");
		//记录操作
		baseDao.logger.audit(caller, "pc_id", pc_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{pc_id});
	}

	@Override
	public void resAuditProjectClose(int pc_id, String caller) {
		//只能对已审核进行反审核操作
		List<String> updateSqls=new LinkedList<String>();
		Object[] data = baseDao.getFieldsDataByCondition("ProjectClose left join project on pc_prjcode=prj_code", "pc_statuscode,pc_prjcode,prj_id", "pc_id=" + pc_id);
		StateAssert.resAuditOnlyAudit(data[0]);
		String prjcode=data[1].toString();
		//执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[]{pc_id});		
			
		//更新任务明细
		List<Object[]> tasks = baseDao.getFieldsDatasByCondition("projecttask", new String[]{"handstatus","handstatuscode","id"}, "prjplanid="+data[2]);
		for(Object[] task:tasks){
			if("FINISHED".equals(task[1]))
				updateSqls.add("update RESOURCEASSIGNMENT set ra_status='已完成',ra_statuscode='FINISHED' where ra_taskid ="+task[2]);
			else if("DOING".equals(task[1])){	
				List<Object[]> percent = baseDao.getFieldsDatasByCondition("RESOURCEASSIGNMENT",new String[]{"ra_id","ra_taskpercentdone"}, "ra_taskid="+task[2]);
				for(Object[] per:percent){
					if("100".equals(per[1].toString()))
						updateSqls.add("update RESOURCEASSIGNMENT set ra_status='已完成',ra_statuscode='FINISHED' where ra_id ="+per[0]);
					else
						updateSqls.add("update RESOURCEASSIGNMENT set ra_status='进行中',ra_statuscode='START' where ra_id="+per[0]);
				}
			}
			else
				updateSqls.add("update RESOURCEASSIGNMENT set ra_status='未激活',ra_statuscode='UNACTIVE' where ra_taskid ="+task[2]);			
		}
		updateSqls.add("update projecttask set status='已审核',statuscode='AUDITED' where prjplanid="+data[2]);
				
		//更新项目状态
		Object status = baseDao.getFieldDataByCondition("projectmaintask","pt_statuscode", "pt_prjcode='"+prjcode+"'");
		if(status!=null&&"DOING".equals(status.toString()))
			updateSqls.add("update project set prj_status='已启动',prj_statuscode='DOING',prj_closedate=to_date('','yyyy-mm-dd hh24:mi:ss') where prj_code='"+prjcode+"'");
		else
			updateSqls.add("update project set prj_status='未启动',prj_statuscode='UNDOING',prj_closedate=to_date('','yyyy-mm-dd hh24:mi:ss') where prj_code='"+prjcode+"'");
		baseDao.execute(updateSqls);
		baseDao.resAudit("projectclose", "pc_id="+pc_id, "pc_status", "pc_statuscode", "pc_auditdate", "pc_auditman");
		//记录操作
		baseDao.logger.resAudit(caller, "pc_id", pc_id);
		//执行审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { pc_id });
	}
}
