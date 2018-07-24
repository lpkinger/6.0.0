package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.BatchMoneyService;

@Service
public class BatchMoneyServiceImpl implements BatchMoneyService{
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private HandlerService handlerService;
	
	@Override
	public void updateBatchById(String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//执行修改前的其它逻辑
		handlerService.handler("Batch!Money", "save", "before", new Object[]{store});
		//修改
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Batch", "ba_id");
		baseDao.execute(formSql);
		//记录操作
		baseDao.logger.update("Batch!Money", "ba_id", store.get("ba_id"));
		//执行修改后的其它逻辑
		handlerService.handler("Batch!Money", "save", "after", new Object[]{store});
	}
}
