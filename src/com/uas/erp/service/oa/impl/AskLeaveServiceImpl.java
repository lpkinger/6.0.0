package com.uas.erp.service.oa.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.oa.AskLeaveService;

@Service
public class AskLeaveServiceImpl implements AskLeaveService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAskLeave(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] {store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AskLeave",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "al_id", store.get("al_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] {store });

	}

	@Override
	public void updateAskLeave(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller,new Object[] { store });
		// 修改AskLeave
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AskLeave",
				"al_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "al_id", store.get("al_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller,new Object[] { store });

	}

	@Override
	public void deleteAskLeave(int al_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, new Object[] {al_id});
		// 删除purchase
		baseDao.deleteById("AskLeave", "al_id", al_id);
		// 记录操作
		baseDao.logger.delete(caller, "al_id", al_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, new Object[] {al_id});
	}

	@Override
	public void auditAskLeave(int al_id, String caller) {
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, new Object[] { al_id });
		// 执行审核操作
		baseDao.audit("AskLeave", "al_id="+ al_id, "al_status", "al_statuscode", "al_auditdate", "al_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "al_id", al_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, new Object[] { al_id });
	}

	@Override
	public void resAuditAskLeave(int al_id, String caller) {
		// 执行反审核操作
		baseDao.resOperate("AskLeave", "al_id="+ al_id, "al_status", "al_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "al_id", al_id);

	}

	@Override
	public void submitAskLeave(int al_id, String caller) {
		// 执行提交前的其它逻辑
		handlerService.beforeSubmit(caller, new Object[] { al_id });
		// 执行提交操作
		baseDao.submit("AskLeave", "al_id=" + al_id, "al_status",
				"al_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "al_id", al_id);
		// 执行提交后的其它逻辑
		handlerService.afterSubmit(caller, new Object[] { al_id });
	}

	@Override
	public void resSubmitAskLeave(int al_id, String caller) {
		// 执行反提交操作
		baseDao.resOperate("AskLeave", "al_id=" + al_id, "al_status",
				"al_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "al_id", al_id);

	}

}
