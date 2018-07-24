package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.DormitoryAppService;

@Service("dormitoryAppService")
public class DormitoryAppServiceImpl implements DormitoryAppService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveDormitoryApp(String formStore, String  caller) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("DormitoryApp", "da_code='" + store.get("da_code") + "'");
		if(!bool){
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//保存DormitoryApp
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "DormitoryApp", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
			//记录操作
		baseDao.logger.save(caller, "da_id", store.get("da_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
		
	}

	@Override
	public void deleteDormitoryApp(int da_id, String  caller) {
		//只能删除在录入的采购单!
				Object status = baseDao.getFieldDataByCondition("DormitoryApp", "da_statuscode", "da_id=" + da_id);
			    StateAssert.delOnlyEntering(status);
				//执行删除前的其它逻辑
				handlerService.beforeDel(caller, new Object[]{da_id});
				//删除DormitoryApp
				baseDao.deleteById("DormitoryApp", "da_id", da_id);
				//记录操作
				baseDao.logger.delete(caller,"da_id", da_id);
				//执行删除后的其它逻辑
				handlerService.afterDel(caller, new Object[]{da_id});
		
	}

	@Override
	public void updateDormitoryAppById(String formStore, String  caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的采购单资料!
		Object status = baseDao.getFieldDataByCondition("DormitoryApp", "da_statuscode", "da_id=" + store.get("da_id"));
		StateAssert.updateOnlyEntering(status);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改DormitoryApp
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "DormitoryApp", "da_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "da_id", store.get("da_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
		
	}

	@Override
	public void submitDormitoryApp(int da_id, String  caller) {
		//只能对状态为[在录入]的订单进行提交操作!
				Object status = baseDao.getFieldDataByCondition("DormitoryApp", "da_statuscode", "da_id=" + da_id);
				StateAssert.submitOnlyEntering(status);
				//执行提交前的其它逻辑
				handlerService.beforeSubmit(caller, new Object[]{da_id});
				//执行提交操作
				baseDao.submit("DormitoryApp", "da_id=" + da_id, "da_status", "da_statuscode");
				//记录操作
				baseDao.logger.submit(caller, "da_id", da_id);
				//执行提交后的其它逻辑
				handlerService.afterSubmit(caller, new Object[]{da_id});
		
	}

	@Override
	public void resSubmitDormitoryApp(int da_id, String  caller) {
		//只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("DormitoryApp", "da_statuscode", "da_id=" + da_id);
		StateAssert.resSubmitOnlyCommited(status);
		//执行反提交操作
		baseDao.resOperate("DormitoryApp", "da_id=" + da_id, "da_status", "da_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "da_id", da_id);
		
	}

	@Override
	public void auditDormitoryApp(int da_id, String  caller) {
		//只能对状态为[已提交]的订单进行审核操作!
				Object status = baseDao.getFieldDataByCondition("DormitoryApp", "da_statuscode", "da_id=" + da_id);
				StateAssert.auditOnlyCommited(status);
				//执行审核前的其它逻辑
				handlerService.beforeAudit(caller, new Object[]{da_id});
				//执行审核操作
				baseDao.audit("DormitoryApp", "da_id=" + da_id, "da_status", "da_statuscode", "da_auditdate", "da_auditman");;
				//记录操作
				baseDao.logger.audit(caller, "da_id", da_id);
				//执行审核后的其它逻辑
				handlerService.afterAudit(caller, new Object[]{da_id});
		
	}

	@Override
	public void resAuditDormitoryApp(int da_id, String  caller) {
		//只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("DormitoryApp", "da_statuscode", "da_id=" + da_id);
		StateAssert.resAuditOnlyAudit(status);
			//执行反审核操作
			baseDao.resOperate("DormitoryApp", "da_id=" + da_id, "da_status", "da_statuscode");
			//记录操作
			baseDao.logger.resAudit(caller, "da_id", da_id);
		
	}
	

}
