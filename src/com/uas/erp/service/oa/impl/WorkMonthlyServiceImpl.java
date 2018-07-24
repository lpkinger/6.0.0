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
import com.uas.erp.dao.common.WorkMonthlyDao;
import com.uas.erp.service.oa.WorkMonthlyService;

@Service
public class WorkMonthlyServiceImpl implements WorkMonthlyService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private WorkMonthlyDao workMonthlyDao;
	
	//获取限制补写月报的参数设置
	@Override
	public void workWeeklyLimit(String wm_month){
		String limitNumber = baseDao.getDBSetting("WorkMonthly","workMonthlyLimit");
		int limit = 1;
		if(limitNumber!=null){
			limit = (int)Math.floor(Double.parseDouble(limitNumber));
			if(limit<0){
				limit = 0 ;
			}else if(limit>12){
				limit = 12 ;
			}
		}
		Object filed = baseDao.getFieldDataByCondition("dual", "to_char(sysdate,'mm')-"+wm_month,"1=1");
		int months = Integer.parseInt(filed.toString());
		if(months<0){
			months = months + 12;
		}
		if(months>limit){
			if(limit==0){
				BaseUtil.showError("只能写本月的月报!");
			}else{					
				BaseUtil.showError("只能写本月以及"+limit+"个月前的月报!");
			}
		}
	}
	@Override
	public void saveWorkMonthly(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		if(store.get("wm_code")==null||"".equals(store.get("wm_code"))){
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
			String emcode = null;
			//加入录入日期
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(System.currentTimeMillis());
			store.put("wm_date", format.format(date));
			// 根据员工编号取出部门岗位组织
			emcode = (String) store.get("wm_empcode");
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
			store.put("wm_depart", dept);
			store.put("wm_hrorg", orname);
			store.put("wm_joname", position);
			store.put("wm_emp", emname);
			store.put("wm_empid", emid);
		}
		String wm_code = store.get("wm_code").toString();
		//根据录入人和录入日期判断一个人一月只能录入一张工作月报单
		String wm_empcode=store.get("wm_empcode").toString();
		String wm_starttime=store.get("wm_starttime").toString();
		String wc=(String) baseDao.getFieldDataByCondition("WorkMonthly","wm_code","wm_empcode='"+wm_empcode+"' and trunc(wm_starttime)=trunc(to_date('"+wm_starttime+"','yyyy-mm-dd'))");
		if(wc!=null&&!"".equals(wc)){
			BaseUtil.showError("你在"+store.get("wm_month").toString()+"月已经录入了一张工作月报，该工作月报的编号为:"+wc);
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("WorkMonthly", "wm_code='"
				+ wm_code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		//获取限制补写月报的参数设
		workWeeklyLimit(store.get("wm_month").toString());
		//添加阅读者
		Object wmid=store.get("wm_groupid");
		Object wmemid=store.get("wm_empid");
		if(wmid!=null&&wmemid!=null&&!"".equals(wmemid)){
			int wm_id=Integer.parseInt(store.get("wm_id").toString());
			if (wmid!=null&&!"".equals(wmid)) {
				String wm_groupid=wmid.toString();			
				String iString="employee#"+wmemid;			
				if(wm_groupid.indexOf(iString)==-1){
					wm_groupid=wm_groupid+';'+iString;
				}				
				baseDao.setReader("WorkMonthly", wm_id, wm_groupid);	
			}else{
				String wm_groupid="employee#"+wmemid;
				baseDao.setReader("WorkMonthly", wm_id, wm_groupid);	
			}
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "WorkMonthly", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "wm_id", store.get("wm_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateWorkMonthly(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//根据录入人和录入日期判断一个人一月只能录入一张工作月报单
		String wm_empcode=store.get("wm_empcode").toString();
		String wm_starttime=store.get("wm_starttime").toString();
		if (wm_starttime != null) {
			String REG_D = "\\d{4}-\\d{2}-\\d{2}";
			String REG_DT = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
			if (wm_starttime.matches(REG_D)) {// 判断是否是形如yyyy-mm-dd格式的日期类型数据
				wm_starttime = DateUtil.parseDateToOracleString(Constant.YMD, wm_starttime);
			} else if (wm_starttime.matches(REG_DT)) {
				wm_starttime = DateUtil.parseDateToOracleString(Constant.YMD_HMS, wm_starttime);
			}
		}
		Object[] wc=baseDao.getFieldsDataByCondition("WorkMonthly","wm_id,wm_code","wm_empcode='"+wm_empcode+"' and trunc(wm_starttime)=trunc("+wm_starttime+")");
		if(wc!=null){
			if(wc[0]!=null&&!"".equals(wc[0])){
				if(!wc[0].toString().equals(store.get("wm_id").toString())){				
					BaseUtil.showError("你在"+store.get("wm_month").toString()+"月已经录入了一张工作月报，该工作月报的编号为:"+wc[1]);
				}
			}
		}
		//添加阅读者
		Object wmid=store.get("wm_groupid");
		Object wmemid=store.get("wm_empid");
		if(wmid!=null&&wmemid!=null&&!"".equals(wmemid)){
			int wm_id=Integer.parseInt(store.get("wm_id").toString());
			if (wmid!=null&&!"".equals(wmid)) {
				String wm_groupid=wmid.toString();			
				String iString="employee#"+wmemid;			
				if(wm_groupid.indexOf(iString)==-1){
					wm_groupid=wm_groupid+';'+iString;
				}				
				baseDao.setReader("WorkMonthly", wm_id, wm_groupid);	
			}else{
				String wm_groupid="employee#"+wmemid;
				baseDao.setReader("WorkMonthly", wm_id, wm_groupid);	
			}
		}
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改WorkMonthly
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "WorkMonthly", "wm_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "wm_id", store.get("wm_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
		
	}

	@Override
	public void deleteWorkMonthly(int wm_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{wm_id});
		//删除purchase
		baseDao.deleteById("WorkMonthly", "wm_id", wm_id);
		//记录操作
		baseDao.logger.delete(caller, "wm_id", wm_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{wm_id});
	}
	/*
	 * 月报提交
	 */
	@Override
	public void submitWorkMonthly(int wm_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkMonthly",
				"wm_statuscode", "wm_id=" + wm_id);
		if(status==null){
			BaseUtil.showError("该单已不存在");
		}
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.submit_onlyEntering"));
		}
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, wm_id);
		// 执行提交操作
		baseDao.updateByCondition("WorkMonthly",
				"wm_statuscode='COMMITED',wm_status='"
						+ BaseUtil.getLocalMessage("COMMITED") + "'", "wm_id="
						+ wm_id);
		baseDao.callProcedure("SP_CATCHWORKCONTENTMONTHLY",new Object[] {wm_id});
		// 记录操作
		baseDao.logger.submit(caller, "wm_id", wm_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, wm_id);
	}
	/*
	 * 月报反提交
	 */
	@Override
	public void resSubmitWorkMonthly(int wm_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("WorkMonthly",
				"wm_statuscode", "wm_id=" + wm_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		// 执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, wm_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"WorkMonthly",
				"wm_statuscode='ENTERING',wm_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "wm_id="
						+ wm_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "wm_id", wm_id);
		// 执行反提交后的其它逻辑
		handlerService.afterResSubmit(caller, wm_id);
	}

	@Override
	public void auditWorkMonthly(int wm_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("WorkMonthly",
				"wm_statuscode", "wm_id=" + wm_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.audit_onlyCommited"));
		}
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, wm_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"WorkMonthly",
				"wm_statuscode='AUDITED',wm_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "wm_id="
						+ wm_id);
		// 记录操作
		baseDao.logger.audit(caller, "wm_id", wm_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, wm_id);
	}

	@Override
	public void resAuditWorkMonthly(int wm_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("WorkMonthly",
				"wm_statuscode", "wm_id=" + wm_id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resAudit_onlyAudit"));
		}
		// 执行反审核前的其它逻辑
		handlerService.beforeResAudit(caller, wm_id);
		// 执行反审核操作
		baseDao.updateByCondition(
				caller,
				"wm_statuscode='ENTERING',wm_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "wm_id="
						+ wm_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "wm_id", wm_id);
		// 执行反审核后的其它逻辑
		handlerService.afterResAudit(caller, wm_id);
	}

	@Override
	public void catchWorkContentMonthly(int wm_id, String caller) {
		baseDao.callProcedure("SP_CATCHWORKCONTENTMONTHLY",new Object[] {wm_id});		
	}

}
