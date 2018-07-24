package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.BudgetService;

@Service("budgetService")
public class BudgetServiceImpl implements BudgetService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveBudget(String caller, String formStore) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store });
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FA_Budget"));
		try {
			// 记录操作
			baseDao.logger.save(caller, "bg_id", store.get("bg_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateBudgetById(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeUpdate(caller, new Object[] { store });
		Object status = baseDao.getFieldDataByCondition("FA_Budget",
				"bg_statuscode", "bg_id=" + store.get("bg_id"));
		StateAssert.updateOnlyEntering(status);
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FA_Budget",
				"bg_id"));
		// 记录操作
		baseDao.logger.update(caller, "bg_id", store.get("bg_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteBudget(String caller, int bg_id) {
		Object status = baseDao.getFieldDataByCondition("FA_Budget",
				"bg_statuscode", "bg_id=" + bg_id);
		StateAssert.delOnlyEntering(status);

		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, bg_id);
		// 删除ARBill
		baseDao.deleteById("FA_Budget", "bg_id", bg_id);
		// 删除ARBillDetail
		// 记录操作
		baseDao.logger.delete(caller, "bg_id", bg_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, bg_id);
	}

	@Override
	public String[] printBudget(String caller, int bg_id, String reportName,
			String condition) {
		// 只能对状态为[未打印]的订单进行打印操作! 已打印的 也可继续打印
		// 执行打印前的其它逻辑
		handlerService.beforePrint(caller, bg_id);
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 记录操作
		baseDao.logger.print(caller, "bg_id", bg_id);
		// 执行打印后的其它逻辑
		handlerService.afterPrint(caller, bg_id);
		return keys;
	}

}
