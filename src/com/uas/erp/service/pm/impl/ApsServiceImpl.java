package com.uas.erp.service.pm.impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ApsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ApsServiceImpl implements ApsService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;

	public void saveAps(String formStore, String gridStore, String caller
			) {	
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeSave(caller, new Object[]{store, grid});
		@SuppressWarnings("deprecation")
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "APSMAIN",
                new String[]{}, new Object[]{});
		baseDao.execute(formSql);

		Object[] ad_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			ad_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				ad_id[i] = baseDao.getSeqId("APSDETAIL_SEQ");
			}
		} else {
			ad_id[0] = baseDao.getSeqId("APSDETAIL_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "APSDETAIL",
                "ad_id", ad_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "am_id", store.get("am_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store, grid});
	}

	public void updateApsById(String formStore, String gridStore,
			String caller ) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		handlerService.beforeUpdate(caller, new Object[]{store,gstore});
		//修改purchase
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "APSMAIN", "am_id");
		baseDao.execute(formSql);
		//修改purchaseDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "APSDETAIL", "ad_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("ad_id") == null || s.get("ad_id").equals("") || s.get("ad_id").toString().equals("0")
					){//新添加的数据，id不存在
				int id = baseDao.getSeqId("APSDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "APSDETAIL", new String[]{"ad_id"},
                        new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "am_id", store.get("am_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store,gstore});
	}


	public void deleteAps(int am_id, String caller ) {		
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{am_id});
		//删除purchase
		baseDao.deleteById("APSMAIN", "am_id", am_id);
		//删除purchaseDetail
		baseDao.deleteById("APSDETAIL", "ad_amid", am_id);
		//记录操作
		baseDao.logger.delete(caller, "am_id", am_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{am_id});
	}

	public void auditAps(int am_id, String caller ) {		
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("APSMAIN", "am_statuscode", "am_id=" + am_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{am_id});
		//执行审核操作
		baseDao.audit("APSMAIN", "am_id=" + am_id, "am_status", "am_statuscode", "am_auditdate", "am_auditman");
		//记录操作
		baseDao.logger.audit(caller, "am_id", am_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{am_id});
	}

    public void resAuditAps(int am_id, String caller ) {	
		Object status = baseDao.getFieldDataByCondition("APSMAIN", "am_statuscode", "am_id=" + am_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("APSMAIN", "am_id=" + am_id, "am_status", "am_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "am_id", am_id);
	}

	public void submitAps(int am_id, String caller ) {	
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("APSMAIN", "am_statuscode", "am_id=" + am_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{am_id});
		//执行提交操作
		baseDao.submit("APSMAIN", "am_id=" + am_id, "am_status", "am_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "am_id",am_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{am_id});

	}

	public void resSubmitAps(int am_id, String caller ) {
		
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("APSMAIN", "am_statuscode", "am_id=" + am_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{am_id});
		//执行反提交操作
		baseDao.resOperate("APSMAIN", "am_id=" + am_id, "am_status", "am_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller,"am_id", am_id);
		handlerService.afterResSubmit(caller, new Object[]{am_id});
	}

}
