package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.PowerApplyService;

@Service
public class PowerApplyServiceImpl implements PowerApplyService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void savePowerApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "PowerApply",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "pa_id", store.get("pa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store });

	}

	@Override
	public void updatePowerApply(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "PowerApply",
				"pa_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pa_id", store.get("pa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[] { store });

	}

	@Override
	public void deletePowerApply(int id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {id});
		// 删除purchase
		baseDao.deleteById("PowerApply", "pa_id",  id);
		// 记录操作
		baseDao.logger.delete(caller, "pa_id",  id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] { id});
	}

	@Override
	public void auditPowerApply(int  id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {  id });
		// 执行审核操作
		baseDao.audit("PowerApply", "pa_id="+ id, "pa_status", "pa_statuscode", "pa_auditdate", "pa_auditer");
		// 记录操作
		baseDao.logger.audit(caller, "pa_id", id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { id });
	}

	@Override
	public void resAuditPowerApply(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PowerApply","pa_statuscode", "pa_id=" + id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("PowerApply", "pa_id="+  id, "pa_status", "pa_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "pa_id", id);

	}

	@Override
	public void submitPowerApply(int  id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] {  id });
		// 执行提交操作
		baseDao.submit("PowerApply", "pa_id=" +  id, "pa_status","pa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "pa_id", id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { id });
	}

	@Override
	public void resSubmitPowerApply(int id, String caller) {
		Object status = baseDao.getFieldDataByCondition("PowerApply", "pa_statuscode", "pa_id=" + id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeSubmit(caller, id);
		// 执行反提交操作
		baseDao.resOperate("PowerApply", "pa_id=" +  id, "pa_status","pa_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "pa_id",id);
		handlerService.afterResSubmit(caller, id);
	}

}
