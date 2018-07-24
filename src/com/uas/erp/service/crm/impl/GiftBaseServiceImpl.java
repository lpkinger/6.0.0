package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.GiftBaseService;

@Service
public class GiftBaseServiceImpl implements GiftBaseService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveGiftBase(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Gift",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "gi_id", store.get("gi_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateGiftBase(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改Oaappliance
		String formSql = SqlUtil
				.getUpdateSqlByFormStore(store, "Gift", "gi_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "gi_id", store.get("gi_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteGiftBase(int gi_id, String caller) {
		handlerService.beforeDel(caller, gi_id);
		// 删除purchase
		baseDao.deleteById("Gift", "gi_id", gi_id);
		// 记录操作
		baseDao.logger.delete(caller, "gi_id", gi_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, gi_id);
	}
}
