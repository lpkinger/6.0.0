package com.uas.opensys.service.Impl;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.opensys.service.CurBaseService;
import com.uas.opensys.service.PrototypeDemandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrototypeDemandServiceImpl implements PrototypeDemandService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;
	
	@Autowired
	private CurBaseService curBaseService;

	@Override
	public void savePrototypeDemand(String formStore, String gridStore,
			String   caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller,new Object[] {store});
		String formSql=SqlUtil.getInsertSqlByFormStore(store, "CurPrototypeDemand", new String[]{}, new Object[] {});
		baseDao.execute(formSql);	
		curBaseService.updateCurInfo("CurPrototypeDemand","cd_custcode","cd_customer",store.get("cd_enid"),"cd_id="+store.get("cd_id"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "cd_id", store.get("cd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store});
	}

	@Override
	public void updatePrototypeDemandById(String formStore, String gridStore,
			String   caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller, new Object[] {store});
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CurPrototypeDemand", "cd_id");		
		baseDao.execute(formSql);	
		curBaseService.updateCurInfo("CurPrototypeDemand","cd_custcode","cd_customer",store.get("cd_enid"),"cd_id="+store.get("cd_id"));
		// 记录操作
		baseDao.logger.update(caller, "cd_id", store.get("cd_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] {store});
	}

	@Override
	public void deletePrototypeDemand(int cd_id, String   caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {cd_id});
		baseDao.deleteById("CurPrototypeDemand", "cd_id", cd_id);
		// 记录操作
		baseDao.logger.delete(caller, "cd_id", cd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {cd_id});

	}

	@Override
	public void auditPrototypeDemand(int cd_id, String   caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("CurPrototypeDemand","cd_statuscode", "cd_id=" + cd_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] {cd_id });
		// 执行审核操作
		baseDao.audit("CurPrototypeDemand", "cd_id=" + cd_id, "cd_status", "cd_statuscode");
		// 记录操作
		baseDao.logger.audit(caller, "cd_id", cd_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] {cd_id });
	}

	@Override
	public void resAuditPrototypeDemand(int cd_id, String caller) {
		Object status = baseDao.getFieldDataByCondition("CurPrototypeDemand","cd_statuscode", "cd_id=" + cd_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("CurPrototypeDemand", "cd_id=" + cd_id, "cd_status", "cd_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "cd_id", cd_id);
		handlerService.afterResAudit(caller, new Object[] {cd_id });
	}

	@Override
	public void submitPrototypeDemand(int cd_id, String   caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("CurPrototypeDemand","cd_statuscode", "cd_id=" + cd_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑	
		handlerService.beforeSubmit(caller, new Object[] {cd_id});
		// 执行提交操作
		baseDao.submit("CurPrototypeDemand", "cd_id=" + cd_id, "cd_status", "cd_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "cd_id", cd_id);;
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] {cd_id});
	}

	@Override
	public void resSubmitPrototypeDemand(int cd_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		handlerService.beforeResSubmit(caller, new Object[] {cd_id});
		Object status = baseDao.getFieldDataByCondition("CurPrototypeDemand","cd_statuscode", "cd_id=" + cd_id);
		StateAssert.resSubmitOnlyCommited(status);
		// 执行反提交操作
		baseDao.resOperate("CurPrototypeDemand", "cd_id=" + cd_id, "cd_status", "cd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "cd_id", cd_id);
		handlerService.afterResSubmit(caller, new Object[] {cd_id});
	}
}
