package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.PropertyrepairService;

@Service
public class PropertyrepairServiceImpl implements PropertyrepairService {
	
	static final String turnStatus = "update Propertyrepair set pr_isuse='1' where pr_id=?";
	
	static final String turnRepairRecord = "insert into Repairrecord(rr_id,rr_code,rr_name,rr_type," +
			"rr_recordorid,rr_recordor,rr_starttime,rr_endtime,rr_price,rr_repairor,rr_remark)valeus" +
			"(?,?,?,?,?,?,?,?,?,?,?)";
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePropertyrepair(String formStore, String gridStore,
			String  caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Propertyrepair", 
				new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存PropertyrepairDetail
		Object[] pd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				pd_id[i] = baseDao.getSeqId("PropertyrepairDETAIL_SEQ");
			}
		} else {
			pd_id[0] = baseDao.getSeqId("PropertyrepairDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "PropertyrepairDetail",
				"pd_id", pd_id);
		baseDao.execute(gridSql);
		baseDao.execute("update Propertyrepair set pr_amount=(select sum(pd_price) from PropertyrepairDetail where pd_prid="+store.get("pr_id")+") where pr_id="+store.get("pr_id"));
		try{
			//记录操作
			baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[]{store,gstore});

	}

	@Override
	public void updatePropertyrepairById(String formStore, String gridStore,
			String  caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Propertyrepair", "pr_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PropertyrepairDetail", "pd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").toString().equals("0")
					){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PropertyrepairDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PropertyrepairDetail", new String[]{"pd_id"}, 
						new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		baseDao.execute("update Propertyrepair set pr_amount=(select sum(pd_price) from PropertyrepairDetail where pd_prid="+store.get("pr_id")+") where pr_id="+store.get("pr_id"));
		
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});

	}

	@Override
	public void deletePropertyrepair(int pr_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{pr_id});
		//删除purchase
		baseDao.deleteById("Propertyrepair", "pr_id", pr_id);
		//删除purchaseDetail
		baseDao.deleteById("Propertyrepairdetail", "pd_prid", pr_id);
		//记录操作
		baseDao.logger.delete(caller, "pr_id", pr_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{pr_id});
	}

	@Override
	public void auditPropertyrepair(int pr_id, String caller) {
		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Propertyrepair", "pr_statuscode", "pr_id=" + pr_id);
	    StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{pr_id});
		//执行审核操作
		baseDao.audit("Propertyrepair", "pr_id=" + pr_id, "pr_status", "pr_statuscode", "pr_auditdate", "pr_auditor");
		//记录操作
		baseDao.logger.audit(caller, "pr_id", pr_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{pr_id});

	}

	@Override
	public void resAuditPropertyrepair(int pr_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Propertyrepair", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Propertyrepair", "pr_id=" + pr_id, "pr_status","pr_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pr_id", pr_id);

	}

	@Override
	public void submitPropertyrepair(int pr_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Propertyrepair", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{pr_id});
		//执行提交操作
		baseDao.submit("Propertyrepair", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "pr_id", pr_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{pr_id});

	}

	@Override
	public void resSubmitPropertyrepair(int pr_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Propertyrepair", "pr_statuscode", "pr_id=" + pr_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Propertyrepair", "pr_id=" + pr_id, "pr_status", "pr_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pr_id", pr_id);

	}

	@Override
	public void turnRepairRecords(int id, String griddata,
			String  caller) {

		JSONArray gridjArray = JSONArray.fromObject(griddata);
		JSONObject gridjJson = new JSONObject();
		for(int i=0;i<gridjArray.size();i++){
			gridjJson = gridjArray.getJSONObject(i);
			baseDao.execute(turnRepairRecord, new Object[]{baseDao.getSeqId("Repairrecord_SEQ"),gridjJson.getString("pd_code"),
					gridjJson.getString("pd_name"),gridjJson.getString("pd_type"),SystemSession.getUser().getEm_id(),
					SystemSession.getUser().getEm_name(),gridjJson.getString("pd_starttime"),gridjJson.getString("pd_endtime"),
					gridjJson.getString("pd_price"),gridjJson.getString("pd_repairor"),gridjJson.getString("pd_remark"),});
			
		}
		baseDao.execute(turnStatus, new Object[]{id});
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("oa.turnRepairRecords"), BaseUtil.getLocalMessage("oa.turnRepairRecordsSuccess"), caller, "pr_id", id);
	}

}
