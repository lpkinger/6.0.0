package com.uas.erp.service.fs.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.FsInvoiceService;

@Service
public class FsInvoiceServiceImpl implements FsInvoiceService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFsInvoice(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FsInvoice"));
		baseDao.logger.save(caller, "in_id", store.get("in_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateFsInvoice(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FsInvoice", "in_id"));
		// 记录操作
		baseDao.logger.update(caller, "in_id", store.get("in_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteFsInvoice(int in_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { in_id });
		// 删除主表内容
		baseDao.deleteById("FsInvoice", "in_id", in_id);
		baseDao.logger.delete(caller, "in_id", in_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { in_id });
	}

	@Override
	public void submitFsInvoice(int in_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("FsInvoice", "in_statuscode", "in_id=" + in_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { in_id });
		// 执行提交操作
		baseDao.submit("FsInvoice", "in_id=" + in_id, "in_status", "in_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "in_id", in_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { in_id });
	}

	@Override
	public void resSubmitFsInvoice(int in_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FsInvoice", "in_statuscode", "in_id=" + in_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { in_id });
		// 执行反提交操作
		baseDao.resOperate("FsInvoice", "in_id=" + in_id, "in_status", "in_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "in_id", in_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { in_id });
	}

	@Override
	public void auditFsInvoice(int in_id, String caller) {
		// 只能对已提交进行审核操作
		Object status = baseDao.getFieldDataByCondition("FsInvoice", "in_statuscode", "in_id=" + in_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { in_id });
		baseDao.audit("FsInvoice", "in_id=" + in_id, "in_status", "in_statuscode", "in_auditdate", "in_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "in_id", in_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { in_id });
	}

	@Override
	public void resAuditFsInvoice(int in_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("FsInvoice", "in_statuscode", "in_id=" + in_id);
		StateAssert.resAuditOnlyAudit(status);
		baseDao.resAuditCheck("FsInvoice", in_id);
		handlerService.beforeResAudit(caller, new Object[] { in_id });
		// 执行反审核操作
		baseDao.resAudit("FsInvoice", "in_id=" + in_id, "in_status", "in_statuscode", "in_auditman", "in_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "in_id", in_id);
		handlerService.afterResAudit(caller, new Object[] { in_id });
	}

}
