package com.uas.erp.service.pm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.ProdReplaceSonService;


@Service("prodReplaceSonService")
public class ProdReplaceSonServiceImpl implements ProdReplaceSonService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveProdReplaceSon(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOM", "bo_code='" + store.get("bo_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_bocodeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store,gstore});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOM", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		////保存Detail
		Object[] pre_id = new Object[1];
		if(gridStore.contains("},")){//明细行有多行数据哦
			String[] datas = gridStore.split("},");
			pre_id = new Object[datas.length];
			for(int i=0;i<datas.length;i++){
				pre_id[i] = baseDao.getSeqId("PRODREPLACE_SEQ");
			}
		} else {
			pre_id[0] = baseDao.getSeqId("PRODREPLACE_SEQ");
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(gridStore, "ProdReplace", "pre_id", pre_id);
		baseDao.execute(gridSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "bo_id", store.get("bo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store,gstore});
	}
	
	@Override
	public void deleteProdReplaceSon(int bo_id, String  caller) {
		//只能删除在录入的采购单!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{bo_id});
		//删除
		baseDao.deleteById("BOM", "bo_id", bo_id);
		//删除明细
		baseDao.deleteById("PRODREPLACE", "pre_bomid", bo_id);
		//记录操作
		baseDao.logger.delete(caller, "bo_id", bo_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{bo_id});
	}
	
	@Override
	public void updateProdReplaceSonById(String formStore, String gridStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		//只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + store.get("bo_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store,gstore});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOM", "bo_id");
		baseDao.execute(formSql);
		//修改Detail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "ProdReplace", "pre_id");
		for(Map<Object, Object> s:gstore){
			if(s.get("pre_id") == null || s.get("pre_id").equals("") || s.get("pre_id").equals("0")){//新添加的数据，id不存在
				int id = baseDao.getSeqId("PRODREPLACE_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "ProdReplace", new String[]{"pre_id"}, new Object[]{id});
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		//记录操作
		baseDao.logger.update(caller, "bp_id", store.get("bo_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store,gstore});
	}
	
	@Override
	public void auditProdReplaceSon(int bo_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{bo_id});
		//执行审核操作
		baseDao.audit("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode", "bo_auditdate", "bo_auditman");
		//记录操作
		baseDao.logger.audit(caller, "bo_id", bo_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{bo_id});
	}
	@Override
	public void resAuditProdReplaceSon(int bo_id, String  caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "bo_id", bo_id);
	}
	@Override
	public void submitProdReplaceSon(int bo_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{bo_id});
		//执行提交操作
		baseDao.submit("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bo_id", bo_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{bo_id});
	}
	@Override
	public void resSubmitProdReplaceSon(int bo_id, String  caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{bo_id});
		//执行反提交操作
		baseDao.resOperate("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bo_id", bo_id);
		handlerService.afterResSubmit(caller, new Object[]{bo_id});
	}
}
