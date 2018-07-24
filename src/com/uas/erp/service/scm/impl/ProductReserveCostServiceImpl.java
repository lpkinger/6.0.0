package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductReserveCostService;

@Service
public class ProductReserveCostServiceImpl implements ProductReserveCostService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateProductWHById(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.handler("Product!ReserveCost", "save", "before", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductWH", "pw_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update(caller, "pw_id", store.get("pw_id"));
		//执行修改后的其它逻辑
		handlerService.handler("Product!ReserveCost", "save", "after", new Object[]{store});
	}
}
