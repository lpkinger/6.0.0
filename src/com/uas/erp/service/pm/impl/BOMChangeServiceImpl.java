package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.service.pm.BOMChangeService;




@Service("BOMChangeService")
public class BOMChangeServiceImpl implements BOMChangeService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveBOM(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("BOM", "bo_code='" + store.get("bo_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "BOM", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作			
			baseDao.logger.save(caller, "bo_id",store.get("bo_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//执行保存后的其它逻辑
		handlerService.afterSave("BOM",new Object[]{store});
	}
	@Override
	public void deleteBOM(int bo_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{bo_id});
		//删除BOM
		baseDao.deleteById("BOM", "bo_id", bo_id);		
		//记录操作
		baseDao.logger.delete(caller, "bo_id",bo_id);
		//执行删除后的其它逻辑
		handlerService.afterDel("BOM!Change",new Object[]{bo_id});
	}
	
	@Override
	public void updateBOMById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + store.get("bo_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave("BOM!Change", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "BOM", "bo_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "bo_id",store.get("bo_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave("BOM!Change",new Object[]{store});
	}

	@Override
	public void auditBOM(int bo_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit("BOM!Change",new Object[]{bo_id});
		//执行审核操作
		baseDao.audit("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode", "bo_auditdate", "bo_auditman");
		//记录操作
		baseDao.logger.audit(caller, "bo_id", bo_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit("BOM!Change", new Object[]{bo_id});
	}
	@Override
	public void resAuditBOM(int bo_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "bo_id", bo_id);
	}
	@Override
	public void submitBOM(int bo_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit("BOM!Change",new Object[]{bo_id});
		//执行提交操作
		baseDao.submit("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "bo_id", bo_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit("BOM!Change",new Object[]{bo_id});
	}
	@Override
	public void resSubmitBOM(int bo_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("BOM", "bo_statuscode", "bo_id=" + bo_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit("BOM!Change", new Object[]{bo_id});
		//执行反提交操作
		baseDao.resOperate("BOM", "bo_id=" + bo_id, "bo_status", "bo_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "bo_id", bo_id);
		handlerService.afterResSubmit("BOM!Change",new Object[]{bo_id});
	}	
}
