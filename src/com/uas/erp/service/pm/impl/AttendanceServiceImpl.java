package com.uas.erp.service.pm.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.AttendanceService;

@Service("AttendanceService")
public class AttendanceServiceImpl implements AttendanceService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public int copyAttendance(int id, String caller) {
		int atid = 0;
		try {
			SqlRowList rs = baseDao.queryForRowSet("select * from attendance where at_id=?", new Object[]{id});
			atid = baseDao.getSeqId("ATTENDANCE_SEQ");
			String code = baseDao.sGetMaxNumber("Attendance", 2);
			if(rs.next()){
				Map<String, Object> diffence = new HashMap<String, Object>();
				diffence.put("at_id", atid);
				diffence.put("at_code", "'" + code + "'");
				diffence.put("at_indate", "sysdate");
				diffence.put("at_recordman", "'" + SystemSession.getUser().getEm_name() + "'");
				diffence.put("at_statuscode", "'ENTERING'");
				diffence.put("at_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
				// 转入主表
				baseDao.copyRecord("Attendance", "Attendance", "at_id=" + id, diffence);
				// 转入从表
				rs = baseDao.queryForRowSet("SELECT ad_id FROM AttendanceDetail WHERE ad_atid=? order by ad_detno", id);
				diffence = new HashMap<String, Object>();
				diffence.put("ad_atid", atid);
				diffence.put("ad_code", "'" + code + "'");
				while (rs.next()) {
					diffence.put("ad_id", baseDao.getSeqId("ATTENDANCEDETAIL_SEQ"));
					baseDao.copyRecord("AttendanceDetail", "AttendanceDetail", "ad_id=" + rs.getInt("ad_id"), diffence);
				}
			}
			return atid;
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}

	@Override
	public void saveAttendance(String formStore, String gridStore, String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Attendance", "at_code='" + store.get("at_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//相同日期、相同车间、相同线别的只能存在一张生产日报
		if(baseDao.getCount("select count(*) from Attendance where to_char(at_attenddate,'yyyy-mm-dd')='"+store.get("at_attenddate")+
				"' and at_department='"+store.get("at_department")+"' and at_lowdepartname='"+store.get("at_lowdepartname")+"'")>0){
			BaseUtil.showError("相同日期、相同车间、相同线别的只能存在一张生产日报");
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store, grid});
		//保存LossWorkTime
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Attendance", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存LossWorkTimeDetail		
		for(Map<Object, Object> s:grid){
			s.put("ad_id", baseDao.getSeqId("AttendanceDETAIL_SEQ"));
			//s.put("atd_code", store.get("at_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "AttendanceDetail");
		baseDao.execute(gridSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "at_code", store.get("at_code"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//baseDao.execute("update Attendance set at_recorder='"+employee.getEm_name()+"' where lw_code='" + store.get("lw_code") + "'");
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store, grid});
	}

	@Override
	public void updateAttendance(String formStore, String gridStore,
			String caller) {
		// TODO Auto-generated method stub
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Attendance", "at_statuscode", "at_id=" + store.get("at_id"));
		StateAssert.updateOnlyEntering(status);
		//相同日期、相同车间、相同线别的只能存在一张生产日报
		if(baseDao.getCount("select count(*) from Attendance where to_char(at_attenddate,'yyyy-mm-dd')='"+store.get("at_attenddate")+
				"' and at_department='"+store.get("at_department")+"' and at_lowdepartname='"+store.get("at_lowdepartname")+"' and at_id<>"+store.get("at_id"))>0){
			BaseUtil.showError("相同日期、相同车间、相同线别的只能存在一张生产日报");
		}		
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store, gstore});
		//修改LossWorkTime
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Attendance", "at_id");
		baseDao.execute(formSql);
		//修改LossWorkTimeDetail		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "AttendanceDetail", "ad_id");
		for(Map<Object, Object> s:gstore){
			if (s.get("ad_id") == null || s.get("ad_id").equals("") || s.get("ad_id").equals("0")
					|| Integer.parseInt(s.get("ad_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("AttendanceDETAIL_SEQ");
				/*s.put("lwd_code", store.get("at_code"));*/
				String sql = SqlUtil.getInsertSqlByMap(s, "AttendanceDetail", new String[] { "ad_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "at_id", store.get("at_id"));
		
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, gstore});
	}

	@Override
	public void deleteAttendance(int id, String caller) {
		// TODO Auto-generated method stub
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{id});
		//删除LossWorkTime
		baseDao.deleteById("Attendance", "at_id", id);
		//删除LossWorkTimeDetail
		baseDao.deleteById("AttendanceDetail", "ad_atid", id);
		//记录操作
		baseDao.logger.delete(caller,"at_id", id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{id});
	}

	@Override
	public void submitAttendance(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Attendance", "at_statuscode", "at_id=" + id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{id});
		//执行提交操作
		baseDao.submit("Attendance", "at_id=" + id, "at_status", "at_statuscode");
		//记录操作		
		baseDao.logger.submit(caller, "at_id", id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{id});
	}

	@Override
	public void resSubmitAttendance(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Attendance", "at_statuscode", "at_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{id});
		//执行反提交操作
		baseDao.resOperate("Attendance", "at_id=" + id, "at_status", "at_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "at_id", id);
		handlerService.afterResSubmit(caller, new Object[]{id});
	}

	@Override
	public void auditAttendance(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Attendance", "at_statuscode", "at_id=" + id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{id});
		//执行审核操作
		baseDao.audit("Attendance", "at_id=" + id, "at_status", "at_statuscode","at_auditdate","at_auditman");
		//记录操作
		baseDao.logger.audit(caller, "at_id", id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{id});
	}

	@Override
	public void resAuditAttendance(int id, String caller) {
		// TODO Auto-generated method stub
		Object status = baseDao.getFieldDataByCondition("Attendance", "at_statuscode", "at_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Attendance", "at_id=" + id, "at_status", "at_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "at_id", id);
	}
}
