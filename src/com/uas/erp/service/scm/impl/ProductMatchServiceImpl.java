package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductMatchService;

@Service
public class ProductMatchServiceImpl implements ProductMatchService {
	@Autowired
	private BaseDao baseDao;
	@Override	
	public void deleteProductMatch(int pm_id, String caller) {
		//删除
		baseDao.deleteById("ProductMatch", "pm_id", pm_id);
		//记录操作
		baseDao.logger.delete(caller, "pm_id", pm_id);
	}
	@Override
	public void saveProductMatch(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//保存product
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductMatch", new String[]{}, new Object[]{});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "pm_id", store.get("pm_id"));
	}
	@Override
	public void updateProductMatchById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductMatch", "pm_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.save(caller, "pm_id", store.get("pm_id"));
	}
}
