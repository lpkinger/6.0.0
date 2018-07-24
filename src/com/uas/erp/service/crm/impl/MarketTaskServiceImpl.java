package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.MarketTaskService;

@Service
public class MarketTaskServiceImpl implements MarketTaskService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveMarketTask(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "MarketTask",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "mt_id", store.get("mt_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteMarketTask(int mt_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, mt_id);
		// 删除purchase
		baseDao.deleteById("MarketTask", "mt_id", mt_id);
		// 记录操作
		baseDao.logger.delete(caller, "mt_id", mt_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, mt_id);
	}

	@Override
	public void updateMarketTask(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改MarketTask
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "MarketTask",
				"mt_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "mt_id", store.get("mt_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
}
