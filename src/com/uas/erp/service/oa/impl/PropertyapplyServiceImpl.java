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
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.PropertyapplyService;

@Service
public class PropertyapplyServiceImpl implements PropertyapplyService {
	
	static final String update = "update Propertyapply set pa_isuse='1' where pa_id=?";

	static final String updateProperty = "update Propert set pr_isuse='已使用' where pr_code=?";
	
	static final String returnupdate = "update Propertyapply set pa_isover='1' where pa_id=?";

	static final String returnProperty = "update Propert set pr_isuse='未使用' where pr_code=?";
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void savePropertyapply(String formStore, String gridStore,
			String  caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Propertyapply", 
				new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存PropertyapplyDetail
		Object[] pd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				pd_id[i] = baseDao.getSeqId("PropertyapplyDETAIL_SEQ");
			}
		} else {
			pd_id[0] = baseDao.getSeqId("PropertyapplyDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "PropertyapplyDetail",
				"pd_id", pd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});

	}

	@Override
	public void updatePropertyapplyById(String formStore, String gridStore,
			String  caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Propertyapply", "pa_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PropertyapplyDetail", "pd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").toString().equals("0")
					){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PropertyapplyDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PropertyapplyDetail", new String[]{"pd_id"}, 
						new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store,gstore});

	}

	@Override
	public void deletePropertyapply(int pa_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{pa_id});
		//删除purchase
		baseDao.deleteById("Propertyapply", "pa_id", pa_id);
		//删除purchaseDetail
		baseDao.deleteById("Propertyapplydetail", "pd_paid", pa_id);
		//记录操作
		baseDao.logger.delete(caller, "pa_id", pa_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{pa_id});

	}

	@Override
	public void auditPropertyapply(int pa_id, String  caller) {
		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Propertyapply", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{pa_id});
		//执行审核操作
		baseDao.audit("Propertyapply",  "pa_id=" + pa_id, "pa_status", "pa_statuscode", "pa_auditdate", "pa_auditor");
		//记录操作
		baseDao.logger.audit(caller, "pa_id", pa_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{pa_id});

	}

	@Override
	public void resAuditPropertyapply(int pa_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Propertyapply", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Propertyapply", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pa_id", pa_id);

	}

	@Override
	public void submitPropertyapply(int pa_id, String caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Propertyapply", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,  new Object[]{pa_id});
		//执行提交操作
		baseDao.submit("Propertyapply", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "pa_id", pa_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{pa_id});

	}

	@Override
	public void resSubmitPropertyapply(int pa_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Propertyapply", "pa_statuscode", "pa_id=" + pa_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Propertyapply", "pa_id=" + pa_id, "pa_status", "pa_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pa_id", pa_id);
	}

	@Override
	public void getProperty(int id, String  caller,
			String param) {
		JSONArray gridjArray = JSONArray.fromObject(param);
		String propertyCode;
		JSONObject gridjJson = new JSONObject();
		for(int i=0;i<gridjArray.size();i++){
			gridjJson = gridjArray.getJSONObject(i);
			propertyCode = gridjJson.getString("pd_code");
			baseDao.execute(updateProperty, new Object[]{propertyCode});
		}
		baseDao.execute(update, new Object[]{id});
		
		baseDao.logger.getMessageLog(BaseUtil.getLocalMessage("oa.getProperty"), BaseUtil.getLocalMessage("oa.getPropertySuccess"), caller, "pa_id", id);
	}

	@Override
	public void ReturnProperty(int id, String  caller,
			String param) {	
		JSONArray gridjArray = JSONArray.fromObject(param);
		String propertyCode;
		JSONObject gridjJson = new JSONObject();
		for(int i=0;i<gridjArray.size();i++){
			gridjJson = gridjArray.getJSONObject(i);
			propertyCode = gridjJson.getString("pd_code");
			baseDao.execute(returnProperty, new Object[]{propertyCode});
		}
		baseDao.execute(returnupdate, new Object[]{id});
		
		baseDao.logger.getMessageLog( BaseUtil.getLocalMessage("oa.ReturnProperty"), BaseUtil.getLocalMessage("oa.ReturnPropertySuccess"), caller, "pa_id", id);
		
	}
}
