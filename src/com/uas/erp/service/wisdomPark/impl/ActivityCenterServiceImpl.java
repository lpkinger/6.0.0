package com.uas.erp.service.wisdomPark.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.Employee;
import com.uas.erp.service.wisdomPark.ActivityCenterService;

@Service("activityCenterService")
public class ActivityCenterServiceImpl implements ActivityCenterService{
	
	@Autowired BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private EndActivityTask endActivityTask;
	
	
	@Override
	public void deleteActivityType(String caller, int id) {
		// 只能删除没有发布的活动的活动分类
		boolean bool = baseDao.checkIf("ActivityType", "at_id = " + id +" and nvl(at_count,0) > 0 ");
		if (bool) {
			BaseUtil.showError("该活动分类存在进行中活动，不能删除！");
		}
		
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		
		// 删除ActivityCenter
		baseDao.deleteById("ActivityType", "at_id", id);
		
		// 记录操作
		baseDao.logger.delete(caller, "at_id", id);
		
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
		
	}

	private void setImage(Map<Object, Object> store){
		if(!StringUtil.hasText(store.get("ac_image"))){
			store.put("ac_image", baseDao.getFieldDataByCondition("ActivityType", "at_image", "at_id = " + store.get("ac_atid")));
		}
		
		if(!StringUtil.hasText(store.get("ac_icon"))){
			store.put("ac_icon", baseDao.getFieldDataByCondition("ActivityType", "at_icon", "at_id = " + store.get("ac_atid")));
		}
		
		if(!StringUtil.hasText(store.get("ac_status"))||"垃圾箱".equals(store.get("ac_status"))){
			store.put("ac_status", "草稿箱");
		}
		store.put("ac_update", DateUtil.format(new Date(), Constant.YMD_HMS));
	}
	
	//保存活动
	public void saveActivity(String caller, String formStore){	
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		
		if(!StringUtil.hasText(store.get("ac_id"))){
			store.put("ac_id", baseDao.getSeqId("ACTIVITYCENTER_SEQ"));
		}
		
		setImage(store);
		
		//处理超长字符
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		Object value = null;
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		// 保存ac_id
		String formSql = SqlUtil.getInsertSqlByMap(store, "ActivityCenter");
		baseDao.execute(formSql);
		baseDao.saveClob("ActivityCenter", clobFields, clobStrs, "ac_id=" + store.get("ac_id"));
		// 记录操作
		baseDao.logger.save(caller, "ac_id", store.get("ac_id"));
		
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}
	
	//更新活动
	public void updateActivity(String caller, String formStore){	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		
		setImage(store);
		
		//处理超长字符
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		Object value = null;
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		
		// 修改ActivityCenter
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ActivityCenter", "ac_id");
		baseDao.execute(formSql);
		baseDao.saveClob("ActivityCenter", clobFields, clobStrs, "ac_id=" + store.get("ac_id"));
		
		// 记录操作
		baseDao.logger.update(caller, "ac_id", store.get("ac_id"));
		
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
	
	//删除活动
	public void deleteActivity(String caller, int id){
		
		// 只能删除未发布的活动!
		boolean bool = baseDao.checkIf("ActivityCenter", "ac_id = " + id +" and nvl(ac_status,'草稿箱') = '进行中'");
		if (bool) {
			BaseUtil.showError("活动进行中，不能删除！");
		}
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, id);
		
		//更新活动数量
		Object[] atid = baseDao.getFieldsDataByCondition("ActivityCenter", new String[]{"ac_atid","ac_status"}, "ac_id = "+id);
		if (atid!=null&&atid[1]!=null&&"已结束".equals(atid[1])) {
			baseDao.updateByCondition("ActivityType", "at_count = nvl(at_count,0)-1", "at_id = "+atid[0]);
		}
		
		// 删除ActivityCenter
		baseDao.deleteById("ActivityCenter", "ac_id", id);
		
		//取消定时任务
		String sob = SpObserver.getSp();
		EndActivityTask.closeTask(sob, id);
		
		// 记录操作
		baseDao.logger.delete(caller, "ac_id", id);
		
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, id);
	}
	
	//发布活动
	public void publishActivity(String caller, int id){	
		
		boolean bool = baseDao.checkIf("ActivityCenter", "ac_id = " + id +" and nvl(ac_status,'草稿箱') = '进行中'");
		if (bool) {
			BaseUtil.showError("活动进行中，不用重复发布！");
		}
		
		Object [] dates = baseDao.getFieldsDataByCondition("ActivityCenter", new String[] {"ac_startdate","ac_enddate"}, "ac_id = " + id);
		
		if(DateUtil.format(new Date(), Constant.YMD_HMS).compareTo(String.valueOf(dates[0]))>0){
			BaseUtil.showError("活动开始时间已过！");
		}
		
		if(String.valueOf(dates[0]).compareTo(String.valueOf(dates[1]))>0){
			BaseUtil.showError("活动开始时间不能大于活动结束时间！");
		}
		
		Object atid = baseDao.getFieldDataByCondition("ActivityCenter", "ac_atid", "ac_id = "+id);
		bool = baseDao.checkByCondition("ActivityType", "at_id ="+atid);
		if (bool) {
			BaseUtil.showError("该活动分类不存在！");
		}
		
		Employee employee = SystemSession.getUser();
		baseDao.updateByCondition("ActivityCenter", "ac_status = '进行中',ac_enternum = 0,ac_publisher = '"+employee.getEm_name()+"',ac_publishdate = "+DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), "ac_id = " + id);
		
		//更新活动数量
		baseDao.updateByCondition("ActivityType", "at_count = nvl(at_count,0)+1", "at_id = "+atid);
		
		//启动定时任务
		Date endDate = DateUtil.parse(String.valueOf(dates[1]), Constant.YMD_HMS);
		endActivityTask.addTask(endDate, id);
		
		//记录日志
		baseDao.logger.others("发布活动", "发布成功", caller, "ac_id", id);
	}
	
	@Override
	public void cancelActivity(String caller, int id) {
		boolean bool = baseDao.checkIf("ActivityCenter", "ac_id = " + id +" and nvl(ac_status,'草稿箱') <> '进行中'");
		if (bool) {
			BaseUtil.showError("活动未发布，不用重复撤销！");
		}
		
		baseDao.updateByCondition("ActivityCenter", "ac_status = '垃圾箱',ac_publisher = '',ac_publishdate = ''", "ac_id = " + id);
		
		//更新活动数量
		Object atid = baseDao.getFieldDataByCondition("ActivityCenter", "ac_atid", "ac_id = "+id);
		baseDao.updateByCondition("ActivityType", "at_count = nvl(at_count,0)-1", "at_id = "+atid);
		
		//取消定时任务
		String sob = SpObserver.getSp();
		EndActivityTask.closeTask(sob, id);
		
		//记录日志
		baseDao.logger.others("取消活动", "取消成功", caller, "ac_id", id);
		
	}

	@Override
	public void advanceEndActivity(String caller, int id) {
		boolean bool = baseDao.checkIf("ActivityCenter", "ac_id = " + id +" and nvl(ac_status,'草稿箱') <> '进行中'");
		if (bool) {
			BaseUtil.showError("活动未发布，不用提前结束！");
		}
		
		baseDao.updateByCondition("ActivityCenter", "ac_status = '已结束'", "ac_id = " + id);
		
		//取消定时任务
		String sob = SpObserver.getSp();
		EndActivityTask.closeTask(sob, id);
		
		//记录日志
		baseDao.logger.others("提前结束活动", "提前结束成功", caller, "ac_id", id);
		
	}
	
}
