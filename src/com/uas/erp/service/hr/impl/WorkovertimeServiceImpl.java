package com.uas.erp.service.hr.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.WorkovertimeDao;
import com.uas.erp.service.hr.WorkovertimeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class WorkovertimeServiceImpl implements WorkovertimeService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private WorkovertimeDao workovertime;

	@Autowired
	private HandlerService handlerService;
	
	/**
	 * 加班申请单（主从表）
	 * 保存
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void saveWorkovertime(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gStore=BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,new Object[] {store,gStore});
		String formSql=SqlUtil.getInsertSqlByFormStore(store, "Workovertime", new String[]{}, new Object[] {});
		for(Map<Object, Object> s:gStore){
			s.put("wod_id",baseDao.getSeqId("Workovertimedet_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gStore, "Workovertimedet");
		baseDao.execute(formSql);
		baseDao.execute(gridSql);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(wod_DETNO) from Workovertimedet where wod_startdate>wod_enddate and wod_woid=?",
						String.class, store.get("wo_id"));
		if (dets != null) {
			BaseUtil.showError("起始时间必须小于等于截止时间！行号：" + dets);
		}
		//计算加班时数
		if (baseDao.isDBSetting(caller,"autoupdateWodcount")) {
			baseDao.callProcedure("sp_Workovertime_com", new Object[] {store.get("wo_id")});
		}
		try {
			// 记录操作
			baseDao.logger.save(caller, "wo_id", store.get("wo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store,gStore});
	}
	
	/**
	 * 加班申请单（主从表）
	 * 更新
	 */
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateWorkovertimeById(String formStore, String gridStore,String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[] {store,gstore});
		// 修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Workovertime", "wo_id");
		baseDao.execute(formSql);
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "Workovertimedet", "wod_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("wod_id") == null || s.get("wod_id").equals("")
					|| s.get("wod_id").toString().equals("0")) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("Workovertimedet_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "Workovertimedet",
						new String[] { "wod_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(wod_DETNO) from Workovertimedet where wod_startdate>wod_enddate and wod_woid=?",
						String.class, store.get("wo_id"));
		if (dets != null) {
			BaseUtil.showError("起始时间必须小于等于截止时间！行号：" + dets);
		}
		//更新加班时数
		if (baseDao.isDBSetting(caller,"autoupdateWodcount")) {
			baseDao.callProcedure("sp_Workovertime_com", new Object[] {store.get("wo_id")});
		}		
		// 记录操作
		baseDao.logger.update(caller, "wo_id", store.get("wo_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store,gstore});
	}
	
	/**
	 * 加班申请单（主从表）
	 * 删除
	 */
	@Override
	public void deleteWorkovertime(int wo_id, String   caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {wo_id});
		// 删除purchase
		baseDao.deleteById("Workovertime", "wo_id", wo_id);
		// 删除purchaseDetail
		baseDao.deleteById("Workovertimedet", "wod_woid", wo_id);
		// 记录操作
		baseDao.logger.delete(caller, "wo_id", wo_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {wo_id});

	}
	
	/**
	 * 加班申请单（主从表）
	 * 审核
	 */
	@Override
	public void auditWorkovertime(int wo_id, String   caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Workovertime",
				"wo_statuscode", "wo_id=" + wo_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {wo_id });
		// 执行审核操作
		baseDao.audit("Workovertime", "wo_id=" + wo_id, "wo_status", "wo_statuscode", "wo_auditdate", "wo_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "wo_id", wo_id);
		SqlRowList rs = baseDao.queryForRowSet("select wod_empcode from Workovertimedet where wod_woid="+wo_id+"");
		while(rs.next()){
			String em_code = rs.getString("wod_empcode");
			baseDao.callProcedure("COUNT_YEAR_REST", new Object[]{em_code});
		}
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {wo_id });
	}
	
	/**
	 * 加班申请单（主从表）
	 * 反审核
	 */
	@Override
	public void resAuditWorkovertime(int wo_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Workovertime",
				"wo_statuscode", "wo_id=" + wo_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("Workovertime", "wo_id=" + wo_id, "wo_status", "wo_statuscode");
		SqlRowList rs = baseDao.queryForRowSet("select wod_empcode from Workovertimedet where wod_woid="+wo_id+"");
		while(rs.next()){
			String em_code = rs.getString("wod_empcode");
			baseDao.callProcedure("COUNT_YEAR_REST", new Object[]{em_code});
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "wo_id", wo_id);
	}
	
	/**
	 * 加班申请单（主从表）
	 * 提交
	 */
	@Override
	public void submitWorkovertime(int wo_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Workovertime",
				"wo_statuscode", "wo_id=" + wo_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		 List<Object[]> data= baseDao.getFieldsDatasByCondition("Workovertimedet", new String[]{"wod_startdate","wod_enddate","wod_detno"}, "wod_woid="+wo_id);
			for(Object[] os:data){
				Date start=DateUtil.parseStringToDate(os[0].toString(), "yyyy-MM-dd HH:mm:ss");
				Date end=DateUtil.parseStringToDate(os[1].toString(), "yyyy-MM-dd HH:mm:ss");
				if(start.getTime()>end.getTime()){
					BaseUtil.showError("第"+os[2]+"行,起始时间大于截止时间!");
				}							
			}
		//判断是否启用延期限制提交
		commitNeedCheck(caller,wo_id);
		handlerService.beforeSubmit(caller, new Object[] {wo_id});
		// 执行提交操作
		baseDao.submit("Workovertime", "wo_id=" + wo_id, "wo_status", "wo_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "wo_id", wo_id);
		//计算加班时数
		if (baseDao.isDBSetting(caller,"autoupdateWodcount")) {
			baseDao.callProcedure("sp_Workovertime_com", new Object[] {wo_id});
		}
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {wo_id});
	}
	
	/**
	 * 加班申请单（主从表）
	 * 反提交
	 */
	@Override
	public void resSubmitWorkovertime(int wo_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		handlerService.beforeResSubmit(caller, new Object[] {wo_id});
		Object status = baseDao.getFieldDataByCondition("Workovertime",
				"wo_statuscode", "wo_id=" + wo_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("Workovertime", "wo_id=" + wo_id, "wo_status", "wo_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "wo_id", wo_id);
		handlerService.afterResSubmit(caller, new Object[] {wo_id});
	}

	@Override
	public void syncDB(String caller, int id) {		
		workovertime.syncToSqlServer(id,caller);		
	}

	@Override
	public void confirmWorkovertime(int id, String   caller) {
		Object status = baseDao.getFieldDataByCondition("Workovertime", "wo_statuscode", "wo_id=" + id);
		StateAssert.confirmOnlyAudited(status);
		//执行反审核操作
		baseDao.updateByCondition("Workovertime", "wo_auditstatus='已处理'", "wo_id=" + id);
		//记录操作
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("msg.confirm"), BaseUtil.getLocalMessage("msg.confirmSuccess"), caller, "wo_id", id);
	}
	
	// 提交前判断是否超过系统设置的可延期天数
	private void commitNeedCheck(String caller, Object id) {
		if (baseDao.isDBSetting("Workovertime", "commitNeedCheck")) {
			String days = baseDao.getDBSetting("Workovertime","SetDelayDays");
			if(days!=null && Integer.parseInt(days)>0){
				Object endTime = baseDao.getFieldDataByCondition("Workovertime left join Workovertimedet on wod_woid=wo_id", "to_char(min(wod_startdate),'yyyy-mm-dd')", "wo_id="+id);
				boolean bool=baseDao.checkIf("dual", "DAY_COUNT(to_date('"+endTime.toString()+"','yyyy-mm-dd'),sysdate)>"+days);
				if(bool) BaseUtil.showError("系统设置的延期提交天数为"+days+"天，已超过不允许提交！");}
		}
	}
}
