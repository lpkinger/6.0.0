package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.CashFlowDefService;

@Service("cashFlowDefService")
public class CashFlowDefServiceImpl implements CashFlowDefService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCashFlowDef(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("CashFlowDef", "cfd_code='"
				+ store.get("cfd_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CashFlowDef",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "cfd_id", store.get("cfd_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteCashFlowDef(int cfd_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("CashFlowDef",
				"cfd_statuscode", "cfd_id=" + cfd_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, cfd_id);
		// 删除payments
		baseDao.deleteById("CashFlowDef", "cfd_id", cfd_id);
		// 记录操作
		baseDao.logger.delete(caller, "cfd_id", cfd_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, cfd_id);
	}

	@Override
	public void updateCashFlowDefById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("CashFlowDef",
				"cfd_statuscode", "cfd_id=" + store.get("cfd_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CashFlowDef",
				"cfd_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "cfd_id", store.get("cfd_id"));
		// 执行修改后的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
	}

}
