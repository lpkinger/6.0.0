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
import com.uas.erp.service.hr.TrainService;

@Service
public class TrainServiceImpl implements TrainService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveTrain(String formStore, String gridStore, String caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller,  new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Train", 
				new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存TrainDetail
		Object[] td_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			td_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				td_id[i] = baseDao.getSeqId("TrainDETAIL_SEQ");
			}
		} else {
			td_id[0] = baseDao.getSeqId("TrainDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "TrainDetail",
				"td_id", td_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "tr_id", store.get("tr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store,gstore});
	}

	@Override
	public void updateTrainById(String formStore, String gridStore,
			String caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Train", "tr_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "TrainDetail", "td_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("td_id") == null || s.get("td_id").equals("") || s.get("td_id").toString().equals("0")
					){//新添加的数据，id不存在
				int id = baseDao.getSeqId("TrainDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "TrainDetail", new String[]{"td_id"}, 
						new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "tr_id", store.get("tr_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}

	@Override
	public void deleteTrain(int tr_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{tr_id});
		//删除purchase
		baseDao.deleteById("Train", "tr_id", tr_id);
		//删除purchaseDetail
		baseDao.deleteById("Traindetail", "td_trid", tr_id);
		//记录操作
		baseDao.logger.delete(caller, "tr_id", tr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{tr_id});
	}

	@Override
	public void auditTrain(int tr_id, String caller) {
		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Train", "tr_statuscode", "tr_id=" + tr_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{tr_id});
		//执行审核操作
		baseDao.audit("Train", "tr_id=" + tr_id, "tr_status", "tr_statuscode", "tr_auditdate", "tr_auditman");
		//记录操作
		baseDao.logger.audit(caller, "tr_id", tr_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{tr_id});

	}

	@Override
	public void resAuditTrain(int tr_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Train", "tr_statuscode", "tr_id=" + tr_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Train",  "tr_id=" + tr_id, "tr_status", "tr_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "tr_id", tr_id);

	}

	@Override
	public void submitTrain(int tr_id, String caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Train", "tr_statuscode", "tr_id=" + tr_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{tr_id});
		//执行提交操作
		baseDao.submit("Train", "tr_id=" + tr_id, "tr_status", "tr_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "tr_id", tr_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{tr_id});
	}

	@Override
	public void resSubmitTrain(int tr_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Train", "tr_statuscode", "tr_id=" + tr_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Train", "tr_id=" + tr_id, "tr_status", "tr_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "tr_id", tr_id);

	}

}
