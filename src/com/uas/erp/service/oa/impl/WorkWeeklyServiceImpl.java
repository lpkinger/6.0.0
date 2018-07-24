package com.uas.erp.service.oa.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.WorkWeeklyDao;
import com.uas.erp.service.oa.WorkWeeklyService;

@Service
public class WorkWeeklyServiceImpl implements WorkWeeklyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private WorkWeeklyDao workWeeklyDao;
	
	//获取限制补写周报的参数设置
	@Override
	public void workWeeklyLimit(String ww_starttime){
		String limitNumber = baseDao.getDBSetting("WorkWeekly","workWeeklyLimit");
		int limit = 1;
		if(limitNumber!=null){
			limit = (int)Math.floor(Double.parseDouble(limitNumber));
			if(limit<0){
				limit = 0 ;
			}else if(limit>12){
				limit = 12 ;
			}
		}
		boolean flag = baseDao.checkByCondition("dual", "trunc(sysdate,'d')+1-trunc("+ww_starttime+")>=0 and trunc(sysdate,'d')+1-trunc("+ww_starttime+")<="+(limit*7));		
		if(flag){
			if(limit==0){
				BaseUtil.showError("只能写本周的周报!");
			}else{					
				BaseUtil.showError("只能写本周以及"+limit+"周前的周报!");
			}
		}
	}
	@Override
	public void saveWorkWeekly(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(store.get("ww_code")==null||"".equals(store.get("ww_code"))){
			String[] fields = {"fo_seq","fo_keyfield","fo_table","fo_codefield","fo_statusfield","fo_statuscodefield"};
			Object[] formData = baseDao.getFieldsDataByCondition("form",  fields, "fo_caller='"+caller+"'");
			Object id = baseDao.getSeqId(formData[0].toString());
			store.put(formData[1].toString(), id);
			String table = formData[2].toString();
			Object code = baseDao.sGetMaxNumber(table != null ? table.split(" ")[0]
					: caller, 2);
			store.put(formData[3].toString(), code);
			store.put(formData[4].toString(), "在录入");
			store.put(formData[5].toString(), "ENTERING");
			//加入录入日期
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());
			store.put("ww_date", format.format(date));
			String emcode = null;
			// 根据员工编号取出部门岗位组织
			emcode = (String) store.get("ww_empcode");
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
			store.put("ww_depart", dept);
			store.put("ww_hrorg", orname);
			store.put("ww_joname", position);
			store.put("ww_emp", emname);
			store.put("ww_empid", emid);
		}
		String ww_code = store.get("ww_code").toString();
		//根据录入人和录入日期判断一个人一周只能录入一张工作周报单
		String ww_empcode=store.get("ww_empcode").toString();
		String ww_starttime=store.get("ww_starttime").toString();
		String wc=(String) baseDao.getFieldDataByCondition("WorkWeekly","ww_code","ww_empcode='"+ww_empcode+"' and trunc(ww_starttime)=trunc(to_date('"+ww_starttime+"','yyyy-mm-dd'))");
		if(wc!=null&&!"".equals(wc)){
			BaseUtil.showError("你在第"+store.get("ww_week").toString()+"周已经录入了一张工作周报，该工作周报的编号为:"+wc);
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("WorkWeekly", "ww_code='"
				+ ww_code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		//获取限制补写周报的参数设置
		workWeeklyLimit(DateUtil.parseDateToOracleString(Constant.YMD, ww_starttime));
		//添加阅读者
		Object wwid=store.get("ww_groupid");
		Object wwemid=store.get("ww_empid");
		if(wwid!=null&&wwemid!=null&&!"".equals(wwemid)){
			int ww_id=Integer.parseInt(store.get("ww_id").toString());
			if (wwid!=null&&!"".equals(wwid)) {
				String ww_groupid=wwid.toString();			
				String iString="employee#"+wwemid;			
				if(ww_groupid.indexOf(iString)==-1){
					ww_groupid=ww_groupid+';'+iString;
				}				
				baseDao.setReader("WorkWeekly", ww_id, ww_groupid);	
			}else{
				String ww_groupid="employee#"+wwemid;
				baseDao.setReader("WorkWeekly", ww_id, ww_groupid);	
			}
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkWeekly", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "ww_id", store.get("ww_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateWorkWeekly(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//根据录入人和录入日期判断一个人一周只能录入一张工作周报单
		String ww_empcode=store.get("ww_empcode").toString();
		String ww_starttime=store.get("ww_starttime").toString();
		if (ww_starttime != null) {
			String REG_D = "\\d{4}-\\d{2}-\\d{2}";
			String REG_DT = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
			if (ww_starttime.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
				ww_starttime = DateUtil.parseDateToOracleString(Constant.YMD, ww_starttime);
			} else if (ww_starttime.matches(REG_DT)) {
				ww_starttime = DateUtil.parseDateToOracleString(Constant.YMD_HMS, ww_starttime);
			}
		}
		Object[] wc=baseDao.getFieldsDataByCondition("WorkWeekly","ww_id,ww_code","ww_empcode='"+ww_empcode+"' and trunc(ww_starttime)=trunc("+ww_starttime+")");
		if(wc!=null){			
			if(wc[0]!=null&&!"".equals(wc[0])){
				if(!wc[0].toString().equals(store.get("ww_id").toString())){				
					BaseUtil.showError("你在第"+store.get("ww_week").toString()+"周已经录入了一张工作周报，该工作周报的编号为:"+wc[1]);
				}
			}
		}
		//添加阅读者
		Object wwid=store.get("ww_groupid");
		Object wwemid=store.get("ww_empid");
		if(wwid!=null&&wwemid!=null&&!"".equals(wwemid)){
			int ww_id=Integer.parseInt(store.get("ww_id").toString());
			if (wwid!=null&&!"".equals(wwid)) {
				String ww_groupid=wwid.toString();			
				String iString="employee#"+wwemid;			
				if(ww_groupid.indexOf(iString)==-1){
					ww_groupid=ww_groupid+';'+iString;
				}				
				baseDao.setReader("WorkWeekly", ww_id, ww_groupid);	
			}else{
				String ww_groupid="employee#"+wwemid;
				baseDao.setReader("WorkWeekly", ww_id, ww_groupid);	
			}
		}
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改WorkWeekly
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkWeekly", "ww_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ww_id", store.get("ww_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
		
	}

	@Override
	public void deleteWorkWeekly(int ww_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{ww_id});
		//删除purchase
		baseDao.deleteById("WorkWeekly", "ww_id", ww_id);
		//记录操作
		baseDao.logger.delete(caller, "ww_id", ww_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ww_id});
	}
	/*
	 * 周报提交
	 */
	@Override
	public void submitWorkWeekly(int ww_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkWeekly",
				"ww_statuscode", "ww_id=" + ww_id);
		if(status==null){
			BaseUtil.showError("该单已不存在");
		}
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, ww_id);
		// 执行提交操作
		baseDao.updateByCondition("WorkWeekly",
				"ww_statuscode='COMMITED',ww_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "ww_id="
						+ ww_id);		
		baseDao.callProcedure("SP_CATCHWORKCONTENTWEEKLY",new Object[] {ww_id});
		// 记录操作
		baseDao.logger.submit(caller, "ww_id", ww_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, ww_id);
	}
	/*
	 * 周报反提交
	 */
	@Override
	public void resSubmitWorkWeekly(int ww_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkWeekly",
				"ww_statuscode", "ww_id=" + ww_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, ww_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"WorkWeekly",
				"ww_statuscode='ENTERING',ww_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ww_id="
						+ ww_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "ww_id", ww_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, ww_id);
	}

	@Override
	public void auditWorkWeekly(int ww_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("WorkWeekly",
				"ww_statuscode", "ww_id=" + ww_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, ww_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"WorkWeekly",
				"ww_statuscode='AUDITED',ww_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "ww_id="
						+ ww_id);
		// 记录操作
		baseDao.logger.audit(caller, "ww_id", ww_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, ww_id);
	}

	@Override
	public void resAuditWorkWeekly(int ww_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WorkWeekly",
				"ww_statuscode", "ww_id=" + ww_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, ww_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				caller,
				"ww_statuscode='ENTERING',ww_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "ww_id="
						+ ww_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "ww_id", ww_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, ww_id);
	}

	@Override
	public void catchWorkContentWeekly(int ww_id, String caller) {
		baseDao.callProcedure("SP_CATCHWORKCONTENTWEEKLY",new Object[] {ww_id});		
	}

}
