package com.uas.erp.service.pm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.pm.POCPleaseService;

@Service
public class POCPleaseServiceImpl implements POCPleaseService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void savePOCPlease(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "POCPlease", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "poc_id", store.get("poc_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updatePOCPlease(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,  new Object[]{formStore});
		//修改AskLeave
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "POCPlease", "poc_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "poc_id", store.get("poc_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,  new Object[]{formStore});
	}

	@Override
	public void deletePOCPlease(int poc_id, String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{poc_id});
		//删除purchase
		baseDao.deleteById("POCPlease", "poc_id", poc_id);
		//记录操作
		baseDao.logger.delete(caller, "poc_id", poc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{poc_id});
	}

	@Override
	public void auditPOCPlease(int poc_id, String caller) {
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{poc_id});
		//执行审核操作
		baseDao.audit("POCPlease", "poc_id=" + poc_id, "poc_status", "poc_statuscode", "poc_auditdate", "poc_auditemname");
		//记录操作
		baseDao.logger.audit(caller, "poc_id", poc_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{poc_id});
	}

	@Override
	public void resAuditPOCPlease(int poc_id, String caller) {
		//执行反审核操作
		baseDao.resOperate("POCPlease", "poc_id=" + poc_id, "poc_status", "poc_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "poc_id", poc_id);

	}

	@Override
	public void submitPOCPlease(int poc_id, String caller) {
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{poc_id});
		//执行提交操作
		baseDao.submit("POCPlease", "poc_id=" + poc_id, "poc_status", "poc_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "poc_id", poc_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{poc_id});

	}

	@Override
	public void resSubmitPOCPlease(int poc_id, String caller) {
		//执行反提交操作
		handlerService.beforeResSubmit(caller, new Object[]{poc_id});
		baseDao.resOperate("POCPlease", "poc_id=" + poc_id, "poc_status", "poc_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "poc_id", poc_id);
		handlerService.afterResSubmit(caller, new Object[]{poc_id});
	}

}
