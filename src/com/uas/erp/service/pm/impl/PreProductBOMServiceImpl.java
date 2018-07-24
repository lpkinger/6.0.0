package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.service.pm.PreProductBOMService;

@Service("preProductBOMService")
public class PreProductBOMServiceImpl implements PreProductBOMService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void savePreProductBOM(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("PreProduct", "pre_code='" + store.get("pre_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PreProduct", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		try{
			//记录操作
			baseDao.logger.save(caller, "pre_id", store.get("pre_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}	
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
	
	@Override
	public void deletePreProductBOM(int pre_id, String caller) {
		//只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.delOnlyEntering(status);
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{pre_id});
		//删除
		baseDao.deleteById("PreProduct", "pre_id", pre_id);		
		//记录操作
		baseDao.logger.delete(caller, "pre_id",pre_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{pre_id});
	}
	
	@Override
	public void updatePreProductBOMById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id" + store.get("pre_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PreProduct", "pre_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "pre_id", store.get("pre_id"));
		//执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}
		
	@Override
	public void auditPreProductBOM(int pre_id, String caller) {
		//只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.auditOnlyCommited(status);
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller,new Object[]{pre_id});
		//执行审核操作
		baseDao.audit("PreProduct", "pre_id=" + pre_id, "pre_status", "pre_statuscode", "pre_auditdate", "pre_auditman");
		//记录操作
		baseDao.logger.audit(caller, "pre_id", pre_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller,new Object[]{pre_id});
	}
	
	@Override
	public void resAuditPreProductBOM(int pre_id, String caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.resAuditOnlyAudit(status);
		//执行反审核操作
		baseDao.resOperate("PreProduct", "pre_id=" + pre_id, "pre_status", "pre_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "pre_id", pre_id);
	}
	
	@Override
	public void submitPreProductBOM(int pre_id, String caller) {
		//只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.submitOnlyEntering(status);
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller,  new Object[]{pre_id});
		//执行提交操作
		baseDao.submit("PreProduct", "pre_id=" + pre_id, "pre_status", "pre_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "pre_id", pre_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller,  new Object[]{pre_id});
	}
	
	@Override
	public void resSubmitPreProductBOM(int pre_id, String caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("PreProduct", "pre_statuscode", "pre_id=" + pre_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, new Object[]{pre_id});
		//执行反提交操作
		baseDao.resOperate("PreProduct", "pre_id=" + pre_id, "pre_status", "pre_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "pre_id", pre_id);
		handlerService.afterResSubmit(caller, new Object[]{pre_id});
	}	
}
