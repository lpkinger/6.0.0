package com.uas.erp.service.pm.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.service.pm.LossWorkTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LossWorkTimeServiceImpl implements LossWorkTimeService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	public void saveLossWorkTime(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("LossWorkTime", "lw_code='" + store.get("lw_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//相同日期、相同车间、相同线别的只能存在一张生产日报
		if(baseDao.getCount("select count(*) from LossWorkTime where to_char(lw_date,'yyyy-mm-dd')='"+store.get("lw_date")+
				"' and lw_groupcode='"+store.get("lw_groupcode")+"' and lw_lowdepartname='"+store.get("lw_lowdepartname")+"'")>0){
			BaseUtil.showError("相同日期、相同车间、相同线别的只能存在一张生产日报");
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store,grid});		//保存LossWorkTime
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "LossWorkTime", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//保存LossWorkTimeDetail
		
		for(Map<Object, Object> s:grid){
			s.put("lwd_id", baseDao.getSeqId("LossWorkTimeDETAIL_SEQ"));
			s.put("lwd_code", store.get("lw_code"));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "LossWorkTimeDetail");
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "lw_id", store.get("lw_id"));		
		baseDao.execute("update LossWorkTime set lw_recorder='"+SystemSession.getUser().getEm_name()+"' where lw_code='" + store.get("lw_code") + "'");
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,grid});
	}

	public void updateLossWorkTimeById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("LossWorkTime", "lw_statuscode", "lw_id=" + store.get("lw_id"));
		StateAssert.updateOnlyEntering(status);
		//相同日期、相同车间、相同线别的只能存在一张生产日报
		if(baseDao.getCount("select count(*) from LossWorkTime where to_char(lw_date,'yyyy-mm-dd')='"+store.get("lw_date")+
				"' and lw_groupcode='"+store.get("lw_groupcode")+"' and lw_lowdepartname='"+store.get("lw_lowdepartname")+"' and lw_id<>"+store.get("lw_id"))>0){
			BaseUtil.showError("相同日期、相同车间、相同线别的只能存在一张生产日报");
		}
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller,new Object[]{store,gstore});		//修改LossWorkTime
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "LossWorkTime", "lw_id");
		baseDao.execute(formSql);
		//修改LossWorkTimeDetail		
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "LossWorkTimeDetail", "lwd_id");
		for(Map<Object, Object> s:gstore){
			if (s.get("lwd_id") == null || s.get("lwd_id").equals("") || s.get("lwd_id").equals("0")
					|| Integer.parseInt(s.get("lwd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("LossWorkTimeDETAIL_SEQ");
				s.put("lwd_code", store.get("lw_code"));
				String sql = SqlUtil.getInsertSqlByMap(s, "LossWorkTimeDetail", new String[] { "lwd_id" },
						new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "lw_id", store.get("lw_id"));
		
		//执行修改后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
	}

	public void deleteLossWorkTime(int lw_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{lw_id});	
		//删除LossWorkTime
		baseDao.deleteById("LossWorkTime", "lw_id", lw_id);
		//删除LossWorkTimeDetail
		baseDao.deleteById("lossWorkTimeDetail", "lwd_lwid", lw_id);
		//记录操作
		baseDao.logger.delete(caller, "lw_id", lw_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{lw_id});
	}
	@Override
	public void auditLossWorkTime(int lw_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("LossWorkTime", "lw_statuscode", "lw_id=" + lw_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{lw_id});		//执行审核操作
		baseDao.audit("LossWorkTime", "lw_id=" + lw_id, "lw_status", "lw_statuscode", "lw_auditdate", "lw_auditman");
		//记录操作
		baseDao.logger.audit(caller, "lw_id", lw_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{lw_id});
	}
	
	@Override
	public void resAuditLossWorkTime(int lw_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("LossWorkTime", "lw_statuscode", "lw_id=" + lw_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("LossWorkTime", "lw_statuscode='ENTERING',lw_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "lw_id=" + lw_id);
		//记录操作
		baseDao.logger.resAudit(caller, "lw_id", lw_id);
	}
	
	@Override
	public void submitLossWorkTime(int lw_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("LossWorkTime", "lw_statuscode", "lw_id=" + lw_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{lw_id});		//执行提交操作
		baseDao.submit("LossWorkTime", "lw_id=" + lw_id, "lw_status", "lw_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "lw_id", lw_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{lw_id});
	}
	
	@Override
	public void resSubmitLossWorkTime(int lw_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("LossWorkTime", "lw_statuscode", "lw_id=" + lw_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{lw_id});		//执行反提交操作
		baseDao.updateByCondition("LossWorkTime", "lw_statuscode='ENTERING',lw_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "lw_id=" + lw_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "lw_id", lw_id);
		handlerService.afterResSubmit(caller, new Object[]{lw_id});
	}

	@Override
	public int copyLossWorkTime(int id, String caller) {
		int lwid = 0;
		try {
			SqlRowList rs = baseDao.queryForRowSet("select * from LossWorkTime where lw_id=?", new Object[]{id});
			lwid = baseDao.getSeqId("LOSSWORKTIME_SEQ");
			String code = baseDao.sGetMaxNumber("LossWorkTime", 2);
			if(rs.next()){
				Map<String, Object> diffence = new HashMap<String, Object>();
				diffence.put("lw_id", lwid);
				diffence.put("lw_code", "'" + code + "'");
				diffence.put("lw_indate", "sysdate");
				diffence.put("lw_recorder", "'" + SystemSession.getUser().getEm_name() + "'");
				diffence.put("lw_statuscode", "'ENTERING'");
				diffence.put("lw_status", "'" + BaseUtil.getLocalMessage("ENTERING") + "'");
				// 转入主表
				baseDao.copyRecord("LossWorkTime", "LossWorkTime", "lw_id=" + id, diffence);
				// 转入从表
				rs = baseDao.queryForRowSet("SELECT lwd_id FROM LossWorkTimeDetail WHERE lwd_lwid=? order by lwd_detno", id);
				diffence = new HashMap<String, Object>();
				diffence.put("lwd_lwid", lwid);
				diffence.put("lwd_code", "'" + code + "'");
				while (rs.next()) {
					diffence.put("lwd_id", baseDao.getSeqId("LOSSWORKTIMEDETAIL_SEQ"));
					baseDao.copyRecord("LossWorkTimeDetail", "LossWorkTimeDetail", "lwd_id=" + rs.getInt("lwd_id"), diffence);
				}
			}
			return lwid;
		} catch (Exception e){
			e.printStackTrace();
			BaseUtil.showError("数据异常,转入失败");
			return 0;
		}
	}
}
