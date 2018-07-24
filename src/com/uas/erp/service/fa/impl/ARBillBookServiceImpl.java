package com.uas.erp.service.fa.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.dao.BaseDao;

import com.uas.erp.service.fa.ARBillBookService;

@Service
public class ARBillBookServiceImpl implements ARBillBookService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveARBillBook(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.beforeSave(caller, new Object[] { store });
		// 保存ARBill
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ARBillBook",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		try {
			// 记录操作
			baseDao.logger.save(caller, "abb_id", store.get("abb_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		handlerService.afterSave(caller, new Object[] { store });
	}

	@Override
	public void updateARBillBookById(String formStore, String gridStore,
			String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store });
		// 修改ARBill
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ARBillBook",
				"abb_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "abb_id", store.get("abb_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store });
	}

	@Override
	public void deleteARBillBook(int abb_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, abb_id);
		// 删除ARBill
		baseDao.deleteById("ARBillBook", "abb_id", abb_id);
		// 记录操作
		baseDao.logger.delete(caller, "abb_id", abb_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, abb_id);
	}

	@Override
	public void printARBillBook(int abb_id, String caller) {
	}

	@Override
	public void auditARBillBook(int abb_id, String caller) {
	}

	@Override
	public void resAuditARBillBook(int abb_id, String caller) {
	}

	@Override
	public void submitARBillBook(int abb_id, String caller) {
	}

	@Override
	public void resSubmitARBillBook(int abb_id, String caller) {

	}
}
