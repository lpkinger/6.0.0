package com.uas.erp.service.oa.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.WorkDailyDao;
import com.uas.erp.service.oa.WorkDailyService;

@Service
public class WorkDailyServiceImpl implements WorkDailyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private WorkDailyDao workDailyDao;
	
	//获取限制补写日报的参数设置
	@Override
	public void workDailyLimit(String wd_date){
		String limitNumber = baseDao.getDBSetting("WorkDaily","workDailyLimit");
		int limit = 1;
		if(limitNumber!=null){
			limit = (int)Math.floor(Double.parseDouble(limitNumber));
			if(limit<0){
				limit = 0 ;
			}else if(limit>12){
				limit = 12 ;
			}
		}
		boolean flag = baseDao.checkByCondition("dual", "to_date(sysdate)-to_date('"+wd_date+"','yyyy-mm-dd')>=0 and to_date(sysdate)-to_date('"+wd_date+"','yyyy-mm-dd')<="+limit);		
		if(flag){
			if(limit==0){
				BaseUtil.showError("只能写今天的日报!");
			}else{					
				BaseUtil.showError("只能写今天以及"+limit+"天前的日报!");
			}
		}
	}
	@Override
	public void saveWorkDaily(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		Integer id = null;
		if(store.get("wd_code")==null||"".equals(store.get("wd_code"))){
			String[] fields = {"fo_seq","fo_keyfield","fo_table","fo_codefield","fo_statusfield","fo_statuscodefield"};
			Object[] formData = baseDao.getFieldsDataByCondition("form",  fields, "fo_caller='"+caller+"'");
			id = baseDao.getSeqId(formData[0].toString());
			store.put(formData[1].toString(), id);
			String table = formData[2].toString();
			Object code = baseDao.sGetMaxNumber(table != null ? table.split(" ")[0]
					: caller, 2);
			store.put(formData[3].toString(), code);
			store.put(formData[4].toString(), "在录入");
			store.put(formData[5].toString(), "ENTERING");
			String emcode = null;
			//加入录入日期
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());
			store.put("wd_entrydate", format.format(date));
			// 根据员工编号取出部门岗位组织
			emcode = (String) store.get("wd_empcode");
			Object dept = baseDao.getFieldDataByCondition("employee", "em_depart",
					"em_code='" + emcode + "'");
			Object position = baseDao.getFieldDataByCondition("employee",
					"EM_POSITION", "em_code='" + emcode + "'");
			Object orname = baseDao.getFieldDataByCondition("employee",
					"EM_DEFAULTORNAME", "em_code='" + emcode + "'");
			Object emname = baseDao.getFieldDataByCondition("employee", "em_name",
					"em_code='" + emcode + "'");
			Object emid = baseDao.getFieldDataByCondition("employee", "em_id",
					"em_code='" + emcode + "'");
			store.put("wd_depart", dept);
			store.put("wd_hrorg", orname);
			store.put("wd_joname", position);
			store.put("wd_emp", emname);
			store.put("wd_empid", emid);
		}
		String wd_code = store.get("wd_code").toString();
		//根据录入人和日报日期判断一个人一天只能录入一张工作日报单
		String wd_empcode=store.get("wd_empcode").toString();
		String wd_date=store.get("wd_date").toString();
		String wc=(String) baseDao.getFieldDataByCondition("WorkDaily","wd_code","wd_empcode='"+wd_empcode+"' and trunc(wd_date)=trunc(to_date('"+wd_date+"','yyyy-mm-dd'))");
		if(wc!=null&&!"".equals(wc)){
			BaseUtil.showError("你在"+wd_date+"已经录入了一张工作日报，该工作日报的编号为:"+wc);
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("WorkDaily", "wd_code='"
				+ wd_code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		//获取限制补写日报的参数设置
		workDailyLimit(wd_date);
		//添加阅读者
		Object wdid=store.get("wd_groupid");
		Object wdemid=store.get("wd_empid");
		if(wdid!=null&&wdemid!=null&&!"".equals(wdemid)){
			int wd_id=Integer.parseInt(store.get("wd_id").toString());
			if (wdid!=null&&!"".equals(wdid)) {
				String wd_groupid=wdid.toString();			
				String iString="employee#"+wdemid;			
				if(wd_groupid.indexOf(iString)==-1){
					wd_groupid=wd_groupid+';'+iString;
				}				
				baseDao.setReader("WorkDaily", wd_id, wd_groupid);	
			}else{
				String wd_groupid="employee#"+wdemid;
				baseDao.setReader("WorkDaily", wd_id, wd_groupid);	
			}
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkDaily", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		// 保存detailgrid
		if (gridStore != null && gridStore.length() > 2) {
			Object[] objects = baseDao.getFieldsDataByCondition("detailgrid", new String[] { "dg_table", "dg_field" }, "dg_caller='"
					+ caller + "' AND dg_logictype='keyField'");
			if (objects != null) {
				Object tab = objects[0] == null ? baseDao.getFieldDataByCondition("Form", "fo_detailtable", "fo_caller='" + caller + "'")
						.toString().split(" ")[0] : objects[0];
				List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
				for (Map<Object, Object> map : grid) {
					map.put(objects[1], baseDao.getSeqId(tab.toString().toUpperCase().split(" ")[0] + "_SEQ"));
					if (id != null && id != -1) {
						map.put("wdd_wdid", id);
						}
				}
				List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "WorkDailydetail");
				baseDao.execute(gridSql);
			}
		}
		//记录操作
		baseDao.logger.save(caller, "wd_id", store.get("wd_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateWorkDaily(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(store.get("wd_date")==null||"".equals(store.get("wd_date"))){
			store.put("wd_date", DateUtil.currentDateString(null));
		}
		//根据录入人和日报日期判断一个人一天只能录入一张工作日报单
		String wd_empcode=store.get("wd_empcode").toString();
		String wd_date=store.get("wd_date").toString();
		Object[] wc=baseDao.getFieldsDataByCondition("WorkDaily","wd_id,wd_code","wd_empcode='"+wd_empcode+"' and trunc(wd_date)=trunc(to_date('"+wd_date+"','yyyy-mm-dd'))");
		if(wc!=null){
			if(wc[0]!=null&&!"".equals(wc[0])){
				if(!wc[0].toString().equals(store.get("wd_id").toString())){
					BaseUtil.showError("你在"+wd_date+"已经录入了一张工作日报，该工作日报的编号为:"+wc[1]);
				}
			}
		}
		//添加阅读者
		Object wdid=store.get("wd_groupid");
		Object wdemid=store.get("wd_empid");
		if(wdid!=null&&wdemid!=null&&!"".equals(wdemid)){
			int wd_id=Integer.parseInt(store.get("wd_id").toString());
			if (wdid!=null&&!"".equals(wdid)) {
				String wd_groupid=wdid.toString();			
				String iString="employee#"+wdemid;			
				if(wd_groupid.indexOf(iString)==-1){
					wd_groupid=wd_groupid+';'+iString;
				}				
				baseDao.setReader("WorkDaily", wd_id, wd_groupid);	
			}else{
				String wd_groupid="employee#"+wdemid;
				baseDao.setReader("WorkDaily", wd_id, wd_groupid);	
			}
		}
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改WorkDaily
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkDaily", "wd_id");
		baseDao.execute(formSql);
		// 修改Grid
		if (gridStore != null && gridStore.length() > 2) {
			Object[] objects = baseDao.getFieldsDataByCondition("detailgrid", new String[] { "dg_table", "dg_field" }, "dg_caller='"
					+ caller + "' AND dg_logictype='keyField'");
			if (objects != null) {
				Object tab = objects[0] == null ? baseDao.getFieldDataByCondition("Form", "fo_detailtable", "fo_caller='" + caller + "'")
						.toString().split(" ")[0] : objects[0];
				List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
				List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(grid, tab.toString(), (String) objects[1]);
				for (Map<Object, Object> map : grid) {
					Object id = map.get(objects[1].toString());
					if (id == null || "".equals(id.toString()) || Integer.parseInt(id.toString()) == 0) {
						map.put(objects[1], baseDao.getSeqId(tab.toString().toUpperCase().split(" ")[0] + "_SEQ"));
						gridSql.add(SqlUtil.getInsertSqlByMap(map, "WorkDailydetail"));
					}
				}
				baseDao.execute(gridSql);
			}
		}
		//记录操作
		baseDao.logger.update(caller, "wd_id", store.get("wd_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
		
	}

	@Override
	public void deleteWorkDaily(int wd_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{wd_id});
		//删除purchase
		baseDao.deleteById("WorkDaily", "wd_id", wd_id);
		//删除明细
		baseDao.deleteById("WorkDailydetail", "wdd_wdid", wd_id);
		//记录操作
		baseDao.logger.delete(caller, "wd_id", wd_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{wd_id});
	}
	/*
	 * 日报提交
	 */
	@Override
	public void submitWorkDaily(int wd_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkDaily",
				"wd_statuscode", "wd_id=" + wd_id);
		if(status==null){
			BaseUtil.showError("该单已不存在");
		}
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, wd_id);
		// 执行提交操作
		baseDao.updateByCondition("WorkDaily",
				"wd_statuscode='COMMITED',wd_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "wd_id="
						+ wd_id);		
		baseDao.callProcedure("SP_CATCHWORKCONTENT",new Object[] {wd_id});
		// 记录操作
		baseDao.logger.submit(caller, "wd_id", wd_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, wd_id);
	}
	/*
	 * 日报反提交
	 */
	@Override
	public void resSubmitWorkDaily(int wd_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkDaily",
				"wd_statuscode", "wd_id=" + wd_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, wd_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"WorkDaily",
				"wd_statuscode='ENTERING',wd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "wd_id="
						+ wd_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "wd_id", wd_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, wd_id);
	}

	@Override
	public void auditWorkDaily(int wd_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("WorkDaily",
				"wd_statuscode", "wd_id=" + wd_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, wd_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"WorkDaily",
				"wd_statuscode='AUDITED',wd_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "wd_id="
						+ wd_id);
		// 记录操作
		baseDao.logger.audit(caller, "wd_id", wd_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wd_id);
	}

	@Override
	public void resAuditWorkDaily(int wd_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WorkDaily",
				"wd_statuscode", "wd_id=" + wd_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, wd_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				caller,
				"wd_statuscode='ENTERING',wd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "wd_id="
						+ wd_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "wd_id", wd_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, wd_id);
	}

	@Override
	public void catchWorkContent(int wd_id, String caller) {
		baseDao.callProcedure("SP_CATCHWORKCONTENTDETAIL",new Object[] {wd_id});		
	}
}
