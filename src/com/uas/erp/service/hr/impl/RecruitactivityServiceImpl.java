package com.uas.erp.service.hr.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.hr.RecruitactivityService;

@Service
public class RecruitactivityServiceImpl implements RecruitactivityService {

	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void saveRecruitactivity(String formStore,String gridStore, String caller) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Recruitactivity", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存RecruitactivityDetail
		Object[] rd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			rd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				rd_id[i] = baseDao.getSeqId("RecruitactivityDETAIL_SEQ");
			}
		} else {
			rd_id[0] = baseDao.getSeqId("RecruitactivityDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "RecruitactivityDetail", "rd_id", rd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "re_id", store.get("re_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}

	@Override
	public void updateRecruitactivityById(String formStore,String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Recruitactivity", "re_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "RecruitactivityDetail", "rd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("rd_id") == null || s.get("rd_id").equals("") || s.get("rd_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("RecruitactivityDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "RecruitactivityDetail", new String[]{"rd_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "re_id", store.get("re_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteRecruitactivity(int re_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{re_id});
		//删除purchase
		baseDao.deleteById("Recruitactivity", "re_id", re_id);
		//删除purchaseDetail
		baseDao.deleteById("Recruitactivitydetail", "rd_reid", re_id);
		//记录操作
		baseDao.logger.delete(caller, "re_id", re_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{re_id});
	}

	@Override
	public void auditRecruitactivity(int re_id, String caller) {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Recruitactivity", "re_statuscode", "re_id=" + re_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,  new Object[]{re_id});
		//执行审核操作
		baseDao.audit("Recruitactivity", "re_id=" + re_id, "re_status", "re_statuscode", "re_auditdate", "re_auditor");
		//记录操作
		baseDao.logger.audit(caller, "re_id", re_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,  new Object[]{re_id});
	}

	@Override
	public void resAuditRecruitactivity(int re_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Recruitactivity", "re_statuscode", "re_id=" + re_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Recruitactivity",  "re_id=" + re_id, "re_status", "re_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "re_id", re_id);
	}

	@Override
	public void submitRecruitactivity(int re_id, String caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Recruitactivity", "re_statuscode", "re_id=" + re_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{re_id});
		//执行提交操作
		baseDao.submit("Recruitactivity", "re_id=" + re_id, "re_status", "re_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "re_id", re_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{re_id});
	}

	@Override
	public void resSubmitRecruitactivity(int re_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Recruitactivity", "re_statuscode", "re_id=" + re_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Recruitactivity", "re_id=" + re_id, "re_status", "re_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "re_id", re_id);
	}
}
