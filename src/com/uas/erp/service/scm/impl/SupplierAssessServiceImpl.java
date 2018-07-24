package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.SupplierAssessService;

@Service
public class SupplierAssessServiceImpl implements SupplierAssessService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSupplierAssess(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String code = store.get("sa_code") + "";
		// 编号要求S开头
		if (!code.startsWith("S")) {
			store.put("sa_code", "S" + code);
		}
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SupplierAssess", "sa_code='" + store.get("sa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("SupplierAssess", "save", "before", new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SupplierAssess", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "sa_id", store.get("sa_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("SupplierAssess", "save", "after", new Object[] { store });
	}

	@Override
	public void deleteSupplierAssess(int sa_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler("SupplierAssess", "delete", "before", new Object[] { sa_id });
		// 删除purchase
		baseDao.deleteById("SupplierAssess", "sa_id", sa_id);
		// 记录操作
		baseDao.logger.delete(caller, "sa_id", sa_id);
		// 执行删除后的其它逻辑
		handlerService.handler("SupplierAssess", "delete", "after", new Object[] { sa_id });
	}

	@Override
	public void updateSupplierAssess(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.handler("SupplierAssess", "save", "before", new Object[] { store });
		String code = store.get("sa_code") + "";
		// 编号要求S开头
		if (!code.startsWith("S")) {
			store.put("sa_code", "S" + code);
		}
		// 修改SupplierAssess
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SupplierAssess", "sa_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "sa_id", store.get("sa_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("SupplierAssess", "save", "after", new Object[] { store });
	}

	@Override
	public void submitSupplierAssess(int sa_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SupplierAssess", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.submitOnlyEntering(status);
		// 执行提交前的其它逻辑
		handlerService.handler("SupplierAssess", "commit", "before", new Object[] { sa_id });
		// 执行提交操作
		baseDao.submit("SupplierAssess", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sa_id", sa_id);
		// 执行提交后的其它逻辑
		handlerService.handler("SupplierAssess", "commit", "after", new Object[] { sa_id });
	}

	@Override
	public void resSubmitSupplierAssess(int sa_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SupplierAssess", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler("SupplierAssess", "resCommit", "before", new Object[] { sa_id });
		// 执行反提交操作
		baseDao.resOperate("SupplierAssess", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sa_id", sa_id);
		handlerService.handler("SupplierAssess", "resCommit", "after", new Object[] { sa_id });
	}

	@Override
	public void auditSupplierAssess(int sa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SupplierAssess", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.handler("SupplierAssess", "audit", "before", new Object[] { sa_id });
		// 执行审核操作
		baseDao.audit("SupplierAssess", "sa_id=" + sa_id, "sa_status", "sa_statuscode", "sa_auditdate", "sa_auditor");
		// 记录操作
		baseDao.logger.audit(caller, "sa_id", sa_id);
		// 执行审核后的其它逻辑
		handlerService.handler("SupplierAssess", "audit", "after", new Object[] { sa_id });
	}

	@Override
	public void resAuditSupplierAssess(int sa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SupplierAssess", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resOperate("SupplierAssess", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
		// 记录操作
		baseDao.logger.resAudit(caller, "sa_id", sa_id);
	}

	@Override
	public void turnPreVendor(int sa_id, String caller) {
		// TODO Auto-generated method stub
	}
}
