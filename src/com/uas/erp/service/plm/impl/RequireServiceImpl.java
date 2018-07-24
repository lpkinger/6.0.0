package com.uas.erp.service.plm.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.RequireService;


@Service
public class RequireServiceImpl implements RequireService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveRequire(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});		
		int i = baseDao.getCountByCondition("PRJREQUEST", "pr_name='"+store.get("pr_name")+"'");
		if(i>0){//#maz 需求单名称不可重复
			BaseUtil.showError("该需求单名称已存在:"+ 
					"<a href=\"javascript:openUrl('jsps/plm/request/require.jsp?formCondition=pr_nameIS" + store.get("pr_name") +  "')\">" + store.get("pr_name") + "</a>&nbsp;");
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PRJREQUEST", new String[]{}, new Object[]{});
		baseDao.execute(formSql);	
		baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		//执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void updateRequireById(String formStore,String caller) {		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[]{store});
		int i = baseDao.getCountByCondition("PRJREQUEST", "pr_name='"+store.get("pr_name")+"' and pr_id<>"+store.get("pr_id")+"");
		if(i>0){//#maz 需求单名称不可重复，排除本身的需求名称判断是否有重复
			BaseUtil.showError("该需求单名称已存在:"+ 
					"<a href=\"javascript:openUrl('jsps/plm/request/require.jsp?formCondition=pr_nameIS" + store.get("pr_name") +  "')\">" + store.get("pr_name") + "</a>&nbsp;");
		}
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PRJREQUEST", "pr_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		//执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[]{store});
	}

	@Override
	public void deleteRequire(int pr_id, String caller) {		
		//执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[]{pr_id});
		//删除主表内容
		baseDao.deleteById("PRJREQUEST", "pr_id", pr_id);
		baseDao.logger.delete(caller, "pr_id", pr_id);
		//执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[]{pr_id});
	}

	@Override
	public void auditRequire(int pr_id, String caller) {
		//只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("PRJREQUEST", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[]{pr_id});
		baseDao.audit("PRJREQUEST", "pr_id=" + pr_id, "pr_status", "pr_statuscode", "pr_auditdate", "pr_auditor");
		//记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		//执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[]{pr_id});
	}
	@Override
	public void submitRequire(int pr_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PRJREQUEST", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pr_id });
		// 执行提交操作
		baseDao.submit("PRJREQUEST", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pr_id", pr_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pr_id });
	}
	@Override
	public void resSubmitRequire(int pr_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PRJREQUEST", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { pr_id });
		// 执行反提交操作
		baseDao.resOperate("PRJREQUEST", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pr_id", pr_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pr_id });
	}
	@Override
	public void resAuditRequire(int pr_id, String caller) {
		// 只能对状态为[已审核]的表单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PRJREQUEST", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resAuditOnlyAudit(status);
		Object ob = baseDao.getFieldDataByCondition("PRJREQUEST", "pr_auditstatus", "pr_id=" + pr_id);
		if(ob!=null&&!"".equals(ob)){ //maz 更新转单的方法
			BaseUtil.showError("当前单据已经转单，不允许反审核");
		}
		// 执行反审核操作
		baseDao.resAudit("PRJREQUEST", "pr_id=" + pr_id, "pr_status", "pr_statuscode", "pr_auditdate", "pr_auditor");
		baseDao.resOperate("PRJREQUEST", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pr_id", pr_id);
	}
	/**
	 *  转立项
	 */
	@Override
	public int turnProject(String caller, int pr_id) {
		
		Object ob[] = baseDao.getFieldsDataByCondition("PRJREQUEST", "pr_custcode,pr_custname,pr_code", "pr_id="+pr_id);
		Object b[] = baseDao.getFieldsDataByCondition("PROJECT", "prj_id,prj_code", "prj_sourcecode='"+ob[2]+"' and prj_sourcetype ='需求单'");
		if(b!=null){
			BaseUtil.showError("该需求单已经转过立项，不能重复转"
					+ "<a href=\"javascript:openUrl('jsps/plm/request/ProjectRequest.jsp?whoami=ProjectRequest&formCondition=prj_idIS" + b[0]
					+ "&gridCondition=pp_prjidIS" + b[0] + "')\">" + b[1] + "</a>&nbsp;");
		}
		Object a[] = baseDao.getFieldsDataByCondition("PREPROJECT", "pp_id,pp_code", "pp_prcode='"+ob[2]+"'");
		if(a!=null){
			BaseUtil.showError("该需求单已经转过预立项，不能重复转!");
		}
		int prid = baseDao.getSeqId("PROJECT_SEQ");
		String prjcode = baseDao.sGetMaxNumber("PROJECT", 2);
		Employee employee = SystemSession.getUser();
		String sql = "insert into PROJECT (prj_id,prj_customercode,prj_customername,prj_sourcecode,prj_person,prj_recordate,prj_sourcetype,prj_statuscode,prj_status,prj_code,prj_auditstatus,prj_auditstatuscode,prj_class,prj_prstatus,prj_prstatuscode,prj_turnstatus,prj_isturnpro) values ('" + prid + "','"+ob[0]+"','"+ob[1]+"','"+ob[2]+"','"
				+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'需求单','UNDOING','未启动','"+prjcode+"','在录入','ENTERING','立项申请书','正式立项','normaltask',0,0)";
		baseDao.execute(sql);
		baseDao.updateByCondition("PRJREQUEST", "pr_auditstatus='转立项'", "pr_id="+pr_id);
		
		BaseUtil.appendError("生成成功,立项单号:" + "<a href=\"javascript:openUrl('jsps/plm/request/ProjectRequest.jsp?whoami=ProjectRequest&formCondition=prj_idIS"+prid+"&gridCondition=pp_prjidIS"+prid+"')\">" + prjcode + "</a>&nbsp;");
		return prid;
	}
	/**
	 *  转预立项
	 */
	@Override
	public int turnPrepProject(String caller, int pr_id) {
	
		
		Object ob[] = baseDao.getFieldsDataByCondition("PRJREQUEST", "pr_custcode,pr_custname,pr_code", "pr_id="+pr_id);
		Object b[] = baseDao.getFieldsDataByCondition("PROJECT", "prj_id,prj_code", "prj_sourcecode='"+ob[2]+"' and prj_sourcetype ='需求单'");
		if(b!=null){
			BaseUtil.showError("该需求单已经转过预立项，不能重复转"
					+ "<a href=\"javascript:openUrl('jsps/plm/request/ProjectRequest.jsp?whoami=PreProject&formCondition=prj_idIS" + b[0]
					+ "&gridCondition=pp_prjidIS" + b[0] + "')\">" + b[1] + "</a>&nbsp;");
		}
		
		int prid = baseDao.getSeqId("PROJECT_SEQ");
		String prjcode = baseDao.sGetMaxNumber("PROJECT", 2);
		Employee employee = SystemSession.getUser();
		String sql = "insert into PROJECT (prj_id,prj_customercode,prj_customername,prj_sourcecode,prj_person,prj_recordate,prj_sourcetype,prj_statuscode,prj_status,prj_code,prj_preaudit,prj_preauditcode,prj_class,prj_prstatus,prj_prstatuscode,prj_turnstatus,prj_isturnpro) values ('" + prid + "','"+ob[0]+"','"+ob[1]+"','"+ob[2]+"','"
				+ employee.getEm_name() + "'," + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()) + ",'需求单','UNDOING','未启动','"+prjcode+"','在录入','ENTERING','立项申请书','预立项','pretask',0,0)";
		baseDao.execute(sql);
		baseDao.updateByCondition("PRJREQUEST", "pr_auditstatus='转预立项'", "pr_id="+pr_id);
		
		BaseUtil.appendError("生成成功,立项单号:" + "<a href=\"javascript:openUrl('jsps/plm/request/ProjectRequest.jsp?whoami=PreProject&formCondition=prj_idIS"+prid+"&gridCondition=pp_prjidIS"+prid+"')\">" + prjcode + "</a>&nbsp;");
		return prid;
	}
}
