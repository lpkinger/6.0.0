package com.uas.erp.service.fs.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.fs.FaItemsFormulaService;

@Service
public class FaItemsFormulaServiceImpl implements FaItemsFormulaService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFaItemsFormula(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FaItemsFormula"));
		int count = baseDao.getCount("select count(1) from FaItemsFormula");
		for (int i = 0; i < 5; i++) {
			int argCount = baseDao.getCountByCondition("user_tab_columns", "table_name='FAITEMS' and column_name in ('FI_NUM" + count
					+ "')");
			if (argCount > 0) {
				count = count + 1;
			} else {
				break;
			}
		}
		baseDao.execute("alter table FAITEMS add FI_NUM" + count + " number");
		baseDao.execute("update FaItemsFormula set fif_field='FI_NUM" + count + "' where fif_id=" + store.get("fif_id"));
		baseDao.logger.save(caller, "fif_id", store.get("fif_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateFaItemsFormula(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FaItemsFormula", "fif_id"));
		Object f = baseDao.getFieldDataByCondition("FaItemsFormula", "fif_field", "fif_id=" + store.get("fif_id"));
		if (!StringUtil.hasText(f)) {
			int count = baseDao.getCount("select count(1) from FaItemsFormula");
			for (int i = 0; i < 5; i++) {
				int argCount = baseDao.getCountByCondition("user_tab_columns", "table_name='FAITEMS' and column_name in ('FI_NUM" + count
						+ "')");
				if (argCount > 0) {
					count = count + 1;
				} else {
					break;
				}
			}
			baseDao.execute("alter table FAITEMS add FI_NUM" + count + " number");
			baseDao.execute("update FaItemsFormula set fif_field='FI_NUM" + count + "' where fif_id=" + store.get("fif_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "fif_id", store.get("fif_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteFaItemsFormula(int fif_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { fif_id });
		Object f = baseDao.getFieldDataByCondition("FaItemsFormula", "fif_field", "fif_id=" + fif_id);
		if (StringUtil.hasText(f)) {
			baseDao.execute("update FaItemsFormula set fif_field=null where fif_id=" + fif_id);
			baseDao.execute("alter table FAITEMS drop column " + f);
		}
		// 删除主表内容
		baseDao.deleteById("FaItemsFormula", "fif_id", fif_id);
		baseDao.logger.delete(caller, "fif_id", fif_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { fif_id });
	}
}
