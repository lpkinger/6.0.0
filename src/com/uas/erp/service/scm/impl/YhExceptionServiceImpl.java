package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.YhExceptionService;


@Service
public class YhExceptionServiceImpl implements YhExceptionService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveYhException(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("YhException", "ye_code='" + store.get("ye_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("YhException", "save", "before", new Object[] { store });
		// 保存YhException
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "YhException", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save("YhException", "ye_id", store.get("ye_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("YhException", "save", "after", new Object[] { store });
	}

	@Override
	public void updateYhExceptionById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("YhException", "ye_statuscode", "ye_id=" + store.get("ye_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler("YhException", "save", "before", new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "YhException", "ye_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update("YhException", "ye_id", store.get("ye_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("YhException", "save", "after", new Object[] { store });
	}

	@Override
	public void deleteYhException(int ye_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("YhException", "ye_statuscode", "ye_id=" + ye_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler("YhException", "delete", "before", new Object[] { ye_id });
		baseDao.deleteById("YhException", "ye_id", ye_id);
		baseDao.logger.delete("YhException", "ye_id", ye_id);
		// 执行删除后的其它逻辑
		handlerService.handler("YhException", "delete", "after", new Object[] { ye_id });
	}

	@Override
	public void auditYhException(int ye_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("YhException", "ye_statuscode", "ye_id=" + ye_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler("YhException", "audit", "before", new Object[] { ye_id });
		// 执行审核操作
		baseDao.audit("YhException", "ye_id=" + ye_id, "ye_status", "ye_statuscode", "ye_auditdate", "ye_auditman");
		// 记录操作
		baseDao.logger.audit("YhException", "ye_id", ye_id);
		// 执行审核后的其它逻辑
		handlerService.handler("YhException", "audit", "after", new Object[] { ye_id });
	}

	@Override
	public void resAuditYhException(int ye_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("YhException", "ye_statuscode", "ye_id=" + ye_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("YhException", "ye_id=" + ye_id, "ye_status", "ye_statuscode");
		// 记录操作
		baseDao.logger.resAudit("YhException", "ye_id", ye_id);
	}

	@Override
	public void submitYhException(int ye_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("YhException", "ye_statuscode", "ye_id=" + ye_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler("YhException", "commit", "before", new Object[] { ye_id });
		// 执行提交操作
		baseDao.submit("YhException", "ye_id=" + ye_id, "ye_status", "ye_statuscode");
		// 记录操作
		baseDao.logger.submit("YhException", "ye_id", ye_id);
		// 执行提交后的其它逻辑
		handlerService.handler("YhException", "commit", "after", new Object[] { ye_id });
	}

	@Override
	public void resSubmitYhException(int ye_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("YhException", "ye_statuscode", "ye_id=" + ye_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("YhException", "resCommit", "before", new Object[] { ye_id });
		// 执行反提交操作
		baseDao.resOperate("YhException", "ye_id=" + ye_id, "ye_status", "ye_statuscode");
		// 记录操作
		baseDao.logger.resSubmit("YhException", "ye_id", ye_id);
		handlerService.handler("YhException", "resCommit", "after", new Object[] { ye_id });
	}
}
