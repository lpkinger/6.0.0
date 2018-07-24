package com.uas.erp.service.oa.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.MeetingService;

@Service("meetingService")
public class MeetingServiceImpl implements MeetingService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMeeting(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,grid});
		//保存Meeting
		store.put("me_numbers", grid.size());
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Meeting", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存MeetingDetail
		for(Map<Object, Object> s:grid){
			s.put("md_id", baseDao.getSeqId("MeetingDETAIL_SEQ"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "MeetingDetail");
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "me_id", store.get("me_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,grid});
	}
	@Override
	public void deleteMeeting(int me_id, String  caller) {
		//只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("Meeting", "me_statuscode", "me_id=" + me_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{me_id });
		//删除Meeting
		baseDao.deleteById("Meeting", "me_id", me_id);
		//删除MeetingDetail
		baseDao.deleteById("Meetingdetail", "md_meid", me_id);
		//记录操作
		baseDao.logger.delete(caller, "me_id", me_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{me_id });
	}
	@Override
	public void deleteDetail(int md_id, String  caller) {
		baseDao.deleteById("Meetingdetail", "md_id", md_id);
	}
	@Override
	public void updateMeetingById(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Meeting", "me_statuscode", "me_id=" + store.get("me_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改Meeting
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Meeting", "me_id");
		baseDao.execute(formSql);
		if(gridStore != null && !gridStore.equals("")){
			//修改MeetingDetail
			List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "MeetingDetail", "md_id");
			baseDao.execute(gridSql);
		}
		//记录操作
		baseDao.logger.update(caller, "me_id", store.get("me_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
	}
	@Override
	public void auditMeeting(int me_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Meeting", "me_statuscode", "me_id=" + me_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{me_id});
		//执行审核操作
		baseDao.audit("Meeting", "me_id=" + me_id, "me_status", "me_statuscode");
		//记录操作
		baseDao.logger.audit(caller, "me_id", me_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{me_id});
	}
	@Override
	public void resAuditMeeting(int me_id, String  caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Meeting", "me_statuscode", "me_id=" + me_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Meeting", "me_id=" + me_id, "me_status", "me_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "me_id", me_id);
	}
	@Override
	public void submitMeeting(int me_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Meeting", "me_statuscode", "me_id=" + me_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{me_id});
		//执行提交操作
		baseDao.submit("Meeting", "me_id=" + me_id, "me_status", "me_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "me_id", me_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{me_id});
	}
	@Override
	public void resSubmitMeeting(int me_id, String  caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Meeting", "me_statuscode", "me_id=" + me_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Meeting", "me_id=" + me_id, "me_status", "me_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "me_id", me_id);
	}
	@Override
	public void changeMeetingStatus(int me_id, String em_code) {
		baseDao.updateByCondition("MeetingDetail", "md_isconfirmed=-1,md_confirmtime=" + 
				DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()), 
				"md_meid=" + me_id + " AND md_emcode='" + em_code + "'");
	}
}
