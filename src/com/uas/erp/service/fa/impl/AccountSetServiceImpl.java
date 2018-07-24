package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.AccountSetService;

@Service("accountSetService")
public class AccountSetServiceImpl implements AccountSetService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveAccountSet(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "AccountSet",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "as_id", store.get("as_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteAccountSet(int as_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, as_id);
		// 删除
		baseDao.deleteById("AccountSet", "as_id", as_id);
		// 记录操作
		baseDao.logger.delete(caller, "as_id", as_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, as_id);
	}

	@Override
	public void updateAccountSetById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "AccountSet",
				"as_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "as_id", store.get("as_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
}
