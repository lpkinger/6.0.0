package com.uas.erp.service.plm.impl;

import java.util.ArrayList;
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
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.plm.ProjectWeeklyReportService;


@Service("projectWeeklyReportService")
public class ProjectWeeklyReportServiceImpl implements ProjectWeeklyReportService{
	
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	
	
	@Override
	public List<Map<String, Object>> autoGetGridData(String man,String prjcode) {
		String condition = null;
		SqlRowList rs = null;
		if (man==null||"".equals(man.trim())) {
			condition = "1=2";
		}else {
			condition = " prj_assigntocode ='"+man+"'";
		}
		if (prjcode!=null&&!"".equals(prjcode.trim())) {
			rs = baseDao.queryForRowSet("select prj_id,prj_code,prj_name,prj_status,prj_completerate from Project where prj_code ='"+prjcode+"' and "+condition);
		}else {
			rs = baseDao.queryForRowSet("select prj_id,prj_code,prj_name,prj_status,prj_completerate from Project where (prj_statuscode ='UNDOING' or prj_statuscode ='DOING' or prj_statuscode ='STOP') and "+condition);
		}
		
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("wrd_detno", rs.getCurrentIndex()+1);
			map.put("wrd_prjcode", rs.getGeneralString("prj_code"));
			map.put("wrd_prjname", rs.getGeneralString("prj_name"));
			map.put("wrd_prjstatus", rs.getGeneralString("prj_status"));
			SqlRowList phs = baseDao.queryForRowSet("select pp_phase,pp_startdate,pp_enddate,pp_status from ProjectPhase where pp_prjid ="+rs.getObject("prj_id")+" order by pp_detno asc");
			String mileplan = new String();
			while(phs.next()){				
				mileplan+=(phs.getCurrentIndex()+1)+"、"+phs.getGeneralString("pp_phase")+" 计划日期："+DateUtil.format(phs.getDate("pp_startdate"),Constant.YMD)+"——"+DateUtil.format(phs.getDate("pp_enddate"),Constant.YMD);
				mileplan+=" "+phs.getGeneralString("pp_status")+"\n";
			}
			SqlRowList tasks = baseDao.queryForRowSet("select name,resourcename from ProjectTask where realenddate >= (select  trunc(sysdate,'d')+1 from dual) and realenddate <= (select  trunc(sysdate,'d')+7 from dual) and  handstatuscode ='FINISHED' and prjplanid ="+rs.getObject("prj_id")+" order by realenddate asc");
			String finishedtask = new String();
			while(tasks.next()){				
				finishedtask+=(tasks.getCurrentIndex()+1)+"、"+tasks.getGeneralString("name")+" ("+tasks.getGeneralString("resourcename")+")\n";	
			}
			map.put("wrd_mileplan", mileplan);
			map.put("wrd_finishedtask", finishedtask);
			map.put("wrd_ratelastweek", rs.getGeneralString("prj_completerate"));
			data.add(map);
		}
		return data;
	}
	

	@Override
	public void savePrjWkReport(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		
		Object wr_date = store.get("wr_date");
		String date = null;
		String year = null;
		if(wr_date!=null&&!"".equals(wr_date)){
			date = wr_date.toString();
		}else{
			date = DateUtil.currentDateString(Constant.YMD);
		}
		year = date.substring(0,date.indexOf("-"));
		
		Object dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(wr_code) from PrjWkReport where wr_week = ? and wr_responsiblemancode = ? and to_char(wr_date,'yyyy')='"+year+"'", String.class,store.get("wr_week"),store.get("wr_responsiblemancode"));
		if (dets!=null) {
			BaseUtil.showError("一人一周只能填写一次项目周报！");
		}
		String code = baseDao.sGetMaxNumber("PrjWkReport", 2);
		store.put("wr_code", code);
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存PrjWkReport
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PrjWkReport", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//保存PrjWkReportDetail
		for(Map<Object, Object> m:grid){
			m.put("wrd_id", baseDao.getSeqId("PRJWKREPORTDETAIL_SEQ"));
			m.put("wrd_wrid", store.get("wr_id"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "PrjWkReportDetail");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "pu_id", store.get("pu_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}
	
	@Override
	public void updatePrjWkReport(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的项目周报!
		Object status = baseDao.getFieldDataByCondition("PrjWkReport", "wr_statuscode", "wr_id=" + store.get("wr_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改PrjWkReport
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PrjWkReport", "wr_id");
		baseDao.execute(formSql);
		// 修改PrjWkReportDetail
		List<String> gridSql = SqlUtil.getInsertOrUpdateSqlbyGridStore(gstore, "PrjWkReportDetail", "wrd_id"); 
		
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "wr_id", store.get("wr_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}
	
	@Override
	public void deletePrjWkReport(int wr_id, String caller) {
		// 只能删除在录入的预立项任务书!
		Object status = baseDao.getFieldDataByCondition("PrjWkReport", "wr_statuscode", "wr_id=" + wr_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { wr_id });
		baseDao.delCheck("PrjWkReport", wr_id);	
		//删除PrjWkReportDetail
		baseDao.deleteByCondition("PrjWkReportDetail", "wrd_wrid=" + wr_id);
		// 删除PrjWkReport
		baseDao.deleteById("PrjWkReport", "wr_id", wr_id);
		// 记录操作
		baseDao.logger.delete(caller, "wr_id", wr_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { wr_id });
	}
	
	@Override
	public void submitPrjWkReport(int wr_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("PrjWkReport", new String[]{"wr_statuscode","wr_week","wr_responsiblemancode","to_char(wr_date,'yyyy-mm-dd')"}, "wr_id=" + wr_id);
		
		Object wr_date = status[3];
		String date = null;
		String year = null;
		if(wr_date!=null&&!"".equals(wr_date)){
			date = wr_date.toString();
		}else{
			date = DateUtil.currentDateString(Constant.YMD);
		}
		year = date.substring(0,date.indexOf("-"));
		StateAssert.submitOnlyEntering(status[0]);
		Object dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(wr_code) from PrjWkReport where wr_statuscode <> 'ENTERING'  and wr_id <> ? and wr_week = ? and wr_responsiblemancode = ? and to_char(wr_date,'yyyy')='"+year+"'", String.class,wr_id,status[1],status[2]);
		if (dets!=null) {
			BaseUtil.showError("一人一周只能提交一次项目周报！");
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { wr_id });
		// 执行提交操作
		baseDao.submit("PrjWkReport", "wr_id=" + wr_id, "wr_status", "wr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "wr_id", wr_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { wr_id });
	}

	@Override
	public void resSubmitPrjWkReport(int wr_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PrjWkReport", "wr_statuscode", "wr_id=" + wr_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { wr_id });
		// 执行反提交操作
		baseDao.resOperate("PrjWkReport", "wr_id=" + wr_id, "wr_status", "wr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "wr_id", wr_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { wr_id });
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void auditPrjWkReport(int wr_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PrjWkReport", "wr_statuscode", "wr_id=" + wr_id);
		StateAssert.auditOnlyCommited(status);
		
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { wr_id });
	    //将本周项目完成率回写到立项申请	
		List<Object[]> list = baseDao.getFieldsDatasByCondition("PRJWKREPORTDETAIL", new String[] {"wrd_prjcode","wrd_id"}, "wrd_wrid="+wr_id);
		for (Object[] object : list) {
			baseDao.updateByCondition("project", "prj_completerate=(select wrd_rateweek from PRJWKREPORTDETAIL where wrd_id="+object[1]+")", "prj_code='"+object[0]+"'");
		}
		// 执行审核操作
		baseDao.audit("PrjWkReport", "wr_id=" + wr_id, "wr_status", "wr_statuscode", "wr_auditdate", "wr_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "wr_id", wr_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { wr_id });
	}

	@Override
	public void resAuditPrjWkReport(int wr_id, String caller) {
		
		// 只能对状态为[已审核]的预立项任务书进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PrjWkReport", "wr_statuscode", "wr_id=" + wr_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("PrjWkReport", wr_id);
		//将上周项目完成率回写到立项申请	
		List<Object[]> list = baseDao.getFieldsDatasByCondition("PRJWKREPORTDETAIL", new String[] {"wrd_prjcode","wrd_id"}, "wrd_wrid="+wr_id);
		for (Object[] object : list) {
			baseDao.updateByCondition("project", "prj_completerate=(select wrd_ratelastweek from PRJWKREPORTDETAIL where wrd_id="+object[1]+")", "prj_code='"+object[0]+"'");
		}
		// 执行反审核操作
		baseDao.resAudit("PrjWkReport", "wr_id=" + wr_id, "wr_status", "wr_statuscode", "wr_auditdate", "wr_auditman");
		// 记录操作
		baseDao.logger.resAudit(caller, "wr_id", wr_id);
	}
	
}
