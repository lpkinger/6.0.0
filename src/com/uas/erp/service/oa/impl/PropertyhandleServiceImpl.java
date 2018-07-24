package com.uas.erp.service.oa.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.PropertyhandleService;

@Service
public class PropertyhandleServiceImpl implements PropertyhandleService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void savePropertyhandle(String formStore, String gridStore,
			String  caller) {
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,  new Object[]{store,gstore});
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Propertyhandle", 
				new String[]{}, new Object[]{});
		baseDao.execute(formSql);
	////保存PropertyhandleDetail
		Object[] pd_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pd_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				pd_id[i] = baseDao.getSeqId("PropertyhandleDETAIL_SEQ");
			}
		} else {
			pd_id[0] = baseDao.getSeqId("PropertyhandleDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "PropertyhandleDetail",
				"pd_id", pd_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ph_id", store.get("ph_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store,gstore});
	}

	@Override
	public void updatePropertyhandleById(String formStore, String gridStore,
			String  caller) {
		
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Propertyhandle", "ph_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "PropertyhandleDetail", "pd_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pd_id") == null || s.get("pd_id").equals("") || s.get("pd_id").toString().equals("0")
					){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PropertyhandleDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "PropertyhandleDetail", new String[]{"pd_id"}, 
						new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "ph_id", store.get("ph_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});

	}

	@Override
	public void deletePropertyhandle(int ph_id, String caller) {
		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{ph_id});
		//删除purchase
		baseDao.deleteById("Propertyhandle", "ph_id", ph_id);
		//删除purchaseDetail
		baseDao.deleteById("Propertyhandledetail", "pd_phid", ph_id);
		//记录操作
		baseDao.logger.delete(caller, "ph_id", ph_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{ph_id});

	}

	@Override
	public void auditPropertyhandle(int ph_id, String caller) {

		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Propertyhandle", "ph_statuscode", "ph_id=" + ph_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{ph_id});
		//执行审核操作
		baseDao.audit("Propertyhandle", "ph_id=" + ph_id, "ph_status", "ph_statuscode", "ph_auditdate", "ph_auditor");
		//记录操作
		baseDao.logger.audit(caller, "ph_id", ph_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{ph_id});
	}

	@Override
	public void resAuditPropertyhandle(int ph_id, String caller) {
		
		Object status = baseDao.getFieldDataByCondition("Propertyhandle", "ph_statuscode", "ph_id=" + ph_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("Propertyhandle", "ph_id=" + ph_id, "ph_status", "ph_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "ph_id", ph_id);

	}

	@Override
	public void submitPropertyhandle(int ph_id, String caller) {
		
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Propertyhandle", "ph_statuscode", "ph_id=" + ph_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{ph_id});
		//执行提交操作
		baseDao.submit("Propertyhandle", "ph_id=" + ph_id, "ph_status", "ph_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "ph_id", ph_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{ph_id});

	}

	@Override
	public void resSubmitPropertyhandle(int ph_id, String caller) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Propertyhandle", "ph_statuscode", "ph_id=" + ph_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("Propertyhandle", "ph_id=" + ph_id, "ph_status", "ph_statuscode");
		baseDao.logger.resSubmit(caller, "ph_id", ph_id);


	}

}
