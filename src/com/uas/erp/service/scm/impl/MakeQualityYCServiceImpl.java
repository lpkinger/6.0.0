package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.MakeQualityYCService;
@Service
public class MakeQualityYCServiceImpl implements MakeQualityYCService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveMakeQualityYC(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("MakeQualityYC", "mq_code='" + store.get("mq_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		// 保存MakeQualityYC
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MakeQualityYC", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "mq_id", store.get("mq_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateMakeQualityYCById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("MakeQualityYC", "mq_statuscode", "mq_id=" + store.get("mq_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MakeQualityYC", "mq_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "mq_id", store.get("mq_id"));
		// 执行修改后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void deleteMakeQualityYC(int mq_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("MakeQualityYC", "mq_statuscode", "mq_id=" + mq_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, mq_id);
		baseDao.deleteById("MakeQualityYC", "mq_id", mq_id);
				// 执行删除后的其它逻辑
		baseDao.logger.delete(caller, "mq_id", mq_id);
		handlerService.afterDel(caller, mq_id);
	}

	@Override
	public void auditMakeQualityYC(int mq_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("MakeQualityYC", "mq_statuscode", "mq_id=" + mq_id);
		StateAssert.auditOnlyCommited(status);	
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, mq_id);
		// 执行审核操作
		baseDao.audit("MakeQualityYC", "mq_id=" + mq_id, "mq_status", "mq_statuscode", "mq_auditdate", "mq_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "mq_id", mq_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, mq_id);
	}

	@Override
	public void resAuditMakeQualityYC(int mq_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
	    Object status = baseDao.getFieldDataByCondition("MakeQualityYC", "mq_statuscode", "mq_id=" + mq_id);
	    StateAssert.resAuditOnlyAudit(status);		
		// 执行反审核操作
	    baseDao.resOperate("MakeQualityYC", "mq_id=" + mq_id, "mq_status", "mq_statuscode");
		// 记录操作
	    baseDao.logger.resAudit(caller, "mq_id", mq_id);
	}

	@Override
	public void submitMakeQualityYC(int mq_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeQualityYC", "mq_statuscode", "mq_id=" + mq_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, mq_id);
		// 执行提交操作
		baseDao.submit("MakeQualityYC", "mq_id=" + mq_id, "mq_status", "mq_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "mq_id", mq_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, mq_id);
	}

	@Override
	public void resSubmitMakeQualityYC(int mq_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("MakeQualityYC", "mq_statuscode", "mq_id=" + mq_id);
		StateAssert.resSubmitOnlyCommited(status);	
		handlerService.beforeResSubmit(caller, mq_id);
		// 执行反提交操作
		 baseDao.resOperate("MakeQualityYC", "mq_id=" + mq_id, "mq_status", "mq_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "mq_id", mq_id);
		handlerService.afterResSubmit(caller, mq_id);
	}
}
