package com.uas.erp.service.crm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.crm.RepairService;

@Service
public class RepairServiceImpl implements RepairService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveRepair(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store });
		// 执行保存操作
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Repair",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.save(caller, "re_id", store.get("re_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void deleteRepair(int re_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, re_id);
		// 删除purchase
		baseDao.deleteById("Repair", "re_id", re_id);
		// 记录操作
		baseDao.logger.delete(caller, "re_id", re_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, re_id);
	}

	@Override
	public void updateRepair(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改Repair
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Repair",
				"re_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "re_id", store.get("re_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}
}
