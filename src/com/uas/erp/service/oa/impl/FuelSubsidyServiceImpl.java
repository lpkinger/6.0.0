package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.MessageLog;
import com.uas.erp.service.oa.FuelSubsidyService;

@Service
public class FuelSubsidyServiceImpl implements FuelSubsidyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFuelSubsidy(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller,new Object[] {store });
		// 保存FuelSubsidy
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "FuelSubsidy",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "fs_id", store.get("fs_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller,new Object[] {store });
	}

	@Override
	public void deleteFuelSubsidy(int fs_id, String  caller) {
		// 只能删除在录入的!
		Object status = baseDao.getFieldDataByCondition("FuelSubsidy",
				"fs_statuscode", "fs_id=" + fs_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller,  new Object[] {fs_id });
		// 删除FuelSubsidy
		baseDao.deleteById("FuelSubsidy", "fs_id", fs_id);
		// 记录操作
		baseDao.logger.delete(caller, "fs_id", fs_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller,  new Object[] {fs_id });
	}

	@Override
	public void updateFuelSubsidyById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("FuelSubsidy",
				"fs_statuscode", "fs_id=" + store.get("fs_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] {store });
		// 修改FuelSubsidy
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "FuelSubsidy",
				"fs_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "fs_id", store.get("fs_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store });
	}

	@Override
	public void submitFuelSubsidy(int fs_id, String  caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FuelSubsidy",
				"fs_statuscode", "fs_id=" + fs_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {fs_id });
		// 执行提交操作
		baseDao.submit("FuelSubsidy", "fs_id=" + fs_id, "fs_status", "fs_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "fs_id", fs_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {fs_id });
	}

	@Override
	public void resSubmitFuelSubsidy(int fs_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		handlerService.beforeResSubmit(caller, new Object[] {fs_id });
		Object status = baseDao.getFieldDataByCondition("FuelSubsidy",
				"fs_statuscode", "fs_id=" + fs_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("FuelSubsidy", "fs_id=" + fs_id, "fs_status", "fs_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "fs_id", fs_id);
		handlerService.afterResSubmit(caller, new Object[] {fs_id });
	}

	@Override
	public void auditFuelSubsidy(int fs_id, String  caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("FuelSubsidy",
				"fs_statuscode", "fs_id=" + fs_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {fs_id });
		// 执行审核操作
		baseDao.audit("FuelSubsidy", "fs_id=" + fs_id, "fs_status", "fs_statuscode", "fs_auditdate", "fs_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "fs_id", fs_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {fs_id });
	}

	@Override
	public void resAuditFuelSubsidy(int fs_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FuelSubsidy",
				"fs_statuscode", "fs_id=" + fs_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.updateByCondition(
				"FuelSubsidy",
				"fs_statuscode='ENTERING',fs_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',fs_auditer='',fs_auditdate=null", "fs_id=" + fs_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "fs_id", fs_id);

	}
	
	@Override
	public void confirmFuelSubsidy(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("FuelSubsidy",
				"fs_statuscode", "fs_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage(
					"common.confirm_onlyAudit"));
		}
		Employee employee = SystemSession.getUser();
		// 执行反审核操作
		baseDao.updateByCondition("FuelSubsidy", "fs_auditstatus='已处理'", "fs_id="
				+ id);
		baseDao.logMessage(new MessageLog(employee.getEm_name(), BaseUtil
				.getLocalMessage("msg.confirm"), BaseUtil
				.getLocalMessage("msg.confirmSuccess"),
				"FuelSubsidy|fs_id=" + id));
	}

}
