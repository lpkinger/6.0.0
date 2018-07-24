package com.uas.erp.service.oa.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import com.uas.erp.service.oa.MeetingRoomService;

@Service
public class MeetingRoomServiceImpl implements MeetingRoomService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMeetingRoom(String formStore,String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		int count=baseDao.getCount("select count(1) from meetingroom where mr_name='"+store.get("mr_name")+"'");		
		if(count!=0){
			BaseUtil.showError("此会议室名称已存在！");
		}
		int count1=baseDao.getCount("select count(1) from meetingroom where mr_code='"+store.get("mr_code")+"'");		
		if(count1!=0){
			BaseUtil.showError("此会议室编号已存在！");
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MeetingRoom", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		//保存equipment	
		for(Map<Object, Object> m:grid){
			m.put("eq_id", baseDao.getSeqId("EQUIPMENT_SEQ"));
				}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "EQUIPMENT");
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.save(caller, "mr_id", store.get("mr_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateMeetingRoom(String formStore,String gridStore,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		int count=baseDao.getCount("select count(1) from meetingroom where mr_id<> "+store.get("mr_id")+" and mr_name='"+store.get("mr_name")+"'");
		if(count!=0){
			BaseUtil.showError("此会议室名称已存在！");
		}
		int count1=baseDao.getCount("select count(1) from meetingroom where mr_id<> "+store.get("mr_id")+" and mr_code='"+store.get("mr_code")+"'");
		if(count1!=0){
			BaseUtil.showError("此会议室编号已存在！");
		}
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 修改MeetingRoom
		store.put("mr_updateman", SystemSession.getUser().getEm_name());//更新人
		store.put("mr_updatedate", DateUtil.currentDateString(null));//更新时间
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MeetingRoom", "mr_id");
		baseDao.execute(formSql);
		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "EQUIPMENT", "eq_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("eq_id") == null || s.get("eq_id").equals("") || s.get("eq_id").equals("0") ||
					Integer.parseInt(s.get("eq_id").toString()) == 0){//新添加的数据，id不存在
				s.put("eq_id", baseDao.getSeqId("EQUIPMENT_SEQ"));
				String sql = SqlUtil.getInsertSqlByMap(s, "EQUIPMENT");
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.delete(caller, "mr_id", store.get("mr_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });

	}

	@Override
	public void deleteMeetingRoom(int mr_id, String  caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[] { mr_id});
		// 删除purchase
		baseDao.deleteById("MeetingRoom", "mr_id", mr_id);
		
		baseDao.deleteById("EQUIPMENT", "eq_mrid", mr_id);
		// 记录操作
		baseDao.logger.delete(caller, "mr_id", mr_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[] { mr_id});
	}

	@Override
	public void auditMeetingRoom(int mr_id, String  caller) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[] { mr_id });
		// 执行审核操作
		baseDao.audit("MeetingRoom", "mr_id=" + mr_id, "mr_status", "mr_statuscode", "mr_auditdate", "mr_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "mr_id", mr_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[] { mr_id });
	}

	@Override
	public void resAuditMeetingRoom(int mr_id, String caller) {
		String mr_code = baseDao.getFieldDataByCondition("MeetingRoom", "mr_code", "mr_id='" +mr_id+"'").toString();
		int count1=baseDao.getCount("select count(1) from Meetingroomapply where ma_mrcode='"+mr_code+"'");//是否在会议申请中使用
		int count2=baseDao.getCount("select count(1) from MeetingChange where mc_newmrcode='"+mr_code+"'");//是否在会议变更中使用
		int count3=baseDao.getCount("select count(1) from StandMeeting where sm_mrcode='"+mr_code+"'");//是否在会议变更中使用
		int count4=baseDao.getCount("select count(1) from MeetingDoc where md_mrcode='"+mr_code+"'");//是否在会议变更中使用
		if(count1!=0||count2!=0||count3!=0||count4!=0){
			BaseUtil.showError("此会议室已在其他单据中被使用，不能反审核！");
		}
		// 执行反审核操作
		baseDao.resAudit("MeetingRoom", "mr_id=" + mr_id, "mr_status", "mr_statuscode", "mr_auditdate", "mr_auditman");
		//baseDao.resOperate("MeetingRoom",  "mr_id=" + mr_id, "mr_status", "mr_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "mr_id", mr_id);
	}

	@Override
	public void submitMeetingRoom(int mr_id, String  caller) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,new Object[] { mr_id });
		// 执行提交操作
		baseDao.submit("MeetingRoom", "mr_id=" + mr_id, "mr_status", "mr_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mr_id", mr_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller,new Object[] { mr_id });
	}

	@Override
	public void resSubmitMeetingRoom(int mr_id, String caller) {
		//执行反提交前的其它逻辑
		handlerService.beforeResSubmit(caller, mr_id);
		// 执行反提交操作
		baseDao.resOperate("MeetingRoom", "mr_id=" + mr_id, "mr_status", "mr_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mr_id", mr_id);
		//执行提交后的其它逻辑
		handlerService.afterResSubmit(caller, mr_id);
	}

	@Override
	public String showapply(String gridStore, String condition) {
		List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		List<Object> objects = baseDao.getFieldDatasByCondition("Meetingstage", "ms_name", " 1=1 order by ms_detno");
		Map<Object, Object> name = new HashMap<Object, Object>();
		for (Object object : objects) {
			Object o = baseDao.getFieldDataByCondition("Meetingstage", "ms_remark", "ms_name='" + object + "'");
			name.put(object, o);
		}
		for (Map<Object, Object> mapa : grid) {
			for (Object object : objects) {
				String conditions = null;
				if (condition == null) {
					String d = DateUtil.parseDateToOracleString(null, new Date());
					conditions = " mad_mrcode='" +
							mapa.get("mr_code") + "' and mad_stage='" + object + "' and mad_date=" + d;
				} else {
					condition = condition.replaceAll("ma_date", "mad_date");
					conditions = " mad_mrcode='" +
							mapa.get("mr_code") + "' and mad_stage='" + object + "' and " + condition;
				}
				int count = baseDao.getCountByCondition("Meetingroomapplydet", conditions);
				List<Object> result = new ArrayList<Object>();
				result.add("0");
				if (count > 0) {
					result = baseDao.getFieldDatasByCondition("Meetingroomapplydet", "mad_maid", conditions);
				}
				// Object o=baseDao.getFieldDataByCondition("Meetingstage",
				// "ms_remark", "ms_name='"+object+"'");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("mamrcode", mapa.get("mr_code"));
				map.put("name", name.get(object));
				map.put("result", result.get(0));
				maps.add(map);
			}
		}
		return BaseUtil.parseGridStore2Str(maps);
	}

}
