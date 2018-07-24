package com.uas.erp.service.scm.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.service.scm.ProductLossService;

@Service
public class ProductLossServiceImpl implements ProductLossService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveProductLoss(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		//产品编号 pl_code +分段数(大于) pl_lapqty 都相同才需要限制保存、更新
		String code = store.get("pl_code").toString();
		baseDao.asserts.isFalse("ProductLoss", "pl_code='"+code+"'and nvl(pl_lapqty,'100000000')='"+StringUtil.nvl(store.get("pl_lapqty"),"100000000")+"'", "当前单据的物料前缀+分段数(大于)记录已存在，不允许保存!");
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store});
		// 保存AssistRequire
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "ProductLoss", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		baseDao.logger.save(caller, "pl_id", store.get("pl_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store});
	}

	@Override
	public void deleteProductLoss(int pl_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] {pl_id});
		// 删除AssistRequire
		baseDao.deleteById("ProductLoss", "pl_id", pl_id);
		// 记录操作
		baseDao.logger.delete(caller, "pl_id", pl_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] {pl_id});

	}

	@Override
	public void updateProductLoss(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前物料编号前缀的记录已经存在,不能修改!
		String code = store.get("pl_code").toString();
		baseDao.asserts.isFalse("ProductLoss", "pl_code='"+code+"'and nvl(pl_lapqty,'100000000')='"+StringUtil.nvl(store.get("pl_lapqty"),"100000000")+"' and pl_id<>" + store.get("pl_id"), "当前单据的物料前缀+分段数(大于)记录已存在，不允许更新!");
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] {store});
		// 修改KBIAssess
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "ProductLoss", "pl_id");
		baseDao.execute(formSql);
		// 记录操作
		baseDao.logger.update(caller, "pl_id", store.get("pl_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] {store});
	}
}
