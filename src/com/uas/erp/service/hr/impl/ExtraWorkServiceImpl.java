package com.uas.erp.service.hr.impl;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.jbpm.pvm.internal.xml.Parse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.hr.ExtraWorkService;

@Service("extraWorkServiceImpl")
public class ExtraWorkServiceImpl implements ExtraWorkService{
	@Autowired
	HandlerService handlerService;
	@Autowired
	BaseDao baseDao;
	@Override
	public void saveExtraWork(HttpSession session,String formStore, String caller) {
		Map<Object,Object> map = BaseUtil.parseFormStoreToMap(formStore);
		checkTime(map);
		Object wo_worktask = map.get("wo_worktask");
		map.remove("wo_worktask");
		handlerService.beforeSave(caller, new Object[]{map});
		int wo_id = baseDao.getSeqId("WORKOVERTIME_SEQ");
		String wo_code = baseDao.sGetMaxNumber("WORKOVERTIME", 2);
		String sql = "insert into Workovertime(wo_id,wo_code,wo_status,wo_statuscode,wo_worktask,wo_job,wo_hrorg,wo_depart,wo_emcode,wo_recorder,wo_recorddate) values("+wo_id+",'"+wo_code+"','在录入','ENTERING','"+wo_worktask+"','"+session.getAttribute("em_position")+"','"+session.getAttribute("em_defaultorname")+"','"+session.getAttribute("em_depart")+"','"+session.getAttribute("em_code")+"','"+session.getAttribute("em_name")+"',sysdate)";
		baseDao.execute(sql);
		map.put("wod_woid", wo_id);
		/**
		 * @author wsy
		 * 加班申请单计算时长。
		 */
		if(map.get("wod_count")==null || "0".equals(map.get("wod_count")) ||"".equals(map.get("wod_count"))){
			DecimalFormat df = new DecimalFormat("#.00");
			double wod_enddate = (double)DateUtil.parse(map.get("wod_enddate").toString(),"yyyy-MM-dd HH:mm:ss").getTime();
			double wod_startdate = (double)DateUtil.parse(map.get("wod_startdate").toString(),"yyyy-MM-dd HH:mm:ss").getTime();
			double time = (wod_enddate-wod_startdate)/(1000*60*60);
			map.remove("wod_count");
			map.put("wod_count", df.format(time));
		}
		String formSql = SqlUtil.getInsertSqlByFormStore(map, "Workovertimedet",new String[]{}, new Object[] {});
		baseDao.execute(formSql);
		//计算加班时数
		if (baseDao.isDBSetting(caller,"autoupdateWodcount")) {
			baseDao.callProcedure("sp_Workovertime_com", new Object[] {wo_id});
		}
		try {
			// 记录操作
			baseDao.logger.save(caller, "wod_id", map.get("wod_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public Map<String, Object> ExtraWorkSaveAndSubmit(HttpSession session,String caller,String formStore,Employee employee){		
			int wod_id=baseDao.getSeqId("Workovertimedet_seq");			
			Map<String, Object> modelMap = new HashMap<String, Object>();
			 Map<Object,Object> store=BaseUtil.parseFormStoreToMap(formStore);
			 store.put("wod_empname", employee.getEm_name());
			 store.put("wod_empcode", employee.getEm_code());
			 store.put("wod_id", wod_id);
			 String map=BaseUtil.parseMap2Str(store);
			saveExtraWork(session,map,caller);
			submitExtraWork(wod_id, caller);
			modelMap.put("wod_id",wod_id);
			return modelMap;
		
	}
	
	@Override
	public void ExtraWorkUpdateAndSubmit(String caller,String formStore){									
			// formStore+=","+"wod_id="+wod_id+"";
			 Map<Object,Object> store=BaseUtil.parseFormStoreToMap(formStore);
			 int id=Integer.parseInt(store.get("wod_id").toString());
			// String map=BaseUtil.parseMap2Str(store);
			 updateExtraWork(formStore,caller);
			submitExtraWork(id, caller);					
	}
	@Override
	public void deleteExtraWork(int wod_id, String caller) {
		Object wod_woid = baseDao.getFieldDataByCondition("Workovertimedet", "wod_woid", "wod_id="+wod_id);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {wod_id});
		// 删除
		baseDao.deleteById("Workovertimedet", "wod_id", wod_id);
		// 删除
		baseDao.deleteById("Workovertime", "wo_id", Integer.parseInt(wod_woid.toString()));
		// 记录操作
		baseDao.logger.delete(caller, "wod_id", wod_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {wod_id});

	}
	@Override
	public void updateExtraWork(String formStore, String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		checkTime(store);
		Object wod_woid = baseDao.getFieldDataByCondition("Workovertimedet", "wod_woid", "wod_id="+store.get("wod_id"));
		baseDao.updateByCondition("Workovertime", "wo_worktask='"+store.get("wo_worktask")+"'", "wo_id="+wod_woid);
		store.remove("wo_worktask");
		/**
		 * @author wsy
		 * 加班申请单计算时长。
		 */
		if(store.get("wod_count")==null || "0".equals(store.get("wod_count")) ||"".equals(store.get("wod_count"))){
			double wod_enddate = (double)DateUtil.parse(store.get("wod_enddate").toString(),"yyyy-MM-dd HH:mm:ss").getTime();
			double wod_startdate = (double)DateUtil.parse(store.get("wod_startdate").toString(),"yyyy-MM-dd HH:mm:ss").getTime();
			double time = (wod_enddate-wod_startdate)/(1000*60*60);
			DecimalFormat df = new DecimalFormat("#.00");
			store.remove("wod_count");
			store.put("wod_count", df.format(time));
		}
		handlerService.beforeUpdate(caller, new Object[] {store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Workovertimedet", "wod_id");
		baseDao.execute(formSql);
		//更新加班时数
		if (baseDao.isDBSetting(caller,"autoupdateWodcount")) {
			baseDao.callProcedure("sp_Workovertime_com", new Object[] {wod_woid});
		}		
		// 记录操作
		baseDao.logger.update(caller, "wod_id", store.get("wod_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store});
	
	}
	@Override
	public void submitExtraWork(int id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object wod_woid = baseDao.getFieldDataByCondition("Workovertimedet", "wod_woid", "wod_id="+id);
		Object status = baseDao.getFieldDataByCondition("Workovertime",
				"wo_statuscode", "wo_id=" + wod_woid);
		StateAssert.submitOnlyEntering(status);
		//判断是否启用延期限制提交
		handlerService.beforeSubmit(caller, new Object[] {id});
		// 执行提交操作
		baseDao.updateByCondition("Workovertime", "wo_status='已提交',wo_statuscode='COMMITED'", "wo_id="+wod_woid);
		// 记录操作
		baseDao.logger.submit(caller, "wod_id", id);;
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {id});
	}
	@Override
	public void resSubmitExtraWork(int id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object wod_woid = baseDao.getFieldDataByCondition("Workovertimedet", "wod_woid", "wod_id="+id);
		handlerService.beforeResSubmit(caller, new Object[] {id});
		Object status = baseDao.getFieldDataByCondition("Workovertime",
				"wo_statuscode", "wo_id=" + wod_woid);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.updateByCondition("Workovertime", "wo_status='在录入',wo_statuscode='ENTERING'", "wo_id="+wod_woid);
		// 记录操作
		baseDao.logger.resSubmit(caller, "wod_id", id);
		handlerService.afterResSubmit(caller, new Object[] {id});
	
	}
	@Override
	public void auditExtraWork(int id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object[] wod_woid = baseDao.getFieldsDataByCondition("Workovertimedet", new String[]{"wod_woid","wod_empcode"}, "wod_id="+id);
		Object status = baseDao.getFieldDataByCondition("Workovertime",
				"wo_statuscode", "wo_id=" + wod_woid[0]);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {id });
		// 执行审核操作
		baseDao.updateByCondition("Workovertime", "wo_status='已审核',wo_statuscode='AUDITED',wo_auditer='"+SystemSession.getUser().getEm_name()+"',wo_auditdate=sysdate", "wo_id="+wod_woid[0]);
		// 记录操作
		baseDao.logger.audit(caller, "wod_id", id);
		baseDao.callProcedure("COUNT_YEAR_REST", new Object[]{wod_woid[1]});
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {id });
	
	}
	@Override
	public void resAuditExtraWork(int id, String caller) {
		Object[] wod_woid = baseDao.getFieldsDataByCondition("Workovertimedet", new String[]{"wod_woid","wod_empcode"}, "wod_id="+id);
		Object status = baseDao.getFieldDataByCondition("Workovertime",
				"wo_statuscode", "wo_id=" + wod_woid[0]);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.updateByCondition("Workovertime", "wo_status='在录入',wo_statuscode='ENTERING'", "wo_id="+wod_woid[0]);
		// 记录操作
		baseDao.logger.resAudit(caller, "wod_id", id);
		baseDao.callProcedure("COUNT_YEAR_REST", new Object[]{wod_woid[1]});
	}
	@Override
	public void checkTime(Map<Object, Object> formStore){
		Timestamp starttime = Timestamp.valueOf(formStore.get("wod_startdate").toString());
		Timestamp endtime = Timestamp.valueOf(formStore.get("wod_enddate").toString());
		if(starttime.after(endtime)){
			BaseUtil.showError("时间输入有误，请检查后重新输入");
		}
	}
}
