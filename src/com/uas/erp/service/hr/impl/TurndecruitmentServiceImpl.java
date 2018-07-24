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
import com.uas.erp.service.hr.TurndecruitmentService;

@Service
public class TurndecruitmentServiceImpl implements TurndecruitmentService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	

	@Override
	public void saveTurndecruitment(String formStore, String gridStore,
			String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Turndecruitment", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存TurndecruitmentDetail
		Object[] td_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			td_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				td_id[i] = baseDao.getSeqId("TurndecruitmentDETAIL_SEQ");
			}
		} else {
			td_id[0] = baseDao.getSeqId("TurndecruitmentDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "TurndecruitmentDetail", "td_id", td_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "td_id", store.get("td_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});
	}

	@Override
	public void updateTurndecruitmentById(String formStore, String gridStore,
			String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Turndecruitment", "td_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "TurndecruitmentDetail", "td_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("td_id") == null || s.get("td_id").equals("") || s.get("td_id").toString().equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("TurndecruitmentDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "TurndecruitmentDetail", new String[]{"td_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "td_id", store.get("td_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store,gstore});
	}

	@Override
	public void deleteTurndecruitment(int td_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{td_id});
		//删除purchase
		baseDao.deleteById("Turndecruitment", "td_id", td_id);
		//删除purchaseDetail
		baseDao.deleteById("Turndecruitmentdetail", "td_tpid", td_id);
		//记录操作
		baseDao.logger.delete(caller, "td_id", td_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{td_id});
	}

	@Override
	public void auditTurndecruitment(int td_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Turndecruitment", "td_statuscode", "td_id=" + td_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{td_id});
		//执行审核操作
		baseDao.audit("Turndecruitment", "td_id=" + td_id, "td_status", "td_statuscode", "ta_auditdate", "ta_auditor");
		//记录操作
		baseDao.logger.audit(caller, "td_id", td_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{td_id});
	}

	@Override
	public void resAuditTurndecruitment(int td_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("Turndecruitment", "td_statuscode", "td_id=" + td_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Turndecruitment", "td_id=" + td_id, "td_status", "td_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "td_id", td_id);
	}

	@Override
	public void submitTurndecruitment(int td_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Turndecruitment", "td_statuscode", "td_id=" + td_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{td_id});
		//执行提交操作
		baseDao.submit("Turndecruitment", "td_id=" + td_id, "td_status", "td_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "td_id", td_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{td_id});
	}

	@Override
	public void resSubmitTurndecruitment(int td_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Turndecruitment", "td_statuscode", "td_id=" + td_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Turndecruitment", "td_id=" + td_id, "td_status", "td_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "td_id", td_id);
	}
}
