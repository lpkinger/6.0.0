package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.service.pm.MakeMaterialOccurService;

@Service("makeMaterialOccurService")
public class MakeMaterialOccurServiceImpl implements MakeMaterialOccurService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeMaterialOccur(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeMaterial", "mm_id='" + store.get("mm_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller,  new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeMaterial", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "mm_id", store.get("mm_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//执行保存后的其它逻辑
		handlerService.afterSave(caller,  new Object[]{store});
	}
	@Override
	public void deleteMakeMaterialOccur(int mm_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakeMaterial", "mm_statuscode", "mm_id=" + mm_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{mm_id});
		//删除BOM
		baseDao.deleteById("MakeMaterial", "mm_id", mm_id);		
		//记录操作
		baseDao.logger.delete(caller, "mm_id", mm_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{mm_id});
	}
	
	@Override
	public void updateMakeMaterialOccurById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakeMaterial", "mm_statuscode", "mm_id=" + store.get("mm_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeMaterial", "mm_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "mm_id",store.get("mm_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
		
	@Override
	public void auditMakeMaterialOccur(int mm_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeMaterial", "mm_statuscode", "mm_id=" + mm_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{mm_id});
		//执行审核操作
		baseDao.audit("MakeMaterial", "mm_id=" + mm_id, "mm_status", "mm_statuscode", "mm_audtidate","mm_auditman");
		//记录操作
		baseDao.logger.audit(caller, "mm_id", mm_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{mm_id});
	}
	
	@Override
	public void resAuditMakeMaterialOccur(int mm_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeMaterial", "mm_statuscode", "mm_id=" + mm_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("MakeMaterial", "mm_statuscode='ENTERING',mm_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mm_id=" + mm_id);
		//记录操作
		baseDao.logger.resAudit(caller, "mm_id", mm_id);
	}
	
	@Override
	public void submitMakeMaterialOccur(int mm_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeMaterial", "mm_statuscode", "mm_id=" + mm_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{mm_id});		//执行提交操作
		baseDao.submit("MakeMaterial", "mm_id=" + mm_id, "mm_status", "mm_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "mm_id", mm_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{mm_id});
	}
	
	@Override
	public void resSubmitMakeMaterialOccur(int mm_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeMaterial", "mm_statuscode", "mm_id=" + mm_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{mm_id});
		//执行反提交操作
		baseDao.updateByCondition("MakeMaterial", "mm_statuscode='ENTERING',mm_status='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "mm_id=" + mm_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "mm_id", mm_id);
		handlerService.afterResSubmit(caller, new Object[]{mm_id});
	}	
}
