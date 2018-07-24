package com.uas.erp.service.oa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.SentCarApplicationService;

@Service
public class SentCarApplicationServiceImpl implements SentCarApplicationService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Override
	public void saveSentCarApplication(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SentCarApplication", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "sca_id", store.get("sca_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void updateSentCarApplication(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[]{store});
		//修改SentCarApplication
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SentCarApplication", "sca_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "sca_id", store.get("sca_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[]{store});
		
	}

	@Override
	public void deleteSentCarApplication(int sca_id, String  caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[]{sca_id});
		//删除purchase
		baseDao.deleteById("SentCarApplication", "sca_id", sca_id);
		//记录操作
		baseDao.logger.delete(caller, "sca_id", sca_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[]{sca_id});
	}

	@Override
	public void auditSentCarApplication(int sca_id, String  caller) {
		//执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[]{sca_id});
		//执行审核操作
		baseDao.audit("SentCarApplication", "sca_id=" + sca_id, "sca_status", "sca_statuscode", "sca_auditdate", "sca_auditman");
		//记录操作
		baseDao.logger.audit(caller, "sca_id", sca_id);
		//执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[]{sca_id});
	}

	@Override
	public void resAuditSentCarApplication(int sca_id, String caller) {
		//执行反审核操作
		baseDao.resOperate("SentCarApplication", "sca_id=" + sca_id, "sca_status", "sca_statuscode");
		//记录操作
		baseDao.logger.resAudit(caller, "sca_id", sca_id);
	}

	@Override
	public void submitSentCarApplication(int sca_id, String  caller) {
		//执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[]{sca_id});
		//执行提交操作
		baseDao.submit("SentCarApplication", "sca_id=" + sca_id, "sca_status", "sca_statuscode");
		//记录操作
		baseDao.logger.submit(caller, "sca_id", sca_id);
		//执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[]{sca_id});
	}

	@Override
	public void resSubmitSentCarApplication(int sca_id, String caller) {
		//执行反提交操作
		baseDao.resOperate("SentCarApplication", "sca_id=" + sca_id, "sca_status", "sca_statuscode");
		//记录操作
		baseDao.logger.resSubmit(caller, "sca_id", sca_id);
	}
}
