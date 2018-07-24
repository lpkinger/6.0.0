package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Status;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.SYSJobService;

@Service("SYSJobService")
public class SYSJobServiceImpl implements SYSJobService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	

	@Override
	public void saveSYSJob(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);  
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SYSJob", "sj_code='" + store.get("sj_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		} 
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[] { store,gstore});
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SYSJob", new String[] {}, new Object[] {});
		baseDao.execute(formSql); 
		try {
			// 记录操作
			baseDao.logger.save(caller, "sj_id", store.get("sj_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[] { store,gstore});
	}

	@Override
	public void deleteSYSJob(int sj_id, String caller) {
		// 只能删除在录入的SYSJob
		Object status = baseDao.getFieldDataByCondition("SYSJob", "sj_statuscode", "sj_id=" + sj_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] { sj_id });
		// 删除SYSJob
		baseDao.deleteById("SYSJob", "sj_id", sj_id); 
		// 记录操作
		baseDao.logger.delete(caller, "sj_id", sj_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { sj_id });
	}

	@Override
	public void updateSYSJobById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore); 
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Object status = baseDao.getFieldDataByCondition("SYSJob", "sj_statuscode", "sj_id=" + store.get("sj_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[] { store,gstore});
		//执行更新
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SYSJob", "sj_id");
		baseDao.execute(formSql); 
		// 记录操作
		baseDao.logger.update(caller, "sj_id", store.get("sj_id")); 
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[] { store,gstore});
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public void auditSYSJob(int sj_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SYSJob", "sj_statuscode", "sj_id=" + sj_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { sj_id});
		// 执行审核操作
		baseDao.audit("SYSJob", "sj_id=" + sj_id, "sj_status", "sj_statuscode", "sj_auditdate", "sj_auditman");
		//执行测试和脚本创建
		this.testOracleJob(sj_id,caller);
		//this.enableOracleJob(sj_id,caller);
		// 记录操作
		baseDao.logger.audit(caller, "sj_id", sj_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { sj_id});
	}

	@Override
	public void resAuditSYSJob(int sj_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SYSJob", "sj_statuscode", "sj_id=" + sj_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, new Object[] { sj_id});
		// 执行反审核操作
		Object [] obs = baseDao.getFieldsDataByCondition("SYSJob", new String[]{"sj_statuscode","sj_jobname"}, "sj_id=" + sj_id);
		if(obs[1] != null && !obs[1].equals("")){
			//判断job是否存在USER_SCHEDULER_JOBS表中，如果不存在的话就不执行stop语句，针对问题反馈2017010401
			int cn=baseDao.getCount("select count(1) from USER_SCHEDULER_JOBS where job_name='"+obs[1].toString()+"'");
			if(cn>0){
				this.stopOracleJob(sj_id,caller);
			}
			baseDao.resOperate("SYSJob", "sj_id=" + sj_id, "sj_status", "sj_statuscode");
		}
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, new Object[] { sj_id});
		// 记录操作
		baseDao.logger.resAudit(caller, "sj_id", sj_id);
	}

	@Override
	public void submitSYSJob(int sj_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SYSJob", "sj_statuscode", "sj_id=" + sj_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { sj_id });
		// 执行提交操作
		baseDao.submit("SYSJob", "sj_id=" + sj_id, "sj_status", "sj_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sj_id", sj_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { sj_id });
	}

	@Override
	public void resSubmitSYSJob(int sj_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SYSJob", "sj_statuscode", "sj_id=" + sj_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller,  new Object[] { sj_id });
		// 执行反提交操作
		baseDao.resOperate("SYSJob", "sj_id=" + sj_id, "sj_status", "sj_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sj_id", sj_id);
		handlerService.afterResSubmit(caller,  new Object[] { sj_id });
	}
	@Override
	public void testOracleJob(int sj_id, String caller) {   
		//执行job创建或更新存储过程 
		String str = baseDao.callProcedure("SYS_SETORAJOB", new Object[] { sj_id, SystemSession.getUser().getEm_name() });
		if (str != null && !str.trim().equals("")) {
			BaseUtil.showError(str);
		} 
		// 记录操作
		baseDao.execute(baseDao.logger.getMessageLog("测试", "测试通过", caller, "sj_id", sj_id).getSql());
	}

	@Override
	public void runOracleJob(int id, String caller) {
		//立即执行JOB
		Object [] obs = baseDao.getFieldsDataByCondition("SYSJob", new String[]{"sj_statuscode","sj_jobname"}, "sj_id=" + id);		
		if(obs[0] != null && !"AUDITED".equals(obs[0])){
			BaseUtil.showError("请先审核JOB！");
		}	
		if(obs[1] != null && !obs[1].equals("")){
			//针对问题反馈2016120424，判断在数据表中是否存在不存在的话创建   @date 2016-12-15   14:27
			int cn = baseDao.getCount("select count(1) from USER_SCHEDULER_jobs where job_name='"+obs[1]+"'");
			if(cn==0){
				testOracleJob(id,caller);
			}			
			baseDao.procedure("dbms_scheduler.run_job", new Object[]{obs[1].toString()});
			baseDao.execute(baseDao.logger.getMessageLog("立即执行", "立即执行成功", caller, "sj_id", id).getSql());
		}else{
			BaseUtil.showError("任务名称不允许为空！");
		}
	}
	
	//goua stop和enable主要是针对问题反馈2016120289，按照需求添加按钮
	@Override
	public void stopOracleJob(int id, String caller) {
		//停止执行JOB
		Object [] obs = baseDao.getFieldsDataByCondition("SYSJob", new String[]{"sj_statuscode","sj_jobname"}, "sj_id=" + id);
		if(obs[0] != null && !"AUDITED".equals(obs[0])){
			BaseUtil.showError("请先审核JOB！");
		}	
		if(obs[1] != null && !obs[1].equals("")){
			baseDao.procedure("dbms_scheduler.DISABLE", new Object[]{obs[1].toString()});
			baseDao.banned("SYSJob", "sj_id=" + id, "sj_status", "sj_statuscode");
			baseDao.execute("update SYSJob set sj_jobenable = 0 where sj_id=" + id);
			baseDao.logger.banned(caller, "sj_id", id);
		}else{
			BaseUtil.showError("任务名称不允许为空！");
		}
	}

	@Override
	public void enableOracleJob(int id, String caller) {
		//启用JOB
		Object [] obs = baseDao.getFieldsDataByCondition("SYSJob", new String[]{"sj_statuscode","sj_jobname"}, "sj_id=" + id);
		if(obs[1] != null && !obs[1].equals("")){
			baseDao.procedure("dbms_scheduler.enable", new Object[]{obs[1].toString()});
			baseDao.resOperate("SYSJob", "sj_id=" + id, "sj_status", "sj_statuscode");
			baseDao.execute("update SYSJob set sj_jobenable = -1 where sj_id=" + id);
			baseDao.logger.resBanned(caller, "sj_id", id);
		}else{
			BaseUtil.showError("任务名称不允许为空！");
		}
	}

	@Override
	public List<Map<String, Object>> getOracleJob(int start, int end) {
		String sql = "select sj_id  id,'pm/mps/runOracleJob.action?caller=\"SYSJob\"'||chr(38)||'&id='||sj_id action,SJ_JOBNAME||SJ_JOBCOMMENTS VALUE from SYSJob where sj_statuscode='AUDITED' and sj_jobenable=-1 order by sj_id desc ";
		String pagingsql = "SELECT * "+
				"FROM(SELECT ROWNUM rn,t.* "+
				"     FROM("+sql+") t"+
				"     WHERE ROWNUM<="+end+")"+
				"WHERE rn>"+start;
		return baseDao.queryForList(pagingsql);
	}

	@Override
	public int getCountOracleJob() {
		return baseDao.getCount("select count(*) from sysjob where sj_statuscode='AUDITED' and sj_jobenable=-1");
	}
}