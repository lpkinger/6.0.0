package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.service.pm.MakeDeleteService;

@Service("makeDeleteService")
public class MakeDeleteServiceImpl implements MakeDeleteService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeDelete(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("Make", "ma_id='" + store.get("ma_id") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Make", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "ma_id", store.get("ma_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deleteMakeDelete(int ma_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller,new Object[]{ma_id});		//删除BOM
		baseDao.deleteById("Make", "ma_id", ma_id);		
		//记录操作
		baseDao.logger.delete(caller, "ma_id", ma_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller,new Object[]{ma_id});
	}
	
	@Override
	public void updateMakeDeleteById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id" + store.get("ma_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Make", "ma_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "ma_id",store.get("ma_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void auditMakeDelete(int ma_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{ma_id});		//执行审核操作
		baseDao.audit("Make",  "ma_id=" + ma_id, "ma_checkstatus", "ma_checkstatuscode", "ma_auditdate", "ma_auditman");
		//记录操作
		baseDao.logger.audit(caller, "ma_id", ma_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{ma_id});
	}
	
	@Override
	public void resAuditMakeDelete(int ma_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.updateByCondition("Make", "ma_checkstatuscode='ENTERING',ma_checkstatus='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ma_id=" + ma_id);
		//记录操作
		baseDao.logger.resAudit(caller, "ma_id", ma_id);
	}
	
	@Override
	public void submitMakeDelete(int ma_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{ma_id});		//执行提交操作
		baseDao.submit("Make", "ma_id=" + ma_id, "ma_checkstatus", "ma_checkstatuscode");
		//记录操作
		baseDao.logger.submit(caller, "ma_id", ma_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{ma_id});
	}
	
	@Override
	public void resSubmitMakeDelete(int ma_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("Make", "ma_statuscode", "ma_id=" + ma_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{ma_id});		//执行反提交操作
		baseDao.updateByCondition("Make", "ma_checkstatuscode='ENTERING',ma_checkstatus='" + 
				BaseUtil.getLocalMessage("ENTERING") + "'", "ma_id=" + ma_id);
		//记录操作
		baseDao.logger.resSubmit(caller, "ma_id", ma_id);
		handlerService.afterResSubmit(caller, new Object[]{ma_id});
	}
}
