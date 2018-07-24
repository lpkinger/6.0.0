package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;


import com.uas.erp.service.crm.CustomerCommuService;

@Service
public class CustomerCommuServiceImpl implements CustomerCommuService{
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveCustomerCommu(String formStore,  String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[]{store});
		//执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "CustomerCommu", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "cc_id", store.get("cc_id"));
		//执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[]{store});
	}

	@Override
	public void deleteCustomerCommu(int cc_id, 	String caller) {
		//执行删除前的其它逻辑
		handlerService.beforeDel(caller, cc_id);
		//删除purchase
		baseDao.deleteById("CustomerCommu", "cc_id", cc_id);
		//记录操作
		baseDao.logger.delete(caller, "cc_id", cc_id);
		//执行删除后的其它逻辑
		handlerService.afterDel(caller, cc_id);
	}

	@Override
	public void updateCustomerCommu(String formStore, 	String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[]{store});
		//修改CustomerCommu
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "CustomerCommu", "cc_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "cc_id", store.get("cc_id"));
		//执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[]{store});
	}
}
