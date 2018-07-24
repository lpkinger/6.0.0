package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.fs.LoadedPlanService;

@Service
public class LoadedPlanServiceImpl implements LoadedPlanService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Override
	public List<Map<String, Object>> getLoadedPlans(String pCaller, int pid, String type) {
		List<Map<String, Object>> plans = new ArrayList<Map<String,Object>>();
		String sql = "select PSD_CALLER,PSD_TYPE,PS_ID from FSLOADEDPLANSETDETAIL,FSLOADEDPLANSET where PSD_PSID = PS_ID and PS_CALLER = ?";
		if (StringUtil.hasText(type)) {
			sql += " and nvl(PS_TYPE,' ') = '"+type+"'";
		}else{
			sql += " and nvl(PS_TYPE,' ') = ' '";
		}
		sql += " order by PSD_DETNO";
		
		SqlRowList rs = baseDao.queryForRowSet(sql, pCaller);
		while (rs.next()) {
			Map<String, Object> plan = new HashMap<String, Object>();
			String caller = rs.getGeneralString("psd_caller");
			Integer id = baseDao.getFieldValue("FSLOADEDPLANTABLE", "pt_id", "pt_caller = '"+caller+"' and pt_pid = "+pid+" and pt_psid = '"+rs.getGeneralInt("ps_id")+"'", Integer.class);
			plan.put("psid", rs.getGeneralInt("ps_id"));
			plan.put("caller", caller);
			plan.put("title", rs.getGeneralString("psd_type"));
			plan.put("id", id);
			plans.add(plan);
		}
		return plans;
	}
	
	private void addAtaches(Object id){
		Object attach = baseDao.getFieldDataByCondition("FSLOADEDPLANSET inner join FSLOADEDPLANTABLE on PT_PSID = PS_ID", "PS_ATTACHES", "pt_id = "+id);
		if (attach!=null) {
			baseDao.updateByCondition("FSLOADEDPLANTABLE", "pt_attach = '"+attach+"'", "pt_id = "+id);
		}
	}
	
	@Override
	public void saveLoadedPlan(String formStore, String param2, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Map<Object, Object> extra = BaseUtil.parseFormStoreToMap(param2);
		store.putAll(extra);
		
		handlerService.handler(caller, "save", "before", new Object[] { store });
		store.put("pt_caller", caller);
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FSLOADEDPLANTABLE"));
		
		//加入附件
		addAtaches(store.get("pt_id"));
		baseDao.logger.save(caller, "pt_id", store.get("pt_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateLoadedPlan(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		Object status = baseDao.getFieldDataByCondition("FSLOADEDPLANTABLE", "pt_statuscode", "pt_id=" + store.get("pt_id"));
		StateAssert.updateOnlyEntering(status);
		
		handlerService.handler(caller, "save", "before", new Object[] { store });
		
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FSLOADEDPLANTABLE", "pt_id"));
		
		//加入附件
		addAtaches(store.get("pt_id"));
		
		// 记录操作
		baseDao.logger.update(caller, "pt_id", store.get("pt_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	/*@Override
	public void deleteLoadedPlan(int pt_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { pt_id });
		
		// 删除主表内容
		baseDao.deleteById("FSLOADEDPLANTABLE", "pt_id", pt_id);
		
		baseDao.logger.delete(caller, "pt_id", pt_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { pt_id });
	}
*/
	void checkNeedRemind(Object [] p){
		if (p!=null) {
			String backdate = baseDao.queryForObject("select to_char(lrd_backdate,'yyyy-mm-dd') from FSLOADEDREMINDDET where lrd_id = ?" , String.class, p[0]);
			if (backdate!=null) {
				String sql = "select count(1) from FSLOADEDPLANSET inner join FSLOADEDPLANSETDETAIL "
						+ "on PSD_PSID = PS_ID left JOIN FSLOADEDPLANTABLE on psd_caller = PT_CALLER and ps_id = PT_PSID "
						+ "and pt_pid = "+p[0]+" WHERE DATEADD('D',NVL(PSD_REMDAYS,0),TRUNC(SYSDATE))>=to_date('"+backdate+"','yyyy-mm-dd') "
								+ "and ps_id = "+p[1]+" and nvl(pt_statuscode,' ') not in ('COMMITED','AUDITED')";
				int count = baseDao.getCount(sql);
				if (count>0) {
					baseDao.updateByCondition("FSLOADEDREMINDDET", "LRD_NEEDREM = '需提醒'", "lrd_id = "+p[0]);
				}else {
					baseDao.updateByCondition("FSLOADEDREMINDDET", "LRD_NEEDREM = null", "lrd_id = "+p[0]);
				}
			}
			Object  lrid = baseDao.getFieldDataByCondition("FSLOADEDREMINDDET", "lrd_lrid", "lrd_id = "+p[0]);
			baseDao.execute("update FSLOADEDREMIND set LR_NEEDREM = case when EXISTS (select 1 from FSLOADEDREMINDDET where NVL(LRD_NEEDREM,' ')<>' ' and LRD_LRID = LR_ID) then '需提醒' else null end where lr_id = " + lrid); 
		}
	}
	
	@Override
	public void submitLoadedPlan(int pt_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FSLOADEDPLANTABLE", "pt_statuscode", "pt_id=" + pt_id);
		StateAssert.submitOnlyEntering(status);
		
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { pt_id });
		// 执行提交操作
		baseDao.submit("FSLOADEDPLANTABLE", "pt_id=" + pt_id, "pt_status", "pt_statuscode");
		
		//更改提醒状态
		Object[] pid = baseDao.getFieldsDataByCondition("FSLOADEDPLANTABLE", new String[] {"pt_pid","pt_psid"}, "pt_id = " + pt_id + " and pt_caller = '" + caller + "'");
		if (pid!=null&&pid[0]!=null&&baseDao.checkIf("FSLOADEDREMINDDET", "lrd_id = "+pid[0])) {
			int count = baseDao.getCountByCondition("FSLOADEDPLANTABLE", "pt_pid = "+pid[0]+" and pt_psid = '"+pid[1]+"' and pt_statuscode in ('COMMITED','AUDITED')");
			int pcount = baseDao.getCountByCondition("FSLOADEDPLANSETDETAIL", "psd_psid = "+pid[1]);
			if (count<pcount) {
				baseDao.updateByCondition("FSLOADEDREMINDDET", "lrd_status = '提醒中'", "lrd_id = "+pid[0]);			
			}else {
				baseDao.updateByCondition("FSLOADEDREMINDDET", "lrd_status = '已提醒'", "lrd_id = "+pid[0]);
			}
			checkNeedRemind(pid);
		}
		
		// 记录操作
		baseDao.logger.submit(caller, "pt_id", pt_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { pt_id });
	}

	@Override
	public void resSubmitLoadedPlan(int pt_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FSLOADEDPLANTABLE", "pt_statuscode", "pt_id=" + pt_id);
		StateAssert.resSubmitOnlyCommited(status);
		
		handlerService.handler(caller, "resCommit", "before", new Object[] { pt_id });
		
		// 执行反提交操作
		baseDao.resOperate("FSLOADEDPLANTABLE", "pt_id=" + pt_id, "pt_status", "pt_statuscode");
		
		//更改提醒状态
		Object[] pid = baseDao.getFieldsDataByCondition("FSLOADEDPLANTABLE", new String[] {"pt_pid","pt_psid"}, "pt_id = " + pt_id + " and pt_caller = '" + caller + "'");
		if (pid!=null&&pid[0]!=null&&baseDao.checkIf("FSLOADEDREMINDDET", "lrd_id = "+pid[0])) {
			int count = baseDao.getCountByCondition("FSLOADEDPLANTABLE", "pt_pid = "+pid[0]+" and pt_psid = '"+pid[1]+"' and pt_statuscode in ('COMMITED','AUDITED')");
			if (count==0) {
				baseDao.updateByCondition("FSLOADEDREMINDDET", "lrd_status = '未提醒'", "lrd_id = "+pid[0]);			
			}else {
				baseDao.updateByCondition("FSLOADEDREMINDDET", "lrd_status = '提醒中'", "lrd_id = "+pid[0]);
			}
			checkNeedRemind(pid);
		}
		// 记录操作
		baseDao.logger.resSubmit(caller, "pt_id", pt_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { pt_id });
	}

	@Override
	public void auditLoadedPlan(int pt_id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("FSLOADEDPLANTABLE", "pt_statuscode", "pt_id=" + pt_id);
		StateAssert.auditOnlyCommited(status);
		
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { pt_id });

		baseDao.audit("FSLOADEDPLANTABLE", "pt_id=" + pt_id, "pt_status", "pt_statuscode", "pt_auditdate", "pt_auditman");
		
		// 记录操作
		baseDao.logger.audit(caller, "pt_id", pt_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { pt_id });
	}

	@Override
	public void resAuditLoadedPlan(int pt_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FSLOADEDPLANTABLE", "pt_statuscode", "pt_id=" + pt_id);
		StateAssert.resAuditOnlyAudit(status);
		
		baseDao.resAuditCheck("FSLOADEDPLANTABLE", pt_id);
		handlerService.beforeResAudit(caller, new Object[] { pt_id });
		
		// 执行反审核操作
		baseDao.resAudit("FSLOADEDPLANTABLE", "pt_id=" + pt_id, "pt_status", "pt_statuscode", "pt_auditman", "pt_auditdate");
		
		// 记录操作
		baseDao.logger.resAudit(caller, "pt_id", pt_id);
		handlerService.afterResAudit(caller, new Object[] { pt_id });
	}

}
