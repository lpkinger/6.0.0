package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductWarehouseService;

@Service
public class ProductWarehouseServiceImpl implements ProductWarehouseService{
	@Autowired
	private BaseDao baseDao;
	
	@Override
	public void saveProductWarehouse(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//保存
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Product", "pr_id");
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "pr_id", store.get("pr_id"));
	}
	
	@Override
	public void updateProductWarehouseById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("Product", "pr_statuscode", "pr_id=" + store.get("pr_id"));
		StateAssert.updateOnlyEntering(status);
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Product", "pr_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "pr_id", store.get("pr_id"));
	}
}
