package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.NewProductConService;
@Service
public class NewProductConServiceImpl implements NewProductConService{
    @Autowired
    private BaseDao baseDao;
	 @Autowired
	 private HandlerService handlerService;
		@Override
		public void saveNewProductCon(String formStore, String caller) {
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
			handlerService.beforeSave(caller, new Object[]{store});
			String formSql = SqlUtil.getInsertSqlByFormStore(store, "NewProductCon", new String[]{}, new Object[]{});
			baseDao.execute(formSql);
			baseDao.logger.save(caller, "npc_id", store.get("npc_id"));
			handlerService.afterSave(caller, new Object[]{store});
		}

		@Override
		public void updateNewProductCon(String formStore, String caller) {
			Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
			handlerService.beforeSave(caller, new Object[]{store});
			String formSql = SqlUtil.getUpdateSqlByFormStore(store, "NewProductCon","npc_id" );
			baseDao.execute(formSql);
			baseDao.logger.update(caller, "npc_id", store.get("npc_id"));
			handlerService.afterSave(caller, new Object[]{store});
		}

		@Override
		public void deleteNewProductCon(int id, String caller) {
			handlerService.beforeDel(caller, id);
			//删除FundSubject
			baseDao.deleteById("NewProductCon", "npc_id", id);
			//记录操作
			baseDao.logger.delete(caller, "npc_id", id);
			//执行删除后的其它逻辑
			handlerService.afterDel(caller, id);
		}
		
		@Override
		public void auditNewProductCon(int id, String caller) {
			Object status = baseDao.getFieldDataByCondition("NewProductCon", "npc_statuscode", "npc_id=" + id);
			StateAssert.auditOnlyCommited(status);
			handlerService.beforeAudit(caller, id);
			//执行审核操作
			baseDao.audit("NewProductCon", "npc_id=" + id, "npc_status", "npc_statuscode");
			//记录操作
			baseDao.logger.audit(caller, "npc_id", id);
			//执行审核后的其它逻辑
			handlerService.afterAudit(caller, id);
		}

		@Override
		public void submitNewProductCon(int id, String caller) {
			Object status = baseDao.getFieldDataByCondition("NewProductCon", "npc_statuscode", "npc_id=" + id);
			StateAssert.submitOnlyEntering(status);
			handlerService.beforeSubmit(caller, id);
			//执行反审核操作
			baseDao.submit("NewProductCon", "npc_id=" + id, "npc_status", "npc_statuscode");
			//记录操作
			baseDao.logger.submit(caller, "npc_id", id);
			handlerService.afterSubmit(caller, id);
		}

		@Override
		public void resSubmitNewProductCon(int id, String caller) {
			Object status = baseDao.getFieldDataByCondition("NewProductCon", "npc_statuscode", "npc_id=" + id);
			StateAssert.resSubmitOnlyCommited(status);
			handlerService.beforeResSubmit(caller, id);
			//执行反提交操作
			baseDao.resOperate("NewProductCon", "npc_id=" + id, "npc_status", "npc_statuscode");
			//记录操作
			baseDao.logger.resSubmit(caller, "npc_id", id);
			handlerService.afterResSubmit(caller, id);
		}
		
		@Override
		public void resAuditNewProductCon(int id, String caller) {
			Object status = baseDao.getFieldDataByCondition("NewProductCon", "npc_statuscode", "npc_id=" + id);
			StateAssert.resAuditOnlyAudit(status);
			//执行反审核操作
			baseDao.resOperate("NewProductCon", "npc_id=" + id, "npc_status", "npc_statuscode");
			//记录操作
			baseDao.logger.resAudit(caller, "npc_id", id);
		}
}